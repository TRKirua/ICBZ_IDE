package fr.epita.assistants.frontend;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.backend.domain.entity.Project;
import fr.epita.assistants.backend.domain.service.ProjectService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static fr.epita.assistants.frontend.MenuFeatures.*;
import static fr.epita.assistants.frontend.Open.*;
import static fr.epita.assistants.frontend.Themes.applyTheme;
import static fr.epita.assistants.frontend.gitFunctions.*;


public class MyIdeApp extends Application {

    // Project Service
    protected static ProjectService projectService;

    // Menu Bar
    protected static MenuBar menuBar;
    protected static Menu fileMenu;
    protected static Menu gitMenu;

    // Themes Bar
    protected static MenuBar themesBar;
    protected static Menu themes;

    // Compile Bar
    protected static MenuBar compileBar;
    protected static Menu compile;

    // Buttons
    protected static Button saveButton;

    // BorderPane
    protected static BorderPane root;
    protected static BorderPane topBox;
    protected static BorderPane mainBox;

    // SplitPane
    protected static SplitPane mainAndBuild;

    // Elements
    protected static TreeView<String> fileTreeView;
    protected static RSyntaxTextArea textArea;
    protected static TabPane tabPane;
    protected static RTextScrollPane sp;
    protected static SwingNode swingNode;
    protected static TextArea buildArea;

    // Boxes
    protected static HBox topLeft;
    protected static HBox topRight;

    // Screen details
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();

    // Current File
    protected static Path currentFile;
    protected static Project lastOpenProject;

    // Saved theme
    protected static String savedTheme;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Initialize the backend
        projectService = MyIde.init(new MyIde.Configuration(Path.of("indexFile"), Path.of("tempFolder")));

        // Create components
        createUICompenents();
        createAllBars(primaryStage);

        // Load saved settings
        String savedProjectPath = Settings.getProjectPath();
        savedTheme = Settings.getSelectedTheme();

        // Create the scene and show the stage
        Scene scene = new Scene(root, screenWidth * 0.8, screenHeight * 0.8);

        scene.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S)
            {
                saveFile();
                SaveEffect();
                AudioSound("sounds/Save.wav");
                event.consume();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("My IDE");
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Open the saved project
        if (savedProjectPath != null) {
            File savedProjectDirectory = new File(savedProjectPath);
            if (savedProjectDirectory.exists()) {
                Project project = projectService.load(savedProjectDirectory.toPath());
                lastOpenProject = project;
                populateFileTreeView(fileTreeView, project.getRootNode());
            }
        }

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        fileTreeView.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {

            if (newValue != null) {

                FilePathTreeItem item = (FilePathTreeItem) newValue;

                if (item.getNode().isFile()) {

                    StringBuilder contentBuilder = new StringBuilder();
                    Path openFile = item.getNode().getPath();

                    int size = tabPane.getTabs().size();
                    boolean fileAlreadyOpened = false;

                    int i;
                    for (i = 0; i < size; i++) {

                        Tab tab = tabPane.getTabs().get(i);
                        if (tab.getText().equals(openFile.getFileName().toString())) {
                            fileAlreadyOpened = true;
                            break;
                        }
                    }

                    if (!fileAlreadyOpened) {

                        RSyntaxTextArea tabTextArea = new RSyntaxTextArea();
                        tabTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        tabTextArea.setCodeFoldingEnabled(true);
                        tabTextArea.setAntiAliasingEnabled(true);

                        RTextScrollPane tabScrollPane = new RTextScrollPane(tabTextArea);
                        tabScrollPane.setLineNumbersEnabled(true);
                        tabScrollPane.getGutter().setSpacingBetweenLineNumbersAndFoldIndicator(10);

                        SwingNode tabSwingNode = new SwingNode();

                        SwingUtilities.invokeLater(() -> {
                            tabSwingNode.setContent(tabScrollPane);
                            setupAutoCompletion(tabTextArea);
                        });

                        Tab newTab = new Tab(openFile.getFileName().toString(), tabSwingNode);
                        newTab.getProperties().put("filePath", openFile);
                        tabPane.getTabs().add(newTab);

                        tabPane.getSelectionModel().select(newTab);

                        try (BufferedReader br = new BufferedReader(new FileReader(openFile.toString()))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                contentBuilder.append(line).append("\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            SwingNode swingNode = (SwingNode) newTab.getContent();
                            RTextScrollPane scrollPane = (RTextScrollPane) swingNode.getContent();
                            RSyntaxTextArea textArea = (RSyntaxTextArea) scrollPane.getTextArea();
                            textArea.setText(contentBuilder.toString());
                        } catch (Exception ignored) {}

                        applyTheme(savedTheme, 1);

                    }

                    else {
                        tabPane.getSelectionModel().select(tabPane.getTabs().get(i));
                        currentFile = (Path) tabPane.getTabs().get(i).getProperties().get("Path");
                    }

                }
            }
        });

        // Apply default or saved theme
        applyTheme(savedTheme, 1);

    }

    // Setting up autocompletion
    public void setupAutoCompletion(RSyntaxTextArea textArea) {

        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new CustomAutoCompletion(provider);
        SwingUtilities.invokeLater(() -> {
            ac.install(textArea);
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                CompletableFuture.runAsync(ac::doCompletion);
            }


            @Override
            public void removeUpdate(DocumentEvent e) {
                // do nothing on deletion
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // plain text components do not fire these events
            }
        });
    }

    public static class CustomAutoCompletion extends AutoCompletion {

        public CustomAutoCompletion(CompletionProvider provider) {
            super(provider);
        }

        @Override
        protected void insertCompletion(Completion c, boolean typedParamListStartChar) {
            super.insertCompletion(c, typedParamListStartChar);

            // Get the text from the completion
            String completionText = c.getReplacementText();

            // Check if it contains the placeholder symbol
            int index = completionText.indexOf("$");

            if (index != -1) {
                RSyntaxTextArea textArea = (RSyntaxTextArea) getTextComponent();

                // Adjust the caret position to the placeholder position
                int newPosition = textArea.getCaretPosition() - (completionText.length() - index);
                textArea.setCaretPosition(newPosition);

                // Remove the placeholder symbol
                try {
                    textArea.getDocument().remove(newPosition, 1);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.setAutoActivationRules(true, "");

        // Array of all Java keywords
        String[] keywords = {
                "abstract", "assert", "boolean", "break", "byte", "case", "char", "class",
                "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
                "finally", "float", "if", "implements", "import", "instanceof", "int", "interface",
                "long", "native", "new", "package", "private", "protected", "public", "return", "short",
                "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "void", "volatile"
        };

        // Add completions for all Java keywords
        for (String keyword : keywords) {
            provider.addCompletion(new BasicCompletion(provider, keyword));
        }

        // Add a completion for the for loop template
        // Add completions for loop and try-catch templates
        provider.addCompletion(new ShorthandCompletion(provider, "for",
                "for (int i = 0; i < size; i++) {\n\t$\n}"));
        provider.addCompletion(new ShorthandCompletion(provider, "while",
                "while (condition) {\n\t$\n}"));
        provider.addCompletion(new ShorthandCompletion(provider, "try",
                "try {\n\t$\n} catch (Exception e) {\n\t\n}"));
        provider.addCompletion(new ShorthandCompletion(provider, "singleton",
                """
                        public class Singleton {

                        \tprivate static Singleton instance;

                        \tprivate Singleton() {
                        \t\t// TODO: Initialize the singleton
                        \t}

                        \tpublic static Singleton getInstance() {
                        \t\tif (instance == null) {
                        \t\t\t// TODO: Create a new instance if it doesn't exist
                        \t\t\tinstance = new Singleton();
                        \t\t}
                        \t\treturn instance;
                        \t}

                        \t// TODO: Add other methods and properties

                        }"""));

        provider.addCompletion(new ShorthandCompletion(provider, "observer",
                """
                        // Observer Interface
                        interface Observer {
                        \tvoid update(String message);
                        }

                        // Concrete Observer
                        class ConcreteObserver implements Observer {
                        \t@Override
                        \tpublic void update(String message) {
                        \t\t// TODO: Implement the update method
                        \t}
                        }

                        // Subject
                        class Subject {
                        \tprivate List<Observer> observers = new ArrayList<>();

                        \tpublic void addObserver(Observer observer) {
                        \t\tobservers.add(observer);
                        \t}

                        \tpublic void removeObserver(Observer observer) {
                        \t\tobservers.remove(observer);
                        \t}

                        \tpublic void notifyObservers(String message) {
                        \t\tfor (Observer observer : observers) {
                        \t\t\tobserver.update(message);
                        \t\t}
                        \t}

                        \t// TODO: Add other methods and properties
                        }
                        """));

        provider.addCompletion(new ShorthandCompletion(provider, "factory",
                """
                        // Product Interface
                        interface Product {
                        \tvoid operation();
                        }

                        // Concrete Product
                        class ConcreteProduct implements Product {
                        \t@Override
                        \tpublic void operation() {
                        \t\t// TODO: Implement the operation method
                        \t}
                        }

                        // Factory Interface
                        interface Factory {
                        \tProduct createProduct();
                        }

                        // Concrete Factory
                        class ConcreteFactory implements Factory {
                        \t@Override
                        \tpublic Product createProduct() {
                        \t\treturn new ConcreteProduct();
                        \t}
                        }

                        // Client
                        class Client {
                        \tprivate Product product;

                        \tpublic Client(Factory factory) {
                        \t\tproduct = factory.createProduct();
                        \t}

                        \tpublic void executeOperation() {
                        \t\tproduct.operation();
                        \t}
                        }
                        """));

        return provider;
    }

    private void createUICompenents() {

        // Create the main UI components
        root = new BorderPane();
        topBox = new BorderPane();
        mainBox = new BorderPane();

        fileTreeView = new TreeView<>();
        fileTreeView.setPrefWidth(screenWidth / 5);
        fileTreeView.setMinWidth(screenWidth / 5);
        fileTreeView.setMaxWidth(screenWidth / 5);

        textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);

        sp = new RTextScrollPane(textArea);
        sp.setLineNumbersEnabled(true);

        swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> swingNode.setContent(sp));

        buildArea = new TextArea();
        buildArea.setEditable(false);

        // Create tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        // Transition des tabs
        TransitionTab(tabPane);

        // Create an image
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/WelcomeIDE.png")));
        ImageView imageView = new ImageView(image);

        // Set size of image
        imageView.setFitHeight(screenHeight / 1.8);
        imageView.setFitWidth(screenWidth / 1.8);

        // Create a label and set the image view as its graphic
        Label label = new Label();
        label.setGraphic(imageView);

        // Create a stack pane and add the label to center the image
        StackPane stackPane = new StackPane(label);

        // Create a tab and set the stack pane as its content
        Tab tab = new Tab("Welcome to ICBZ IDE !", stackPane);
        tabPane.getTabs().add(tab);

        // Set an event listener for tab selection changes
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                currentFile = (Path) newTab.getProperties().get("filePath");
            }
        });

    }

    private void createAllBars(Stage primaryStage) {

        // Create Save Button
        saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            saveFile();
            SaveEffect();
            AudioSound("sounds/Save.wav");
        });

        // Create bars
        createMenuBar(primaryStage);
        createThemesBar();
        createCompileBar(primaryStage);

        // Create Top Left Box
        topLeft = new HBox(menuBar, saveButton);
        topLeft.setAlignment(Pos.CENTER_RIGHT);
        topLeft.setSpacing(10);
        topLeft.setPadding(new Insets(10));

        // Create Top Right Box
        topRight = new HBox(compileBar, themesBar);
        topRight.setAlignment(Pos.CENTER_RIGHT);
        topRight.setSpacing(10);
        topRight.setPadding(new Insets(10));

        // Set top box placement
        topBox.setLeft(topLeft);
        topBox.setRight(topRight);

        // Set main box placement
        mainBox.setCenter(tabPane);
        mainBox.setBottom(buildArea);

        // Create SplitPane between mainBox and buildArea
        mainAndBuild = new SplitPane();
        mainAndBuild.setOrientation(Orientation.VERTICAL);
        mainAndBuild.getItems().addAll(mainBox, buildArea);
        mainAndBuild.setDividerPositions(0.8);

        // Set root placement
        root.setTop(topBox);
        root.setCenter(mainAndBuild);
        root.setLeft(fileTreeView);

    }

    private void createMenuBar(Stage primaryStage) {

        // Create "File" menu
        MenuItem openFileMenuItem = new MenuItem("Open File");
        MenuItem openProjectMenuItem = new MenuItem("Open Project");

        openFileMenuItem.setOnAction(event -> openFile(primaryStage));
        openProjectMenuItem.setOnAction(event -> {
            lastOpenProject =  openProject(primaryStage, fileTreeView);
        });

        fileMenu = new Menu("File");
        fileMenu.getItems().addAll(openFileMenuItem, openProjectMenuItem);

        // Create "Git" Menu
        MenuItem gitAddMenuItem = new MenuItem("Add");
        MenuItem gitCommitMenuItem = new MenuItem("Commit");
        MenuItem gitPushMenuItem = new MenuItem("Push");
        MenuItem gitPullMenuItem = new MenuItem("Pull");

        gitAddMenuItem.setOnAction(event -> gitAdd(lastOpenProject));
        gitCommitMenuItem.setOnAction(event -> gitCommit(lastOpenProject));
        gitPushMenuItem.setOnAction(event -> gitPush(lastOpenProject));
        gitPullMenuItem.setOnAction(event ->  {
            lastOpenProject =  gitPull(lastOpenProject, fileTreeView);
        });

        gitMenu = new Menu("Git");
        gitMenu.getItems().addAll(gitAddMenuItem, gitCommitMenuItem, gitPushMenuItem, gitPullMenuItem);

        // Create menu bar
        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, gitMenu);

    }

    private void createThemesBar() {

        // Create buttons as menu items
        MenuItem SciFiTheme = new MenuItem("Sci/Fi Theme");
        MenuItem SoftSciFiTheme = new MenuItem("Soft Sci/Fi Theme");
        MenuItem AggressiveSciFiTheme = new MenuItem("Aggressive Sci/Fi Theme");
        MenuItem PeacefulSciFiTheme = new MenuItem("Peaceful Sci/Fi Theme");
        MenuItem CyberpunkTheme = new MenuItem("Cyberpunk Theme");
        MenuItem ClassicWhiteTheme = new MenuItem("Classic White Theme");
        MenuItem ClassicDarkTheme = new MenuItem("Classic Dark Theme");
        MenuItem RandomTheme = new MenuItem("Random Theme");

        // Event handlers for menu items
        SciFiTheme.setOnAction(event -> applyTheme("Sci/Fi Theme", 0));
        SoftSciFiTheme.setOnAction(event -> applyTheme("Soft Sci/Fi Theme", 0));
        AggressiveSciFiTheme.setOnAction(event -> applyTheme("Aggressive Sci/Fi Theme", 0));
        PeacefulSciFiTheme.setOnAction(event -> applyTheme("Peaceful Sci/Fi Theme", 0));
        CyberpunkTheme.setOnAction(event -> applyTheme("Cyberpunk Theme", 0));
        ClassicWhiteTheme.setOnAction(event -> applyTheme("Classic White Theme", 0));
        ClassicDarkTheme.setOnAction(event -> applyTheme("Classic Dark Theme", 0));
        RandomTheme.setOnAction(event -> applyTheme("Random Theme", 0));

        // Create themes container
        themes = new Menu("Themes");
        themes.getItems().addAll(SciFiTheme, SoftSciFiTheme, AggressiveSciFiTheme, PeacefulSciFiTheme, CyberpunkTheme, ClassicWhiteTheme, ClassicDarkTheme, RandomTheme);

        // Create themes bar
        themesBar = new MenuBar();
        themesBar.getMenus().add(themes);

    }

    private void createCompileBar(Stage primaryStage) {

        // Create buttons as menu items
        MenuItem CompileFile = new MenuItem("Run code");
        MenuItem CompileProject = new MenuItem("Run project");

        // Event handlers for menu items
        CompileFile.setOnAction(event -> RunFile(primaryStage));
        CompileProject.setOnAction(event -> RunProject());

        // Create compile container
        compile = new Menu("Compile");
        compile.getItems().addAll(CompileFile, CompileProject);

        // Create compile bar
        compileBar = new MenuBar();
        compileBar.getMenus().add(compile);

    }

}