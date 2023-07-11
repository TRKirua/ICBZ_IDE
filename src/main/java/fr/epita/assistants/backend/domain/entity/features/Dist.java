package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Node;
import fr.epita.assistants.backend.domain.entity.Project;
import fr.epita.assistants.backend.domain.service.MyNodeService;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Dist implements Feature {

    private static void dist(Node rootNode, String[] toRemove, int len) {
        List<Node> children = rootNode.getChildren();

        for (int i = children.size() - 1; i >= 0; i--) {
            Node child = children.get(i);
            String childPath = child.getPath().getFileName().toString();

            if (childPath.equals(toRemove[0])) {

                if (len == 1) {
                    new MyNodeService().delete(child);
                    children.remove(child);
                }
                else {
                    dist(child, Arrays.copyOfRange(toRemove, 1, len), len - 1);
                }

                return;
            }
        }
    }

    @Override
    public ExecutionReport execute(Project project, Object... params)
    {
        System.out.println("Dist test");
        return () -> {
            try {
                Path file = project.getRootNode().getPath().resolve(".myideignore");
                BufferedReader reader = new BufferedReader(new FileReader(file.toString()));

                String line;

                while ((line = reader.readLine()) != null) {
                    String[] toRemove = line.split("/");
                    dist(project.getRootNode(), toRemove, toRemove.length);
                }
            } catch (Exception e) {
                return false;
            }

            Path rootPath = project.getRootNode().getPath().getParent();
            String zipFileName = project.getRootNode().getPath().getFileName().toString() + ".zip";
            Path zipFilePath = rootPath.resolve(zipFileName);

            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile())))
            {
                List<Node> nodes = new ArrayList<>();
                collectNodes(project.getRootNode(), nodes);

                for (Node node : nodes)
                {
                    if (node.getType() == Node.Types.FILE)
                        addFileToZip(rootPath, node.getPath(), zos);
                }

                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
        };
    }

    private void collectNodes(Node node, List<Node> nodes)
    {
        nodes.add(node);

        for (Node child : node.getChildren()) {
            collectNodes(child, nodes);
        }
    }

    private void addFileToZip(Path rootPath, Path filePath, ZipOutputStream zos) throws IOException
    {
        Path relativePath = rootPath.relativize(filePath);
        ZipEntry zipEntry = new ZipEntry(relativePath.toString());
        zos.putNextEntry(zipEntry);

        Files.copy(filePath, zos);

        zos.closeEntry();
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.DIST;
    }
}
