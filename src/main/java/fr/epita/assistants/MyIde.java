package fr.epita.assistants;

import fr.epita.assistants.backend.domain.service.MyProjectService;
import fr.epita.assistants.backend.domain.service.ProjectService;
import fr.epita.assistants.backend.utils.Given;
import java.nio.file.Path;

/**
 * Starter class, we will use this class and the init method to get a
 * configured instance of {@link ProjectService}.
 */
@Given(overwritten = false)
public class MyIde {

    /**
     * Init method. It must return a fully functional implementation of {@link ProjectService}.
     *
     * @return An implementation of {@link ProjectService}.
     */
    public static ProjectService init(final Configuration configuration) {
        return new MyProjectService(configuration.indexFile, configuration.tempFolder);
    }

    /**
     * Record to specify where the configuration of your IDE
     * must be stored. Might be useful for the search feature.
     */
    public record Configuration(Path indexFile,
                                Path tempFolder) {
    }

    /*public static void main(String[] args) {
        Configuration configuration = new Configuration(Paths.get("C:\\Users\\enese\\Downloads\\SCALA"), Paths.get("C:\\Users\\enese\\Downloads\\tmp"));
        ProjectService projectService = init(configuration);

        Project project = projectService.load(Paths.get("/home/nicolas/Prog/ping-icbz"));
        //var res = project.getFeature(Mandatory.Features.Git.ADD).get().execute(project,"ping/src/");
        //System.out.println(res.isSuccess());

        //var res2 = project.getFeature(Mandatory.Features.Git.COMMIT).get().execute(project, "automatic commit 3");
        //res2.isSuccess();

        //var res3 = project.getFeature(Mandatory.Features.Git.PUSH).get().execute(project, "ping/src/");
        //res3.isSuccess();

        //var res4 = project.getFeature(Mandatory.Features.Git.PULL).get().execute(project, "ping/src/");
        //res4.isSuccess();

        //project.getFeature(Mandatory.Features.Maven.TREE).get().execute(project).isSuccess();
        //System.out.println(project.getRootNode().getPath() + " " + project.getRootNode().getType());

        project.getFeature(Mandatory.Features.Git.PULL).get().execute(project);
    }*/
}