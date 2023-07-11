package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;

import javax.validation.constraints.NotNull;

public class Pull implements Feature {
    @Override
    public @NotNull Feature.ExecutionReport execute(final Project project, final Object... params) {
        System.setProperty("user.dir", project.getRootNode().getPath().toAbsolutePath().toString());
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current directory: " + currentDir);

        return () -> {
            try {
                var repoPath = project.getRootNode().getPath();
                System.out.println("Repository path: " + repoPath);
                try (Git git = Git.open(repoPath.toFile())) {

                    PullCommand pull = git.pull();
                    pull.setRemote("origin");
                    pull.setRemoteBranchName("master");
                    pull.call();

                    System.out.println("Pull operation completed successfully");
                }
            } catch (Exception e) {
                System.err.println("An error occurred while performing pull operation: " + e);
                return false;
            }
            return true;
        };
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.PULL;
    }
}
