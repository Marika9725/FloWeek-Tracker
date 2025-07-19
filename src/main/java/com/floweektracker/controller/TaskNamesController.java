package com.floweektracker.controller;

import com.floweektracker.service.TaskNamesService;
import com.floweektracker.view.TaskNamesDialog;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Controller for the {@link TaskNamesDialog} class. This class handles the interactions and logic related to managing
 * task names, by communicating with {@link TaskNamesService}, {@link TaskAddingDialogController}, and
 * {@link TaskEditingDialogController}.
 * <br><br>
 * Fields: {@link #taskNamesService}, {@link #taskNamesDialog}, {@link #taskAddingDialogController},
 * {@link #taskEditingDialogController}
 * <br><br>
 * Listeners methods: {@link #areComponentsHaveNoListeners()}, {@link #createTaskNamesListListener()},
 * {@link #createButtonsListener()}
 * <br><br>
 * Helper methods: {@link #makeAction(Function)}, {@link #updateTaskNamesView()}
 * <br><br>
 * Other methods: {@link #updateTaskNamesDialogs(String)}
 */
@Getter
public class TaskNamesController {
    private final TaskNamesService taskNamesService = TaskNamesService.getService();
    private final TaskNamesDialog taskNamesDialog = TaskNamesDialog.getView();
    private final TaskAddingDialogController taskAddingDialogController;
    private final TaskEditingDialogController taskEditingDialogController;

    /**
     * Creates a controller for the given {@link TaskAddingDialogController} and {@link TaskEditingDialogController} and
     * sets listeners to the components of the {@link #taskNamesDialog} if they don't have any.
     *
     * @param taskAddingDialogController  a given {@link TaskAddingDialogController}
     * @param taskEditingDialogController a given {@link TaskEditingDialogController}
     * @see MainPanelController
     * @see #areComponentsHaveNoListeners()
     * @see #initListeners()
     */
    public TaskNamesController(TaskAddingDialogController taskAddingDialogController, TaskEditingDialogController taskEditingDialogController) {
        this.taskAddingDialogController = taskAddingDialogController;
        this.taskEditingDialogController = taskEditingDialogController;

        if (areComponentsHaveNoListeners()) initListeners();
    }

    //region listeners methods

    /**
     * @return true if the components in the {@link #taskNamesDialog} don't have listeners, otherwise false
     * @see TaskNamesController(TaskAddingDialogController, TaskEditingDialogController)
     */
    private boolean areComponentsHaveNoListeners() {
        var hasNoTaskNamesListListener = taskNamesDialog.getTaskNamesList().getListSelectionListeners().length < 1;
        var haveNoButtonsListener = Arrays.stream(taskNamesDialog.getButtons()).allMatch(button -> button.getActionListeners().length < 1);

        return hasNoTaskNamesListListener && haveNoButtonsListener;
    }

    /**
     * Adds listeners to the components in the {@link #taskNamesDialog}: the task names list and the buttons.
     *
     * @see #createTaskNamesListListener()
     * @see #createButtonsListener()
     * @see TaskNamesController(TaskAddingDialogController, TaskEditingDialogController)
     */
    private void initListeners() {
        taskNamesDialog.getTaskNamesList().addListSelectionListener(createTaskNamesListListener());

        Arrays.stream(taskNamesDialog.getButtons())
                .forEach(button -> button.addActionListener(createButtonsListener()));
    }

    /**
     * Creates {@link ListSelectionListener} for the task names list in the {@link #taskNamesDialog}. When event is
     * handled, it checks if the event is not adjusting and then sets the selected task name from the task names list in
     * the input field.
     *
     * @return a new {@link ListSelectionListener} for the task names list in the {@link #taskNamesDialog}
     * @see TaskNamesDialog#setTaskNameInInputField(String)
     * @see TaskNamesDialog#getSelectedTaskNameFromTaskNamesList()
     * @see #initListeners()
     */
    private ListSelectionListener createTaskNamesListListener() {
        return listEvent -> {
            if (!listEvent.getValueIsAdjusting()) {
                taskNamesDialog.setTaskNameInInputField(taskNamesDialog.getSelectedTaskNameFromTaskNamesList());
            }
        };
    }

    /**
     * Creates a new {@link ActionListener} for the buttons in the {@link #taskNamesDialog}. When event is handled, it
     * checks, which button was clicked and then calls the appropriate method.
     * <ul>
     *     <li>When confirm button is clicked, it calls the {@link TaskNamesService#addTaskName(String)} method to add task name to the database.</li>
     *     <li>When delete button is clicked, it calls the {@link TaskNamesService#deleteTaskName(String)} method to delete task name in the database.</li>
     *     <li>When cancel button is clicked, it clears the input field.</li>
     * </ul>
     *
     * @return a new {@link ActionListener} for the button from the {@link #taskNamesDialog}
     * @see #makeAction(Function)
     * @see TaskNamesDialog#setTaskNameInInputField(String)
     * @see #initListeners()
     */
    private ActionListener createButtonsListener() {
        return event -> {
            switch (event.getActionCommand()) {
                case "OK" -> makeAction(taskNamesService::addTaskName);
                case "Usuń" -> makeAction(taskNamesService::deleteTaskName);
                case "Wyczyść" -> taskNamesDialog.setTaskNameInInputField("");
            }
        };
    }
    //endregion

    //region helper methods

    /**
     * Calls the given {@code action} method with the task name from the input field in the {@link #taskNamesDialog}. It
     * is used to add or delete a task name getting from the input field. When the action is successful, it updates task
     * names in the task names view and in the task names dialogs.
     *
     * @param action a given {@link Function} which should be called; it shouldn't be null
     * @see #createButtonsListener()
     * @see TaskNamesDialog#getTaskNameFromInputField()
     * @see #updateTaskNamesView()
     * @see #updateTaskNamesDialogs(String)
     */
    private void makeAction(@NotNull Function<String, Boolean> action) {
        var taskName = taskNamesDialog.getTaskNameFromInputField();
        if (action.apply(taskName)) {
            updateTaskNamesView();
            updateTaskNamesDialogs(taskName);
        }
    }

    /**
     * Updates the task names list in the {@link #taskNamesDialog} and clears the input field.
     *
     * @see #makeAction(Function)
     */
    private void updateTaskNamesView() {
        taskNamesDialog.setTaskNamesList(taskNamesService.getTaskNames().toArray(new String[0]));
        taskNamesDialog.setTaskNameInInputField("");
    }
    //endregion

    /**
     * Updates the task names in the task dialogs, by calling the
     * {@link TaskAddingDialogController#updateTaskNames(String)} and
     * {@link TaskEditingDialogController#updateTaskNames(String)} methods.
     *
     * @param taskName a given task name which should be updated
     * @see #makeAction(Function)
     * @see TaskAddingDialogController#updateTaskNames(String)
     * @see TaskEditingDialogController#updateTaskNames(String)
     */
    private void updateTaskNamesDialogs(String taskName) {
        taskAddingDialogController.updateTaskNames(taskName);
        taskEditingDialogController.updateTaskNames(taskName);
    }
}