package com.floweektracker.controller;

import com.floweektracker.model.SingleTask;
import com.floweektracker.service.*;
import com.floweektracker.util.DialogUtils;
import com.floweektracker.view.TaskEditingDialog;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionListener;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Controller of the {@link TaskEditingDialog} class. This class handles the user interactions and business logic
 * related to editing old task, by communicating with {@link TasksService}, {@link PlannerService}, and
 * {@link WeekdayPlannerService}.
 * <br><br>
 * Fields: {@link #taskEditingDialog}, {@link #tasksService}, {@link #plannerService}, {@link #weekdayPlannerService}
 * <br><br>
 * Listeners methods: {@link #areButtonsWithoutListeners(TaskEditingDialog)}, {@link #initializeListeners()},
 * {@link #createEditConfirmButtonListener()}, {@link #createEditCancelButtonListener()}, {@link #createSingleTask()}
 * <br><br>
 * Edit task methods: {@link #editTask(SingleTask, SingleTask)}, {@link #isTaskEdited(SingleTask, SingleTask)},
 * {@link #rollback(SingleTask, SingleTask, List)}
 * <br><br>
 * Other methods: {@link #updateTaskNames(String)}
 */
@Getter
public class TaskEditingDialogController {
    //region Fields
    private final TasksService tasksService = TasksService.getService();
    private final PlannerService plannerService = PlannerService.getService();
    private final WeekdayPlannerService weekdayPlannerService = WeekdayPlannerService.getService();
    private final TaskEditingDialog taskEditingDialog;
    //endregion

    /**
     * Creates a controller for the given {@link TaskEditingDialog} and sets up listeners for its buttons when they
     * haven't got listeners. Checks if the given {@link TaskEditingDialog} is null.
     *
     * @param taskEditingDialog a reference to the {@link TaskEditingDialog} instance to be managed by this controller
     * @throws IllegalArgumentException when the given {@link TaskEditingDialog} is null
     * @see #areButtonsWithoutListeners(TaskEditingDialog)
     * @see #initializeListeners()
     * @see PlannerController#PlannerController()
     */
    public TaskEditingDialogController(TaskEditingDialog taskEditingDialog) {
        if (taskEditingDialog == null) throw new IllegalArgumentException("TaskEditingDialog cannot be null.");
        this.taskEditingDialog = taskEditingDialog;

        if (areButtonsWithoutListeners(taskEditingDialog)) initializeListeners();
    }

    //region Listener methods

    /**
     * @param taskEditingDialog a reference to the {@link TaskEditingDialog} instance whose buttons should be checked
     * @return true when buttons from the {@link TaskEditingDialog} instance don't have listeners, otherwise false
     * @see TaskEditingDialogController(TaskEditingDialog)
     */
    private boolean areButtonsWithoutListeners(@NotNull TaskEditingDialog taskEditingDialog) {
        return taskEditingDialog.getButtons().stream().noneMatch(button -> button.getActionListeners().length > 0);
    }

    /**
     * Adds listeners to the buttons from the {@link #taskEditingDialog}. The first button is for confirm editing. The
     * second one is for closing the dialog and cancelling changes. If the {@link #taskEditingDialog} is null, it does
     * nothing.
     *
     * @see TaskEditingDialogController(TaskEditingDialog)
     * @see #createEditConfirmButtonListener()
     * @see #createEditCancelButtonListener()
     */
    public void initializeListeners() {
        taskEditingDialog.getButtons().getFirst().addActionListener(createEditConfirmButtonListener());
        taskEditingDialog.getButtons().getLast().addActionListener(createEditCancelButtonListener());
    }

    /**
     * Creates a listener for the confirm button from the {@link #taskEditingDialog}. When the button is clicked, it
     * runs {@link #editTask(SingleTask, SingleTask)} method.
     *
     * @return an {@link ActionListener} for the confirm button
     * @see #initializeListeners()
     */
    private ActionListener createEditConfirmButtonListener() {
        return _ -> editTask(taskEditingDialog.getOriginalTask(), createSingleTask());
    }

    /**
     * Creates a listener for the cancel button from the {@link #taskEditingDialog}. When the button is clicked, it
     * makes the dialog invisible.
     *
     * @return an {@link ActionListener} for the cancel button
     * @see #initializeListeners()
     */
    private ActionListener createEditCancelButtonListener() {
        return _ -> taskEditingDialog.makeDialogInvisible();
    }

    /**
     * Creates a new {@link SingleTask} based on the values from the {@link #taskEditingDialog}.
     *
     * @return a new {@link SingleTask}
     * @see #createEditConfirmButtonListener()
     */
    private SingleTask createSingleTask() {
        return new SingleTask(
                taskEditingDialog.getTaskName(),
                taskEditingDialog.getDescription(),
                taskEditingDialog.getTime(),
                taskEditingDialog.getStatus(),
                taskEditingDialog.getWeekday(),
                taskEditingDialog.getPriority()
        );
    }
    //endregion

    //region EditTask methods

    /**
     * Edits an {@code original task} with a given {@code edited task}. It first checks if the given tasks are not null.
     * If they are, it returns false. Then, it makes {@link #taskEditingDialog} invisible and edits the task in the
     * {@link TasksService}, {@link com.floweektracker.view.PlannerView} and
     * {@link com.floweektracker.view.WeekdayPlannerView}. If any of these operations fails, the task is rolled back.
     *
     * @param originalTask a given original task which should be edited
     * @param editedTask   a given edited task which should replace the original one
     * @return true if task is successfully edited, otherwise false
     * @see #isTaskEdited(SingleTask, SingleTask)
     * @see #rollback(SingleTask, SingleTask, List)
     * @see #createEditConfirmButtonListener()
     */
    public boolean editTask(SingleTask originalTask, SingleTask editedTask) {
        if ((originalTask == null) || (editedTask == null)) return false;

        taskEditingDialog.makeDialogInvisible();

        if (!tasksService.editTask(originalTask, editedTask)) return false;
        if (!weekdayPlannerService.editTask(originalTask, editedTask))
            return rollback(originalTask, editedTask, List.of(tasksService::editTask));
        if (!plannerService.editTask(originalTask, editedTask))
            return rollback(originalTask, editedTask, List.of(weekdayPlannerService::editTask, tasksService::editTask));

        return isTaskEdited(originalTask, editedTask);
    }

    /**
     * Checks if an {@code originalTask} is edited in the {@link TasksService},
     * {@link com.floweektracker.view.PlannerView} and {@link com.floweektracker.view.WeekdayPlannerView}.
     *
     * @param originalTask an original task which should be removed
     * @param editedTask an edited task which should replace the original one
     * @return true if task is edited in all services, otherwise false
     * @see #editTask(SingleTask, SingleTask)
     * @see #rollback(SingleTask, SingleTask, List)
     */
    private boolean isTaskEdited(SingleTask originalTask, SingleTask editedTask) {
        return !tasksService.isTaskInSchedule(originalTask) && tasksService.isTaskInSchedule(editedTask) &&
                !plannerService.isTaskInPlanner(originalTask) && plannerService.isTaskInPlanner(editedTask) &&
                !weekdayPlannerService.isTaskInWeekdayPlanner(originalTask) && weekdayPlannerService.isTaskInWeekdayPlanner(editedTask);
    }

    /**
     * Rolls back editing task by executing a list of operations. When an operation fails, it shows a message dialog to
     * the user.
     *
     * @param originalTask an original task which should be rolled back
     * @param editedTask an edited task which should be rolled back
     * @param actionsList an operation which should be done to roll back the tasks
     * @return true if task is successfully rolled back, otherwise false
     * @see #editTask(SingleTask, SingleTask)
     */
    private boolean rollback(SingleTask originalTask, SingleTask editedTask, List<BiConsumer<SingleTask, SingleTask>> actionsList) {
        for (BiConsumer<SingleTask, SingleTask> action : actionsList) {
            try {
                action.accept(editedTask, originalTask);
            } catch (Exception e) {
                DialogUtils.showMessageDialog("Niepowodzenie", "Nie udało się cofnąć operacji dla: " + originalTask);
            }
        }

        return isTaskEdited(originalTask, editedTask);
    }
    //endregion

    /**
     * Updates the tasks combo box in the {@link #taskEditingDialog}. It checks if the given task name is in the tasks
     * combo box. If it is, it removes it. Otherwise, it inserts it in the correct position. If the task name is null or
     * black, it returns false.
     *
     * @param taskName a given task name which should be updated (removed or inserted)
     * @return true when the task name is successfully updated, otherwise false
     * @see TaskNamesController#updateTaskNamesView()
     */
    public boolean updateTaskNames(String taskName) {
        if (taskName == null || taskName.isBlank()) return false;

        var taskNamesComboBox = taskEditingDialog.getTasksComboBox();
        var taskNames = taskEditingDialog.getAllTaskNames();

        if (taskNames.contains(taskName)) taskNamesComboBox.removeItem(taskName);
        else {
            int insertIndex = -Collections.binarySearch(taskNames, taskName) - 1;
            taskNamesComboBox.insertItemAt(taskName, insertIndex);
        }

        return true;
    }
}
