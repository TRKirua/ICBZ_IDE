package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;

public class Tree implements Feature {
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        System.out.println("Generating Maven Project Tree:");
        return () -> {
            try {
                String args = "";
                for (int i = 0; i < params.length; i++)  {
                    args +=  " " + params[0].toString();
                }

                String command = "mvn dependency:tree" + args;
                Process process = Runtime.getRuntime().exec(command, null,
                        project.getRootNode().getPath().toFile());

                int exitCode = process.waitFor();

                if (exitCode != 0) {
                    System.out.println("Failed to generate Maven project tree. Exit code: " + exitCode);
                } else {
                    System.out.println("Succeed to generate Maven project tree.");
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
        return Mandatory.Features.Maven.TREE;
    }
}
