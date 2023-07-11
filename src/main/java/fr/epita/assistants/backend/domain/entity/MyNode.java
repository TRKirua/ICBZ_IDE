package fr.epita.assistants.backend.domain.entity;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MyNode implements Node {

    private Path path;
    private Type type;
    private List<Node> children = new ArrayList<>();

    public MyNode(Path path) {

        this.path = path;

        if (Files.isDirectory(path)) {
            this.type = Types.FOLDER;

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path subPath : stream) {
                    children.add(new MyNode(subPath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.type = Types.FILE;
        }
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<@NotNull Node> getChildren() {
        return children;
    }
}