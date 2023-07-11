package fr.epita.assistants.backend.domain.entity;

import fr.epita.assistants.backend.utils.Given;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Given()
public interface Project {

    /**
     * @return The root node of the project.
     */
    @NotNull Node getRootNode();

    /**
     * @return The aspects of the project.
     */
    @NotNull Set<Aspect> getAspects();

    /**
     * Get an optional feature of the project depending
     * of its type. Returns an empty optional if the
     * project does not have the features queried.
     *
     * @param featureType Type of the feature to retrieve.
     * @return An optional feature of the project.
     */
    @NotNull Optional<Feature> getFeature(@NotNull final Feature.Type featureType);

    /**
     * @return The list of the project features.
     */
    default @NotNull List<@NotNull Feature> getFeatures() {
        return getAspects().stream().map(Aspect::getFeatureList).flatMap(Collection::stream).toList();
    }
}

