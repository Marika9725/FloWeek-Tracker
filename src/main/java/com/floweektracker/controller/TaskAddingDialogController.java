package com.floweektracker.controller;

import com.floweektracker.model.SingleTask;
import com.floweektracker.service.*;
import com.floweektracker.util.DialogUtils;
import com.floweektracker.view.TaskAddingDialog;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Controller of the {@link TaskAddingDialog} class. This class handles the user interactions and business logic related
 * to adding new task, by communicating with {@link TasksService}, {@link PlannerService}, and
 * {@link WeekdayPlannerService}.
 * <br><br>
 * Fields: {@link #taskAddingDialog}, {@link #tasksService}, {@link #plannerService}, {@link #weekdayPlannerService}
 * <br><br>
 * Listeners methods: {@link #initializeListeners()}, {@link #createSingleTask()}
 * <br><br>
 * Add task methods: {@link #addTask(SingleTask)}, {@link #isTaskAdded(SingleTask)},
 * {@link #rollback(SingleTask, List)}
 * <br><br>
 * Other methods: {@link #updateTaskNames(String)}
 */
@Getter
public class TaskAddingDialogController {
    //region Fields
    private final TaskAddingDialog taskAddingDialog;
    private final TasksService tasksService = TasksService.getService();
    private final PlannerService plannerService = PlannerService.getService();
    private final WeekdayPlannerService weekdayPlannerService = WeekdayPlannerService.getService();
    //endregion

    /**
     * Creates a controller for the given {@link TaskAddingDialog} and sets up listeners for its buttons. Checks if the
     * given {@link TaskAddingDialog} is null and throws an {@link IllegalArgumentException} if it is.
     *
     * @param taskAddingDialog a reference to the {@link TaskAddingDialog} instance to be managed by this controller
     * @throws IllegalArgumentException if the given {@link TaskAddingDialog} is null
     * @see #initializeListeners()
     * @see com.floweektracker.MainFrame
     */
    public TaskAddingDialogController(TaskAddingDialog taskAddingDialog) {
        if (taskAddingDialog == null) throw new IllegalArgumentException("TaskAddingDialog cannot be null");
        this.taskAddingDialog = taskAddingDialog;

        initializeListeners();
    }

    //region listeners methods

    /**
     * Adds listeners to the buttons from the {@link #taskAddingDialog}. The first button is for adding a task. The
     * second one is for closing the dialog.
     *
     * @see TaskAddingDialogController(TaskAddingDialog)
     * @see #addTask(SingleTask)
     * @see #createSingleTask()
     */
    public void initializeListeners() {
        taskAddingDialog.getButtons().getFirst().addActionListener(_ -> addTask(createSingleTask()));
        taskAddingDialog.getButtons().getLast().addActionListener(_ -> taskAddingDialog.makeDialogInvisible());
    }

    /**
     * Creates a new {@link SingleTask} based on the values from the {@link #taskAddingDialog}.
     *
     * @return a new {@link SingleTask}
     * @see #initializeListeners()
     */
    private SingleTask createSingleTask() {
        return new SingleTask(
                taskAddingDialog.getTaskName(),
                taskAddingDialog.getDescription(),
                taskAddingDialog.getTime(),
                false,
                taskAddingDialog.getWeekday(),
                taskAddingDialog.getPriority()
        );
    }
    //endregion

    //region addTask methods
    /**
     * Adds a given task to the {@link TasksService}, {@link com.floweektracker.view.PlannerView} and
     * {@link com.floweektracker.view.WeekdayPlannerView}. If any of these operations fails, the task is rolled back. It
     * also checks if the given task is null and returns false if it is. It also makes {@link #taskAddingDialog}
     * invisible.
     *
     * @param task a given task which should be added to the planner
     * @return true if task is successfully added, otherwise false
     * @see #isTaskAdded(SingleTask)
     * @see #rollback(SingleTask, List)
     * @see #initializeListeners()
     */
    boolean addTask(SingleTask task) {
        if (task == null) return false;

        taskAddingDialog.makeDialogInvisible();

        if (!tasksService.addTask(task)) return isTaskAdded(task);
        if (!plannerService.addTask(task)) return rollback(task, List.of(tasksService::deleteTask));
        if (!weekdayPlannerService.addTask(task))
            return rollback(task, List.of(plannerService::deleteTask, tasksService::deleteTask));

        return isTaskAdded(task);
    }

    /**
     * Checks if a given task is in {@link TasksService}, {@link com.floweektracker.view.PlannerView} and
     * {@link com.floweektracker.view.WeekdayPlannerView}.
     *
     * @param task a given task which should be checked
     * @return true if task is in all services, otherwise false
     * @see #addTask(SingleTask)
     * @see #rollback(SingleTask, List)
     */
    private boolean isTaskAdded(SingleTask task) {
        return tasksService.isTaskInSchedule(task) && plannerService.isTaskInPlanner(task) && weekdayPlannerService.isTaskInWeekdayPlanner(task);
    }

    /**
     * Rolls back a given task by executing a list of operations. When an operation fails, it shows a message dialog to
     * the user.
     *
     * @param task        a given task which should be rolled back
     * @param actionsList an operation which should be done to roll back the task
     * @return true if task is successfully rolled back, otherwise false
     * @see #addTask(SingleTask)
     */
    private boolean rollback(@NotNull SingleTask task, @NotNull List<Consumer<SingleTask>> actionsList) {
        for (Consumer<SingleTask> action : actionsList) {
            try {
                action.accept(task);
            } catch (Exception e) {
                DialogUtils.showMessageDialog("Niepowodzenie", "Nie udało się cofnąć operacji dla: " + task);
            }
        }

        return isTaskAdded(task);
    }
    //endregion

    //region updateTaskNames methods
    /**
     * Updates the tasks combo box in the {@link #taskAddingDialog}. It checks if the given task name is in the tasks
     * combo box. If it is, it removes it. Otherwise, it inserts it in the correct position. If the task name is null or
     * black, it returns false.
     *
     * @param taskName a given task name which should be updated (removed or inserted)
     * @return true when the task name is successfully updated, otherwise false
     * @see TaskNamesController#updateTaskNamesView()
     */
    public boolean updateTaskNames(String taskName) {
        if (taskName == null || taskName.isBlank()) return false;
        var taskNamesComboBox = taskAddingDialog.getTasksComboBox();
        var taskNames = taskAddingDialog.getAllTaskNames();

        if (taskAddingDialog.getAllTaskNames().contains(taskName)) taskNamesComboBox.removeItem(taskName);
        else {
            int insertIndex = -Collections.binarySearch(taskNames, taskName) - 1;
            taskNamesComboBox.insertItemAt(taskName, insertIndex);
        }

        return true;
    }
    //endregion
}