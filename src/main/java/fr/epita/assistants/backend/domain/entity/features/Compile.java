package fr.epita.assistants.backend.domain.entity.features;

import fr.epita.assistants.backend.domain.entity.Feature;
import fr.epita.assistants.backend.domain.entity.Mandatory;
import fr.epita.assistants.backend.domain.entity.Project;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Compile implements Feature {

    public @NotNull Feature.ExecutionReport execute(final Project project, final Object... params) {
        System.out.println("Running Maven Compile");
        return () -> {
            try {

                Process process = Runtime.getRuntime().exec("mvn compile", null,
                        project.getRootNode().getPath().toFile());

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("Maven build successful!");
                } else {
                    System.out.println("Maven build failed. Exit code: " + exitCode);
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        };
    }


    @Override
    public Type type() {
        return Mandatory.Features.Maven.COMPILE;
    }
}
