package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Node;
import fr.epita.assistants.backend.domain.entity.Project;
import fr.epita.assistants.backend.domain.service.MyNodeService;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class CleanUp implements Feature {

    private static void cleanup(Node rootNode, String[] toRemove, int len) {
        List<Node> children = rootNode.getChildren();

        for (int i = children.size() - 1; i >= 0; i--) {
            Node child = children.get(i);
            String childPath = child.getPath().getFileName().toString();

            if (childPath.equals(toRemove[0])) {

                if (len == 1) {
                    new MyNodeService().delete(child);
                }
                else {
                    cleanup(child, Arrays.copyOfRange(toRemove, 1, len), len - 1);
                }

                return;
            }
        }
    }

    @Override
    public @NotNull Feature.ExecutionReport execute(final Project project, final Object... params) {
        System.out.println("Cleanup test");
        return () -> {
            try {
                Path file = project.getRootNode().getPath().resolve(".myideignore");
                BufferedReader reader = new BufferedReader(new FileReader(file.toString()));

                String line;

                while ((line = reader.readLine()) != null) {
                    String[] toRemove = line.split("/");
                    cleanup(project.getRootNode(), toRemove, toRemove.length);
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        };
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.CLEANUP;
    }
}
