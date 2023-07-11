package fr.epita.assistants.frontend;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Settings {

    private static final String CONFIG_FILE = "config.properties";
    private static final String PROJECT_PATH_KEY = "project_path";
    private static final String SELECTED_THEME_KEY = "selected_theme";

    protected static void saveSettings(String projectPath, String selectedTheme) {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            if (projectPath != null) {
                prop.setProperty(PROJECT_PATH_KEY, projectPath);
            }
            if (selectedTheme != null) {
                prop.setProperty(SELECTED_THEME_KEY, selectedTheme);
            }
            prop.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static String getProjectPath() {
        return loadSettings().getProperty(PROJECT_PATH_KEY);
    }

    protected static String getSelectedTheme() {
        return loadSettings().getProperty(SELECTED_THEME_KEY);
    }

    private static Properties loadSettings() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            prop.load(input);
        } catch (FileNotFoundException e) {
            saveSettings(null, "Sci/Fi Theme");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return prop;
    }

}
