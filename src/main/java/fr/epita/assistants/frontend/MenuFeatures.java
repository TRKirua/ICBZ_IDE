package fr.epita.assistants.frontend;

import javafx.animation.*;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.tools.*;
import java.io.*;
import javafx.scene.paint.Color;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static fr.epita.assistants.frontend.MyIdeApp.*;
import static fr.epita.assistants.frontend.Themes.*;
import static fr.epita.assistants.frontend.Arts.*;

public class MenuFeatures {

    protected static int countCompileLogo = 0;

    protected static void saveFile() {

        StringBuilder stringBuilder = new StringBuilder();

        for (Tab tab : tabPane.getTabs()) {

            if (!Objects.equals(tab.getText(), "Welcome to ICBZ IDE !")) {

                try {

                    SwingNode sw = (SwingNode) tab.getContent();
                    RTextScrollPane rt = (RTextScrollPane) sw.getContent();
                    RSyntaxTextArea ra = (RSyntaxTextArea) rt.getTextArea();
                    String fileContent = ra.getText();

                    BufferedWriter bf = new BufferedWriter(new FileWriter(((Path) tab.getProperties().get("filePath")).toFile()));
                    bf.write(fileContent);
                    bf.flush();
                    bf.close();

                    stringBuilder.setLength(0);
                    Path path = (Path) tab.getProperties().get("filePath");

                    try (BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        ra.setText(stringBuilder.toString());
                    } catch (Exception ignored) {
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    protected static void RunFile(Stage primaryStage) {

        buildArea.setScrollTop(0);

        saveFile();
        buildArea.clear();

        if (tabPane.getTabs().size() == 0) {
            AudioSound("sounds/CompileNoFile.wav");
            buildArea.setText("Compilation cannot be performed.\nNo files are currently open.\n" + Paimon);
            return;
        }

        Path filePath = currentFile;

        if (filePath == null) {

            if (countCompileLogo < 4) {
                AudioSound("sounds/Compile_Logo.wav");
            }

            if (countCompileLogo == 0) {
                buildArea.setText("Huh ??\nDid you really try to compile our logo ?\nTry to not be as dumb as this the next time you want to compile something.\n" + Shinobu);
            }

            else if (countCompileLogo == 1) {
                buildArea.setText("Bro why are you compiling the logo bruhhh.\nGo code something instead of making me lose my time.\n" + Nagatoro);
            }

            else if (countCompileLogo == 2) {
                buildArea.setText("Bro think he's funny.\nDo you have that much time to waste ? Stop it and go touch some grass ...\n" + Itachi);
            }

            else if (countCompileLogo == 3) {
                buildArea.setText("Okay I'm done, this feature is now disable. Bye.\n" + Bored);
            }

            else {
                buildArea.setText("...\n" + Ganyu);
            }

            countCompileLogo++;
            return;
        }

        if (!filePath.toString().endsWith(".java")) {
            AudioSound("sounds/CompileNotJava.wav");
            NotJavaEffect(primaryStage);
            buildArea.setText("Compilation cannot be performed.\nThe file is not a Java file.\n\n" + Imposter);
            return;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        File file = new File(filePath.toUri());

        try {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(file);

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

            buildArea.appendText("Running code...\n\n");
            boolean success = task.call();

            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                buildArea.appendText("Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource().toUri() + "\n");
            }

            if (success) {
                Process process = Runtime.getRuntime().exec("java " + filePath);

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null)
                    buildArea.appendText(line + "\n");

                process.waitFor();
                AudioSound("sounds/CompileSuccess.wav");
                successEffect();
                buildArea.appendText("\nProcess finished with exit code 0\n" + ZeroTwo);
            }
            else
            {
                AudioSound("sounds/CompileError.wav");
                glitchEffect();
                buildArea.appendText("\nCompilation failed. Code execution aborted.\n" + Cat);
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally
        {
            try
            {
                fileManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void RunProject() {
        buildArea.clear();

        if (lastOpenProject != null)
        {
            buildArea.appendText("Running code...\n\n");
            try {
                String args = "";

                String command = "mvn clean install && mvn exec:java" + args;
                Process process = Runtime.getRuntime().exec(command, null,
                        lastOpenProject.getRootNode().getPath().toFile());

                // Capture the output of the process
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String output = reader.lines().collect(Collectors.joining(System.lineSeparator()));

                int exitCode = process.waitFor();

                buildArea.appendText(output + "\n\n");

                if (exitCode != 0) {
                    System.out.println("Execution failed. Exit code: " + exitCode);
                    buildArea.appendText("\nExecution failed. Exit code: " + exitCode + "\n\n" + Cat);
                } else {
                    System.out.println("Succeed to execute Maven project.");
                    buildArea.appendText("\nProcess finished with exit code 0\n\n" + ZeroTwo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // currentProject.getFeature(Mandatory.Features.Maven.EXEC).get().execute(currentProject);

        } else {
            buildArea.appendText("Please load a project before trying to run.");
        }

    }


    public static void AudioSound(String audioFilePath)
    {
        try {
            File audioFile = new File(audioFilePath);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(audioFile));
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    private static void glitchEffect()
    {
        Timeline timeline = new Timeline();

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0.0);
        colorAdjust.setSaturation(1.0);
        colorAdjust.setBrightness(0.0);
        colorAdjust.setContrast(1.0);

        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(0), new KeyValue(root.opacityProperty(), 1.0), new KeyValue(root.effectProperty(), null));
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), new KeyValue(root.opacityProperty(), 0.0), new KeyValue(root.effectProperty(), colorAdjust));
        KeyFrame keyFrame3 = new KeyFrame(Duration.millis(600), new KeyValue(root.opacityProperty(), 1.0), new KeyValue(root.effectProperty(), null));

        timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3);
        timeline.setCycleCount(3);
        timeline.play();
    }

    private static void successEffect() {

        Timeline timeline = new Timeline();

        Blend blend = new Blend();
        blend.setMode(BlendMode.ADD);

        GaussianBlur gaussianBlur = new GaussianBlur(0);
        blend.setTopInput(gaussianBlur);

        Lighting lighting = new Lighting();
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0);
        lighting.setLight(light);
        lighting.setSurfaceScale(2.0);
        blend.setBottomInput(lighting);

        KeyValue keyValue1 = new KeyValue(gaussianBlur.radiusProperty(), 0);
        KeyValue keyValue2 = new KeyValue(gaussianBlur.radiusProperty(), 30);
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(0), keyValue1);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);
        KeyFrame keyFrame3 = new KeyFrame(Duration.millis(1000), keyValue1);

        KeyValue keyValue3 = new KeyValue(light.colorProperty(), Color.GHOSTWHITE);
        KeyValue keyValue4 = new KeyValue(light.colorProperty(), Color.LIGHTGREEN);
        KeyFrame keyFrame4 = new KeyFrame(Duration.millis(0), keyValue3);
        KeyFrame keyFrame5 = new KeyFrame(Duration.millis(500), keyValue4);
        KeyFrame keyFrame6 = new KeyFrame(Duration.millis(1000), keyValue3);

        timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3, keyFrame4, keyFrame5, keyFrame6);
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        root.setEffect(blend);

        timeline.setOnFinished(event -> {
            root.setEffect(null);
        });

        timeline.play();
    }

    static void SaveEffect() {
        String originalStyle = saveButton.getStyle();

        saveButton.setStyle(String.format("-fx-background-color: white; -fx-control-inner-background: white; -fx-text-fill: black; -fx-font-family: '%s';", actualFont));

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            saveButton.setStyle(originalStyle);
        }));

        timeline.play();
    }

    static void TransitionTab(TabPane tabPane)
    {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {

                // Fade animation
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), newTab.getContent());
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);

                // Tremor animation
                TranslateTransition shakeTransition = new TranslateTransition(Duration.millis(100), newTab.getContent());
                shakeTransition.setFromX(-5);
                shakeTransition.setToX(5);
                shakeTransition.setInterpolator(Interpolator.LINEAR);
                shakeTransition.setAutoReverse(true);
                shakeTransition.setCycleCount(5);

                // Blue filter
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setSaturation(-1);

                // Apply blue filter with blend
                Blend blend = new Blend();
                blend.setMode(BlendMode.OVERLAY);
                blend.setBottomInput(newTab.getContent().getEffect());
                blend.setTopInput(colorAdjust);

                // Apply blend to tab
                newTab.getContent().setEffect(blend);

                // Create parallel transition for animation
                ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, shakeTransition);
                parallelTransition.setOnFinished(event -> {
                    // Delete filter after animation
                    newTab.getContent().setEffect(null);
                });
                parallelTransition.play();
            }
        });
    }

    private static void NotJavaEffect(Stage primaryStage) {

        applyImposter();

        // Change buttons into SUS
        compile.setText("SUS");
        saveButton.setText("SUS");
        fileMenu.setText("SUS");
        gitMenu.setText("SUS");
        themes.setText("SUS");
        primaryStage.setTitle("SUS");

        String originalFileContent;
        if (currentFile != null) {
            originalFileContent = readFileContent(currentFile);
        } else {
            originalFileContent = "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n\n\t\t$$$$$    $    $    $$$$\n");
        sb.append("\t\t$        $    $    $\n");
        sb.append("\t\t$$$$$    $    $    $$$$\n");
        sb.append("\t\t    $    $    $       $\n");
        sb.append("\t\t$$$$$    $$$$$$    $$$$\n");

        if (currentFile != null) {
            boolean writeSuccess = writeFileContent(currentFile, sb.toString());
            if (writeSuccess) {
                SwingNode swingNode = (SwingNode) tabPane.getSelectionModel().getSelectedItem().getContent();
                RTextScrollPane scrollPane = (RTextScrollPane) swingNode.getContent();
                RSyntaxTextArea textArea = (RSyntaxTextArea) scrollPane.getTextArea();
                textArea.setText(sb.toString());
            }
        }

        List<String> originalChildNames = new ArrayList<>();
        for (TreeItem<String> item : fileTreeView.getRoot().getChildren()) {
            originalChildNames.add(item.getValue());
        }

        String RootFileName = fileTreeView.getRoot().getValue();
        fileTreeView.getRoot().setValue("SUS");

        for (TreeItem<String> item : fileTreeView.getRoot().getChildren()) {
            item.setValue("SUS");
        }

        // Create an animation for 1sec
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            applyTheme(savedTheme, 1);
            compile.setText("Compile");
            saveButton.setText("Save");
            fileMenu.setText("File");
            gitMenu.setText("Git");
            themes.setText("Themes");
            primaryStage.setTitle("My IDE");

            fileTreeView.getRoot().setValue(RootFileName);

            for (int i = 0; i < fileTreeView.getRoot().getChildren().size(); i++) {
                TreeItem<String> item = fileTreeView.getRoot().getChildren().get(i);
                item.setValue(originalChildNames.get(i));
            }

            if (currentFile != null) {
                boolean writeSuccess = writeFileContent(currentFile, originalFileContent);
                if (writeSuccess) {
                    SwingNode swingNode = (SwingNode) tabPane.getSelectionModel().getSelectedItem().getContent();
                    RTextScrollPane scrollPane = (RTextScrollPane) swingNode.getContent();
                    RSyntaxTextArea textArea = (RSyntaxTextArea) scrollPane.getTextArea();
                    textArea.setText(originalFileContent);
                }
            }
        });

        // Start animation
        pause.play();
    }

    private static boolean writeFileContent(Path filePath, String content) {
        try {
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String readFileContent(Path filePath) {
        try {
            byte[] encodedBytes = Files.readAllBytes(filePath);
            return new String(encodedBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
