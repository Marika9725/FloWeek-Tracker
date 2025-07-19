package com.floweektracker.service;

import com.floweektracker.controller.TaskNamesController;
import com.floweektracker.repository.TaskNamesRepository;
import lombok.*;

import java.util.*;

/**
 * Singleton service for the {@link com.floweektracker.view.TaskNamesDialog}. It is used to store and manage task names,
 * providing possibility to add, remove and save task names.
 * <br><br>
 * Fields: {@link #service}, {@link #taskNames}, {@link #taskNamesRepository}
 * <br><br>
 * Methods: {@link #addTaskName(String)}, {@link #deleteTaskName(String)}, {@link #isTaskValid(String)},
 * {@link #saveTaskNames()}
 */
@Getter
public class TaskNamesService {
    @Getter(AccessLevel.PUBLIC)
    private static final TaskNamesService service = new TaskNamesService();
    private final Set<String> taskNames = new TreeSet<>();
    private final TaskNamesRepository taskNamesRepository = TaskNamesRepository.getRepository();

    /**
     * Constructor for the {@link TaskNamesService}. It gets task names from the {@link TaskNamesRepository} and puts
     * them in the {@link #taskNames}.
     *
     * @see com.floweektracker.MainFrame#addWindowListenerToFrame()
     * @see com.floweektracker.controller.TaskNamesController
     * @see com.floweektracker.view.TaskNamesDialog#TaskNamesDialog()
     */
    private TaskNamesService() {
        var loadedTaskNames = taskNamesRepository.getTaskNames();
        if (loadedTaskNames != null) taskNames.addAll(loadedTaskNames);
    }

    /**
     * Adds a given task name to the {@link #taskNames}. If the task name isn't valid or already exists, it returns
     * false.
     *
     * @param taskName a given task name which should be added
     * @return true if a task name is successfully added, otherwise false
     * @see TaskNamesController#createButtonsListener()
     * @see #isTaskValid(String)
     */
    public boolean addTaskName(String taskName) {
        if (!isTaskValid(taskName) || taskNames.contains(taskName)) return false;

        return taskNames.add(taskName);
    }

    /**
     * Deletes a given task name in the {@link #taskNames}. If the task name isn't valid or doesn't exist, it returns
     * false.
     *
     * @param taskName a given task name which should be deleted
     * @return true if task name is successfully deleted, otherwise false
     * @see TaskNamesController#createButtonsListener()
     * @see #isTaskValid(String)
     */
    public boolean deleteTaskName(String taskName) {
        if (!isTaskValid(taskName) || !taskNames.contains(taskName)) return false;

        return taskNames.remove(taskName);
    }

    /**
     * Checks if the given task name is not null or blank.
     *
     * @param taskName a given task name which should be checked
     * @return true if task name is valid, otherwise false
     * @see #addTaskName(String)
     * @see #deleteTaskName(String)
     */
    private boolean isTaskValid(String taskName) {
        return (taskName != null) && !taskName.isBlank();
    }

    /**
     * Saves task names in the {@link #taskNamesRepository}.
     *
     * @see com.floweektracker.MainFrame#addWindowListenerToFrame()
     */
    public void saveTaskNames() {
        taskNamesRepository.setTaskNames(taskNames);
        taskNamesRepository.saveTaskNames();
    }
}