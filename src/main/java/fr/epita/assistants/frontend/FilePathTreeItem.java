package fr.epita.assistants.frontend;

import fr.epita.assistants.backend.domain.entity.Node;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.Objects;

public class FilePathTreeItem extends TreeItem<String> {
    public static Image fileImage;
    public Image folderImage;
    private Node node;

    public FilePathTreeItem(Node node) {

        super(node.getPath().toString());
        this.node = node;
        fileImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/file.png")));
        folderImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/folder.png")));

        if (!node.isFile()) {
            this.setGraphic(new ImageView(folderImage));
        } else {
            this.setGraphic(new ImageView(fileImage));
        }

        if (!node.getPath().toString().endsWith(File.separator)) {
            String value = node.getPath().toString();
            int indexOf = value.lastIndexOf(File.separator);
            if (indexOf > 0) {
                this.setValue(value.substring(indexOf + 1));
            } else {
                this.setValue(value);
            }
        }

        this.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
            @Override
            public void handle(Event e) {
                FilePathTreeItem source = (FilePathTreeItem) e.getSource();
                if (!source.getNode().isFile() && source.isExpanded()) {
                    ImageView iv = (ImageView) source.getGraphic();
                    iv.setImage(folderImage);
                }
                if (source.getChildren().isEmpty()) {
                    Node node = source.getNode();
                    if (!node.isFile()) {
                        for (Node child : node.getChildren()) {
                            FilePathTreeItem treeNode = new FilePathTreeItem(child);
                            source.getChildren().add(treeNode);
                        }
                    }
                } else {
                    // implement rescanning a directory for changes
                }
            }
        });

        this.addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
            @Override
            public void handle(Event e) {
                FilePathTreeItem source = (FilePathTreeItem) e.getSource();
                if (!source.getNode().isFile() && !source.isExpanded()) {
                    ImageView iv = (ImageView) source.getGraphic();
                    iv.setImage(folderImage);
                }
            }
        });
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
