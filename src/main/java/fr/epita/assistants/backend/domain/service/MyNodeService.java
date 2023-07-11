package fr.epita.assistants.backend.domain.service;

import fr.epita.assistants.backend.domain.entity.MyNode;
import fr.epita.assistants.backend.domain.entity.Node;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MyNodeService implements NodeService {

    @Override
    public Node update(Node node, int from, int to, byte[] insertedContent) {

        if (node.getType() == Node.Types.FOLDER)
            throw new IllegalArgumentException("Node is not a file");

        try (RandomAccessFile file = new RandomAccessFile(node.getPath().toFile(), "rw"))
        {
            byte[] currentContent = new byte[(int) file.length()];

            file.readFully(currentContent);

            if (to > currentContent.length) {
                to = currentContent.length;
            }

            byte[] newContent = new byte[from + insertedContent.length + (currentContent.length - to)];

            System.arraycopy(currentContent, 0, newContent, 0, from);
            System.arraycopy(insertedContent, 0, newContent, from, insertedContent.length);
            System.arraycopy(currentContent, to, newContent, from + insertedContent.length, currentContent.length - to);

            file.setLength(0);
            file.write(newContent);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return node;
    }

    @Override
    public boolean delete(Node node) {
        if (node.getType() == Node.Types.FILE) {
            Path filePath = node.getPath();

            try {
                Files.delete(filePath);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            Path folderPath = node.getPath();
            @NotNull List<Node> children = node.getChildren();

            try {
                for (Node child : children) {
                    delete(child);
                }

                node.getChildren().removeIf(n -> node.getPath() == n.getPath());
                Files.delete(folderPath);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

    @Override
    public Node create(Node folder, String name, Node.Type type) {
        if (folder.getType() == Node.Types.FOLDER) {
            Path folderPath = folder.getPath();
            Path newNodePath = folderPath.resolve(name);

            try {
                if (type == Node.Types.FILE) {
                    Files.createFile(newNodePath);
                } else if (type == Node.Types.FOLDER) {
                    Files.createDirectory(newNodePath);
                }

                return new MyNode(newNodePath);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    @Override
    public Node move(Node nodeToMove, Node destinationFolder) {
        if (nodeToMove.getType() == Node.Types.FILE) {
            Path sourceFilePath = nodeToMove.getPath();

            if (destinationFolder.getType() == Node.Types.FOLDER) {
                Path destinationFolderPath = destinationFolder.getPath();
                Path destinationFilePath = destinationFolderPath.resolve(sourceFilePath.getFileName());

                try {
                    Files.move(sourceFilePath, destinationFilePath);
                    return new MyNode(destinationFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                throw new IllegalArgumentException("Destination node is not a folder");
            }
        } else if (nodeToMove.getType() == Node.Types.FOLDER) {
            Path sourceFolderPath = nodeToMove.getPath();

            if (destinationFolder.getType() == Node.Types.FOLDER) {
                Path destinationFolderPath = destinationFolder.getPath().resolve(sourceFolderPath.getFileName());

                try {
                    Files.createDirectory(destinationFolderPath);

                    List<Node> children = nodeToMove.getChildren();

                    for (Node child : children) {
                        move(child, new MyNode(destinationFolderPath));
                    }

                    delete(nodeToMove);

                    return new MyNode(destinationFolderPath);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                throw new IllegalArgumentException("Destination node is not a folder");
            }
        } else {
            throw new IllegalArgumentException("Node type not supported for move");
        }
    }
}
