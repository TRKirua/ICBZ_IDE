package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;

public class Exec implements Feature {
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        System.out.println("Running Maven Exec");
        return () -> {
            try {
                String args = "";
                for (int i = 0; i < params.length; i++) {
                    args += " " + params[0].toString();
                }

                String command = "mvn exec:java" + args;
                Process process = Runtime.getRuntime().exec(command, null,
                        project.getRootNode().getPath().toFile());

                int exitCode = process.waitFor();

                if (exitCode != 0) {
                    System.out.println("Execution failed. Exit code: " + exitCode);
                } else {
                    System.out.println("Succeed to execute Maven project.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        };
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.EXEC;
    }
}
