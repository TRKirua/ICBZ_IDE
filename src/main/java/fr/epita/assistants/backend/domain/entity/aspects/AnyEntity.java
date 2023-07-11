package fr.epita.assistants.backend.domain.entity.aspects;

import fr.epita.assistants.backend.domain.entity.Aspect;
import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.features.CleanUp;
import fr.epita.assistants.backend.domain.entity.features.Dist;
import fr.epita.assistants.backend.domain.entity.features.Search;

import java.util.ArrayList;
import java.util.List;

public class AnyEntity implements Aspect {
    private List<Feature> features = new ArrayList<>();

    public AnyEntity() {
        features.add(new CleanUp());
        features.add(new Dist());
        features.add(new Search());
    }

    @Override
    public Type getType() {
        return Mandatory.Aspects.ANY;
    }

    @Override
    public List<Feature> getFeatureList() {
        return features;
    }
}
