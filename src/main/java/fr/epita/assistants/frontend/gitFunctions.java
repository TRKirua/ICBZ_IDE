package fr.epita.assistants.frontend;

import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;
import javafx.scene.control.TreeView;

import java.nio.file.Path;

import static fr.epita.assistants.frontend.MyIdeApp.projectService;
import static fr.epita.assistants.frontend.Open.populateFileTreeView;
import static fr.epita.assistants.frontend.Settings.saveSettings;

public class gitFunctions {

    protected static void gitAdd(Project project) {
        project.getFeature(Mandatory.Features.Git.ADD).get().execute(project, " .");
    }

    protected static void gitCommit(Project project) {
        project.getFeature(Mandatory.Features.Git.COMMIT).get().execute(project, " -m ", " added files via ICBZ IDE");
    }

    protected static void gitPush(Project project) {
        project.getFeature(Mandatory.Features.Git.PUSH).get().execute(project);
    }

    protected static Project gitPull(Project project, TreeView fileTreeView) {
        project.getFeature(Mandatory.Features.Git.PULL).get().execute(project);
        Path selectedDirectory = project.getRootNode().getPath();
        project = projectService.load(selectedDirectory);
        populateFileTreeView(fileTreeView, project.getRootNode());
        saveSettings(selectedDirectory.toString(), Settings.getSelectedTheme());
        return project;
    }

}
