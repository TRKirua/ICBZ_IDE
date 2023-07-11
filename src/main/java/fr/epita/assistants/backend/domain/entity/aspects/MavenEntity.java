package fr.epita.assistants.backend.domain.entity.aspects;

import fr.epita.assistants.backend.domain.entity.Aspect;
import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.features.Package;
import fr.epita.assistants.backend.domain.entity.features.*;

import java.util.ArrayList;
import java.util.List;

public class MavenEntity implements Aspect {

    private List<Feature> features = new ArrayList<>();

    public MavenEntity() {
        features.add(new Compile());
        features.add(new Clean());
        features.add(new Test());
        features.add(new Package());
        features.add(new Install());
        features.add(new Exec());
        features.add(new Tree());
    }

    @Override
    public Type getType() {
        return Mandatory.Aspects.MAVEN;
    }

    @Override
    public List<Feature> getFeatureList() {
        return features;
    }
}
