package com.floweektracker.repository;

import lombok.*;

import java.io.File;

/**
 * Represents a configurator for the repositories. It is a singleton with no args constructor. It is used to set up the
 * repositories before the application starts.
 * <br><br>
 * FIELDS: {@link #configurator}, {@link #plannerRepository}, {@link #taskNamesRepository}
 * <br><br>
 * METHODS: {@link #setUpRepositories(File)}, {@link #setUpPlannerRepository(File)},
 * {@link #setUpTaskNamesRepository(File)}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepositoryConfigurator {
    @Getter(AccessLevel.PUBLIC)
    private static final RepositoryConfigurator configurator = new RepositoryConfigurator();
    private final PlannerRepository plannerRepository = PlannerRepository.getRepository();
    private final TaskNamesRepository taskNamesRepository = TaskNamesRepository.getRepository();

    /**
     * Sets up all repositories using the given directory. If the directory is null or doesn't exist, the method return
     * false. Otherwise, it calls {@link #setUpPlannerRepository(File)} and {@link #setUpTaskNamesRepository(File)} to
     * initialize the repositories.
     *
     * @param directory a directory which should be passed to the repository set up methods
     * @return true if both repositories are successfully set up, otherwise false
     * @see com.floweektracker.Main#main(String[])
     */
    public boolean setUpRepositories(File directory) {
        if (directory == null || !directory.exists()) return false;

        setUpPlannerRepository(directory);
        setUpTaskNamesRepository(directory);

        return (plannerRepository.getFile() != null) && (taskNamesRepository.getFile() != null);
    }

    /**
     * Sets up the {@link #plannerRepository} using the given directory. Creates a new planner file, assigns it to the
     * repository, and then loads planner data from the file.
     *
     * @param directory a given directory where the planner file should be created or located
     * @see #setUpRepositories(File)
     */
    private void setUpPlannerRepository(File directory) {
        var plannerFile = new File(directory, "Planner.json");
        plannerRepository.setFile(plannerFile);
        plannerRepository.loadPlanner();
    }

    /**
     * Sets up the {@link #taskNamesRepository} using the given directory. Creates a new task names file, assigns it to
     * the repository, and then load task names data from the file.
     *
     * @param directory a given directory where the task names file should be created or located
     * @see #setUpRepositories(File)
     */
    private void setUpTaskNamesRepository(File directory) {
        var taskNamesFile = new File(directory, "TaskNames.json");
        taskNamesRepository.setFile(taskNamesFile);
        taskNamesRepository.loadTaskNames();
    }
}
