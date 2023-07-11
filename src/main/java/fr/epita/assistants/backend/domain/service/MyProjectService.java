package fr.epita.assistants.backend.domain.service;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.MyProject;
import fr.epita.assistants.backend.domain.entity.Project;

import java.nio.file.Path;

public class MyProjectService implements ProjectService {

    private MyProject myProject;
    private Path indexFile;
    private Path tmpFolder;
    private MyNodeService myNodeService;

    public MyProjectService(Path indexFile, Path tmpFolder) {
        this.indexFile = indexFile;
        this.tmpFolder = tmpFolder;
        this.myNodeService = new MyNodeService();
    }

    @Override
    public Project load(Path root) {
        myProject = new MyProject(root);
        return myProject;
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Feature.Type featureType, Object... params) {
        for (Feature feature : project.getFeatures()) {
            if (feature.type() == featureType) {
                return feature.execute(project, params);
            }
        }

        return new Feature.ExecutionReport() {
            @Override
            public boolean isSuccess() {
                return false;
            }
        };
    }

    @Override
    public NodeService getNodeService() {
        return myNodeService;
    }

    public MyProject getMyProject() {
        return myProject;
    }
}
