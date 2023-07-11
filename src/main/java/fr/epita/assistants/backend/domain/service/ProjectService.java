package fr.epita.assistants.backend.domain.service;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Project;
import fr.epita.assistants.backend.utils.Given;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;

/**
 * You will handle your projects through this service.
 */
@Given()
public interface ProjectService {

    /**
     * Load a {@link Project} from a path.
     *
     * @param root Path of the root of the project to load.
     * @return New project.
     */
    @NotNull Project load(@NotNull final Path root);


    /**
     * Execute the given feature on the given project.
     *
     * @param project     Project for which the features is executed.
     * @param featureType Type of the feature to execute.
     * @param params      Parameters given to the features.
     * @return Execution report of the feature.
     */
    @NotNull Feature.ExecutionReport execute(@NotNull final Project project,
                                             @NotNull final Feature.Type featureType,
                                             final Object... params);

    /**
     * @return The {@link NodeService} associated with your {@link ProjectService}
     */
    @NotNull NodeService getNodeService();
}
