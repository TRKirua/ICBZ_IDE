package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Node;
import fr.epita.assistants.backend.domain.entity.Project;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.List;

public class Search implements Feature {

    @Override
    public @NotNull Feature.ExecutionReport execute(final Project project, final Object... params) {
        System.out.println("Search test");
        return () -> {
            Node rootNode = project.getRootNode();
            String searchString = (String) params[0];

            return search(rootNode, searchString);
        };
    }

    private boolean search(Node rootNode, String searchString) {
        List<Node> children = rootNode.getChildren();

        for (Node child : children) {
            if (child.isFile()) {
                if (searchInFile(child.getPath().toFile(), searchString)) {
                    return true;
                }
            }

            else {
                if (search(child, searchString)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean searchInFile(File file, String searchString) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(searchString.toLowerCase())) {
                    System.out.println("Found in : " + file.getPath());
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return false;
        } catch (IOException e) {
            System.out.println("Error");
            return false;
        }

        return false;
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.SEARCH;
    }
}
