package com.floweektracker;

import com.floweektracker.repository.*;
import com.floweektracker.util.AppDirectoryManager;

import javax.swing.*;

/**
 * Main class which serves as the entry point of the application. It checks the first argument. If it is true, it runs
 * {@link AppDirectoryManager#createDirectory()} to checks if the app contains directory where necessary files can be
 * stored. If not, it creates the directory with user's permission or terminate the app. Then, it calls the
 * {@link RepositoryConfigurator} to initialize repositories. Then, it attempts to load planner and task names from json
 * files to the application. At the end, it creates the main frame of the app.
 */
public class Main {
    public static void main(String[] args) {
        if (args != null) {
            SwingUtilities.invokeLater(() -> {
                if (isFirstArgumentTrue(args)) {
                    var createdFile = AppDirectoryManager.getInstance().createDirectory();
                    RepositoryConfigurator.getConfigurator().setUpRepositories(createdFile);
                    PlannerRepository.getRepository().loadPlanner();
                    TaskNamesRepository.getRepository().loadTaskNames();

                    MainFrame.getMAIN_FRAME();
                }
            });
        }
    }

    /**
     * @param args arguments passed to the application
     * @return true if first argument is "true", otherwise false
     * @see #main(String[])
     */
    private static boolean isFirstArgumentTrue(String[] args) {
        return (args.length == 1) && (args[0].equalsIgnoreCase("true"));
    }
}
