package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test implements Feature {
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        System.out.println("Running Maven Test");
        return () -> {
            try {
                Process process = Runtime.getRuntime().exec("mvn test", null, project.getRootNode().getPath().toFile());

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("Maven test successful!");
                } else {
                    System.out.println("Maven test failed. Exit code: " + exitCode);
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
        return Mandatory.Features.Maven.TEST;
    }
}
