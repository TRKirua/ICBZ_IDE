package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;
import org.eclipse.jgit.api.Git;

import javax.validation.constraints.NotNull;
import java.io.File;

public class Add implements Feature {
    @Override
    public @NotNull Feature.ExecutionReport execute(final Project project, final Object... params) {
        return () -> {
            try {
                String repoPath = project.getRootNode().getPath().toString();
                System.out.println("Running git add");

                if (params.length == 0) {
                    System.out.println("No files or directories passed as parameters.");
                    return false;
                }

                try (Git git = Git.open(new File(repoPath))) {
                    // Check if all files or directories exist
                    for (int i = 0; i < params.length; i++) {
                        String pathToAdd = params[i].toString();
                        String fullPath = repoPath + "/" + pathToAdd;
                        File checkPath = new File(fullPath);
                        if (!checkPath.exists()) {
                            System.out.println("Path does not exist: " + fullPath);
                            return false;
                        }
                    }

                    // Add each file or directory specified in the parameters
                    for (int i = 0; i < params.length; i++) {
                        String pathToAdd = params[i].toString();
                        try {
                            git.add().addFilepattern(pathToAdd).call();
                            System.out.println("Path added successfully: " + pathToAdd);
                        } catch (Exception e) {
                            System.out.println("Failed to add path: " + pathToAdd);
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        };
    }




    @Override
    public Type type() {
        return Mandatory.Features.Git.ADD;
    }
}