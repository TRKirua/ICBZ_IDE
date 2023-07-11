package fr.epita.assistants.backend.domain.entity;

import fr.epita.assistants.backend.domain.entity.aspects.AnyEntity;
import fr.epita.assistants.backend.domain.entity.aspects.GitEntity;
import fr.epita.assistants.backend.domain.entity.aspects.MavenEntity;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MyProject implements Project {

    private MyNode rootNode;
    private Set<Aspect> aspects;
    private Set<Feature> features;

    public MyProject(Path path) {
        this.rootNode = new MyNode(path);
        aspects = new HashSet<>();
        features = new HashSet<>();

        AnyEntity anyEntity = new AnyEntity();
        aspects.add(anyEntity);
        features.addAll(anyEntity.getFeatureList());

        boolean isMavenProject = false;
        for (Node node : rootNode.getChildren()) {
            if (node.getType() == Node.Types.FILE && node.getPath().getFileName().toString().equals("pom.xml")) {
                isMavenProject = true;
                break;
            }
        }

        if (isMavenProject) {
            MavenEntity mavenEntity = new MavenEntity();
            aspects.add(mavenEntity);
            for (Feature feature : mavenEntity.getFeatureList()) {
                features.add(feature);
            }
        }

        boolean isGitProject = false;
        for (Node node : rootNode.getChildren()) {
            if (node.getType() == Node.Types.FOLDER && node.getPath().getFileName().toString().equals(".git")) {
                isGitProject = true;
                break;
            }
        }

        if (isGitProject) {
            GitEntity gitEntity = new GitEntity();
            aspects.add(gitEntity);
            for (Feature feature : gitEntity.getFeatureList()) {
                features.add(feature);
            }
        }
    }

    @Override
    public MyNode getRootNode() {
        return rootNode;
    }

    @Override
    public Set<Aspect> getAspects() {
        return aspects;
    }

    @Override
    public Optional<Feature> getFeature(Feature.Type featureType) {
        for (Feature feature : features) {
            if (feature.type() == featureType)
                return Optional.of(feature);
        }

        return Optional.empty();
    }
}
