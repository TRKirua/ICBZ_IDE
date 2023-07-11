package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;
import org.eclipse.jgit.api.Git;

import javax.validation.constraints.NotNull;
import java.io.File;

public class Commit implements Feature {
    @Override
    public @NotNull Feature.ExecutionReport execute(final Project project, final Object... params) {
        return () -> {
            try {
                String repoPath = project.getRootNode().getPath().toString();
                try (Git git = Git.open(new File(repoPath))) {
                    if (params != null)
                    {
                        String commitMessage = params[0].toString();
                        git.commit().setMessage(commitMessage).call();
                        System.out.println("Commit successful: " + commitMessage);

                    }
                    else
                    {
                        String commitMessage = "fixing or adding";
                        git.commit().setMessage(commitMessage).call();
                        System.out.println("Commit successful: " + commitMessage);
                    }

                } catch (Exception e) {
                    System.out.println("Failed to commit");
                    e.printStackTrace();
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
            return true;
        };
    }


    @Override
    public Type type() {
        return Mandatory.Features.Git.COMMIT;
    }
}
