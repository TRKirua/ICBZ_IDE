package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

import javax.validation.constraints.NotNull;
import java.io.File;

public class Push implements Feature {
    @Override
    public @NotNull Feature.ExecutionReport execute(final Project project, final Object... params) {
        return () -> {
            try {
                String repoPath = project.getRootNode().getPath().toString();

                try (Git git = Git.open(new File(repoPath))) {

                    Iterable<PushResult> results = git.push().call();

                    for (PushResult result : results) {
                        for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                            if (update.getStatus() == RemoteRefUpdate.Status.UP_TO_DATE) {
                                System.out.println("Failed to push. The repository is already up-to-date.");
                                return false;
                            }
                        }
                    }

                    System.out.println("Push successful");

                } catch (Exception e) {
                    System.out.println("Failed to push");
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
        return Mandatory.Features.Git.PUSH;
    }
}
