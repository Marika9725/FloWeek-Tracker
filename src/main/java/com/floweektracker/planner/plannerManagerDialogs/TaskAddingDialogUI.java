package com.floweektracker.planner.plannerManagerDialogs;

import com.floweektracker.planner.PlannerDataManager;
import com.floweektracker.tasksDatabase.SingleTaskManager;

import javax.swing.*;
import java.util.HashMap;


/**
 * Represents the UI dialog allowing the user to add a task to the planner. This class extends from
 * {@link TaskDialogUI}, which provides the base structure for the dialog.
 * <br><br>
 * Fields:
 * <ul><li>{@link #isTaskAdded} - a boolean indicating whether a task has been added</li></ul>
 * Methods:
 * <ul><li>{@link #configureTaskAddingDialog()} - configures the {@link #dialog} to allow adding tasks to the planner
 * </li></ul>
 *
 * Getters: {@link #isTaskAdded()}
 * <br><br>
 * Setters: {@link #setTaskAdded(boolean)}
 */
public class TaskAddingDialogUI extends TaskDialogUI {
    /**
     * Contains a boolean value representing status of task adding. If the value is true, the task was successfully
     * added; otherwise the task adding failed.
     *
     * @see #TaskAddingDialogUI(HashMap)
     * @see #isTaskAdded()
     * @see #setTaskAdded(boolean)
     */
    private boolean isTaskAdded;

    /**
     * Constructs a new {@link TaskAddingDialogUI} instance by invoking the {@link TaskDialogUI} constructor with the
     * given {@code planners} and a new {@link SingleTaskManager} as parameters to create the base {@link #dialog}.
     * Then, it invokes {@link #configureTaskAddingDialog()} to add {@link java.awt.Component}s and settings specific to
     * the task adding dialog. Finally, sets {@link #isTaskAdded} to false.
     *
     * @param planners a {@link HashMap} containing {@link SingleTaskManager}s sorted by each day of the week. The key
     *                 is a {@link String} representing the weekday name, and the value is another {@link HashMap} where
     *                 the key is a {@link String} representing the time and the value is a {@link SingleTaskManager}
     *                 representing the task
     * @see PlannerDataManager#addTask()
     */
    public TaskAddingDialogUI(HashMap<String, HashMap<String, SingleTaskManager>> planners) {
        super(planners, new SingleTaskManager());
        configureTaskAddingDialog();
    }

    /**
     * Configures the task adding {@link #dialog} by adding {@link JPanel}s created in the
     * {@link #createDescriptionPanel()} and {@link #createButtonsPanel()}, listeners, and setting the title. It also
     * invokes {@code pack()} to fit the window to its preferred size, {@code setLocationRelativeTo} to center the
     * window, and {@code setVisible} to display the window.
     *
     * @see #TaskAddingDialogUI(HashMap)
     * @see DialogListeners
     */
    public void configureTaskAddingDialog() {
        getDialog().add(createDescriptionPanel());
        getDialog().add(createButtonsPanel());
        new DialogListeners<>(this);

        getDialog().setTitle("Dodaj zadanie do planera");
        getDialog().pack();
        getDialog().setLocationRelativeTo(null);
        getDialog().setVisible(true);
    }

    /**
     * @return a boolean value representing the status of task addition. Returns true if the task has been added
     * successfully; false if the task addition failed.
     * @see PlannerDataManager#addTask()
     */
    public boolean isTaskAdded() {return isTaskAdded;}

    /**
     * Assigns the {@code taskAdded} value to the {@link #isTaskAdded} field.
     * @param taskAdded a boolean value representing the status of task addition.
     * @see DialogListeners#createButtonsListener()
     */
    void setTaskAdded(boolean taskAdded) {this.isTaskAdded = taskAdded;}
}