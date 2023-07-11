package fr.epita.assistants.backend.domain.entity.aspects;

import fr.epita.assistants.backend.domain.entity.Aspect;
import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.features.Add;
import fr.epita.assistants.backend.domain.entity.features.Commit;
import fr.epita.assistants.backend.domain.entity.features.Pull;
import fr.epita.assistants.backend.domain.entity.features.Push;

import java.util.ArrayList;
import java.util.List;

public class GitEntity implements Aspect {
    private List<Feature> features = new ArrayList<>();

    public GitEntity() {
        features.add(new Pull());
        features.add(new Add());
        features.add(new Push());
        features.add(new Commit());
    }

    @Override
    public Type getType() {
        return Mandatory.Aspects.GIT;
    }

    @Override
    public List<Feature> getFeatureList() {
        return features;
    }
}
