package fr.epita.assistants.frontend;

import fr.epita.assistants.backend.domain.entity.Node;
import fr.epita.assistants.backend.domain.entity.Project;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static fr.epita.assistants.frontend.MyIdeApp.fileTreeView;
import static fr.epita.assistants.frontend.MyIdeApp.projectService;
import static fr.epita.assistants.frontend.Settings.saveSettings;


public class Open {

    protected static void openFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            Project project = projectService.load(selectedFile.toPath());
            populateFileTreeView(fileTreeView, project.getRootNode());
            saveSettings(selectedFile.getParent(), Settings.getSelectedTheme());
        }
    }

    protected static Project openProject(Stage primaryStage, TreeView<String> fileTreeView) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            Project project = projectService.load(selectedDirectory.toPath());
            populateFileTreeView(fileTreeView, project.getRootNode());
            saveSettings(selectedDirectory.getAbsolutePath(), Settings.getSelectedTheme());
            return project;
        }

        return null;
    }

    protected static void populateFileTreeView(TreeView<String> fileTreeView, Node node) {
        TreeItem<String> rootItem = new FilePathTreeItem(node);
        fileTreeView.setRoot(rootItem);
        populateTreeView(rootItem, node);
    }

    protected static void populateTreeView(TreeItem<String> parentItem, Node parentNode) {

        for (Node childNode : parentNode.getChildren()) {

            TreeItem<String> childItem = new FilePathTreeItem(childNode);
            parentItem.getChildren().add(childItem);

            if (childNode.getType() == Node.Types.FOLDER) {
                populateTreeView(childItem, childNode);
            }
        }
    }

}
