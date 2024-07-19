package com.floweektracker.planner.plannerManagerDialogs;

import com.floweektracker.planner.PlannerDataManager;
import com.floweektracker.tasksDatabase.SingleTaskManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Represents the UI dialog allowing the user to edit a task from the planner. This class extends from
 * {@link TaskDialogUI}, which provides the base structure for the dialog.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #isDoneComboBox} - a {@link JComboBox} representing a combo box with "yes" and "no" options, allowing
 *     the user to mark a task as done, displayed within the {@link #dialog}</li>
 *     <li>{@link #oldTime} - a {@link String} representing the previous time of the task</li>
 *     <li>{@link #oldWeekDayEN} - a {@link String} representing the previous weekday name of the task in English</li>
 *     <li>{@link #isTaskEdited} - a boolean indicating whether a task has been edited</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #configureTaskEditingDialog()} - configures the {@link #dialog} to allow editing a task from the
 *     planner</li>
 *     <li>{@link #createIsDoneComboBox()} - creates a {@link JPanel} containing components allowing the user to select
 *     the execution status of the {@link #task}</li>
 * </ul>
 *
 * Getters: {@link #getIsDoneComboBox()}, {@link #getOldTime()}, {@link #getOldWeekDayEN()}, {@link #isTaskEdited}
 * <br><br>
 * Setters: {@link #setTaskEdited(boolean)}
 */
public class TaskEditingDialogUI extends TaskDialogUI {
    //region Fields
    /**
     * Contains a {@link JComboBox} allowing the user to mark a {@link #task} as done. The user can select the "yes"
     * option to mark a task as done, or the "no" option to mark a task as not done. It is displayed within the
     * {@link #dialog}.
     *
     * @see #TaskEditingDialogUI(HashMap, SingleTaskManager)
     * @see #createIsDoneComboBox()
     * @see #getIsDoneComboBox()
     */
    private final JComboBox<String> isDoneComboBox;

    /**
     * Contains a {@link String} representing the previous time of the task. This value is used to track the original
     * time before any edits are made.
     *
     * @see #TaskEditingDialogUI(HashMap, SingleTaskManager)
     * @see #getOldTime()
     */
    private final String oldTime,
    /**
     * Contains a {@link String} representing the previous weekday name of the task. This value is used to track the
     * original weekday name before any edits are made.
     *
     * @see #TaskEditingDialogUI(HashMap, SingleTaskManager)
     * @see #getOldWeekDayEN()
     */
    oldWeekDayEN;
    /**
     * Contains a boolean indicating whether a task has been edited.
     *
     * @see #TaskEditingDialogUI(HashMap, SingleTaskManager)
     * @see #isTaskEdited()
     * @see #setTaskEdited(boolean)
     */
    private boolean isTaskEdited;
    //endregion

    /**
     * Constructs a new {@link TaskEditingDialogUI} instance with the specified {@code planners} and {@code task}. This
     * constructor invokes the {@code super()} method with {@code planners} and {@code task} to create the base
     * structure for the {#link dialog}. It initializes references to the {@link #isDoneComboBox}, {@link #oldTime},
     * {@link #oldWeekDayEN}, and {@link #isTaskEdited} fields. Finally, it invokes the
     * {@link #configureTaskEditingDialog()} method to add components and setting dedicated to the task editing dialog.
     *
     * @param planners a {@link HashMap} representing the {@link #planners}
     * @param task     a {@link SingleTaskManager} representing the {@link #task}
     */
    public TaskEditingDialogUI(HashMap<String, HashMap<String, SingleTaskManager>> planners, SingleTaskManager task) {
        super(planners, task);
        this.isDoneComboBox = new JComboBox<>(new String[]{"Tak", "Nie"});
        this.oldTime = task.getTime();
        this.oldWeekDayEN = task.getWeekDay().name();
        this.isTaskEdited = false;

        configureTaskEditingDialog();
    }

    //region Methods

    /**
     * Configures the task editing {@link #dialog} by adding {@link JPanel}s created in the
     * {@link #createIsDoneComboBox()}, {@link #createDescriptionPanel()} and {@link #createButtonsPanel()}, listeners,
     * and setting the title. It also invokes {@code pack()} to fit the window to its preferred size,
     * {@code setLocationRelativeTo} to center the window, and {@code setVisible} to display the window.
     *
     * @see #TaskEditingDialogUI(HashMap, SingleTaskManager)
     * @see DialogListeners
     */
    public void configureTaskEditingDialog() {
        getDialog().add(createIsDoneComboBox());
        getDialog().add(createDescriptionPanel());
        getDialog().add(createButtonsPanel());
        new DialogListeners<>(this);

        getDialog().setTitle("Edytuj zadanie");
        getDialog().pack();
        getDialog().setLocationRelativeTo(null);
        getDialog().setVisible(true);
    }

    /**
     * Creates a {@link JPanel} containing a {@link JLabel} and the {@link #isDoneComboBox}. This method sets the
     * selected item in the {@link #isDoneComboBox} to the current task's completion status, and then creates and
     * returns a {@link JPanel} using the {@link #createPanel(Component, String)} method.
     *
     * @return a {@link JPanel} with a {@link JLabel} and the {@link #isDoneComboBox}
     * @see #configureTaskEditingDialog()
     */
    private JPanel createIsDoneComboBox() {
        isDoneComboBox.setSelectedItem(getTask().getIsDone() ? "Tak" : "Nie");
        return createPanel(isDoneComboBox, "Wykonano:");
    }
    //endregion

    //region Getters

    /**
     * @return a {@link #isDoneComboBox} containing {@link String}s representing "yes" and "no" options, allowing the
     * user to select the option corresponding to the current task's completion status.
     * @see DialogListeners#setupListeners()
     * @see DialogListeners#createIsDoneComboBoxListener()
     */
    public JComboBox<String> getIsDoneComboBox() {return isDoneComboBox;}

    /**
     * @return an {@link #oldTime} representing the previous time of the task
     * @see DialogListeners#createButtonsListener()
     */
    public String getOldTime() {return oldTime;}

    /**
     * @return an {@link #oldWeekDayEN} representing the previous weekday name of the task in English
     * @see DialogListeners#createButtonsListener()
     */
    public String getOldWeekDayEN() {return oldWeekDayEN;}

    /**
     * Returns {@link #isTaskEdited}, indicating whether a task has been edited
     *
     * @return {@code true} if a task has been edited, {@code false} otherwise
     * @see PlannerDataManager#editTask(String, String)
     */
    public boolean isTaskEdited() {return isTaskEdited;}

    //endregion
    //region Setters

    /**
     * Sets the flag indicating whether a task has been edited.
     *
     * @param isTaskEdited {@code true} if the task has been edited, {@code false} otherwise
     * @see DialogListeners#createButtonsListener()
     */
    public void setTaskEdited(boolean isTaskEdited) {this.isTaskEdited = isTaskEdited;}
    //endregion
}