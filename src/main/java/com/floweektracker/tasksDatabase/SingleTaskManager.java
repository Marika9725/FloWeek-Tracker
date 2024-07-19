package com.floweektracker.tasksDatabase;

import com.floweektracker.planner.*;
import com.floweektracker.planner.plannerManagerDialogs.*;

import java.util.HashSet;

/**
 * Represents the management of a single task in the planner's system. Contains fields with information about the task
 * and access methods to these fields.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #taskName} - a {@link String} representing the task's name</li>
 *     <li>{@link #time} - a {@link String} representing the task's time</li>
 *     <li>{@link #isDone} - a boolean value representing execution status of the task</li>
 *     <li>{@link #weekDay} - a {@link WeekDays} enum representing the task's weekday name</li>
 *     <li>{@link #priority} - a byte value representing the task's priority</li>
 *     <li>{@link #description} - a {@link String} representing the task's description</li>
 * </ul>
 * Methods:
 * <ul><li>{@link #toString()} - returns a {@link String} representation of this object </li></ul>
 *
 * Getters: {@link #getTaskName()}, {@link #getTime()}, {@link #getIsDone()}, {@link #getWeekDay()},
 * {@link #getPriority()}, {@link #getDescription()}
 * <br><br>
 * Setters: {@link #setTaskName(String)}, {@link #setTime(String)}, {@link #setDone(boolean)},
 * {@link #setWeekDay(WeekDays)}, {@link #setPriority(byte)}, {@link #setDescription(String)}
 */
public class SingleTaskManager {
    //region Fields
    /**
     * Contains a {@link String} representing the task's name.
     *
     * @see #SingleTaskManager()
     * @see #getTaskName()
     * @see #setTaskName(String)
     * @see #toString()
     */
    private String taskName,
    /**
     * Contains a {@link String} representing the task's description.
     *
     * @see #SingleTaskManager()
     * @see #getDescription() ()
     * @see #setDescription(String)
     * @see #toString()
     */
    description,
    /**
     * Contains a {@link String} representing the task's time.
     *
     * @see #SingleTaskManager()
     * @see #getTime()
     * @see #setTime(String)
     */
    time;
    /**
     * Contains a boolean value representing the execution status of the task.
     *
     * @see #SingleTaskManager()
     * @see #getIsDone()
     * @see #setDone(boolean)
     * @see #toString()
     */
    private boolean isDone;
    /**
     * Contains a {@link WeekDays} enum representing the task's weekday name.
     *
     * @see #SingleTaskManager()
     * @see #getWeekDay()
     * @see #setWeekDay(WeekDays)
     * @see #toString()
     */
    private WeekDays weekDay;
    /**
     * Contains a byte value representing the task's priority.
     *
     * @see #SingleTaskManager()
     * @see #getPriority()
     * @see #setPriority(byte)
     * @see #toString()
     */
    private byte priority;
    //endregion

    /**
     * Constructs a new {@link SingleTaskManager} instance representing a user's task. It initializes references to the
     * {@link #taskName}, {@link #time}, {@link #isDone}, {@link #weekDay}, {@link #priority}, and {@link #description}
     * with default values.
     *
     * @see TaskAddingDialogUI
     */
    public SingleTaskManager() {
        this.taskName = "unknown";
        this.time = "00:00";
        this.isDone = false;
        this.weekDay = WeekDays.MONDAY;
        this.priority = 1;
        this.description = "unknown";
    }

    //region Getters

    /**
     * @return a {@link #taskName} representing the task's name.
     * @see PlannerTableManager#createPlannerModel()
     * @see WeekDayPanelUI#createTaskPanel(SingleTaskManager)
     * @see TaskDialogUI#createTaskSelectionPanel()
     */
    public String getTaskName() {return taskName;}

    /**
     * @return a {@link #time} representing the task's time.
     * @see WeekDayPanelUI#createTaskPanel(SingleTaskManager)
     * @see DialogListeners#createButtonsListener()
     * @see TaskDialogUI#createTimeSelectionPanel()
     * @see TaskEditingDialogUI#TaskEditingDialogUI(java.util.HashMap, SingleTaskManager)
     */
    public String getTime() {return time;}

    /**
     * @return a {@link #isDone} representing the execution status of the task.
     * @see PlannerTableManager#createPlannerModel()
     * @see PlannerTableManager#countPoints()
     * @see WeekDayPanelUI#createTaskPanel(SingleTaskManager)
     * @see TaskEditingDialogUI#createIsDoneComboBox()
     */
    public boolean getIsDone() {return isDone;}

    /**
     * @return a {@link #weekDay} representing the task's weekday name.
     * @see DialogListeners#createButtonsListener()
     * @see TaskDialogUI#createWeekDaySelectionPanel()
     * @see TaskEditingDialogUI#TaskEditingDialogUI(java.util.HashMap, SingleTaskManager)
     */
    public WeekDays getWeekDay() {return weekDay;}

    /**
     * @return a {@link #priority} representing the task's priority.
     * @see PlannerTableManager#createPlannerModel()
     * @see PlannerTableManager#countPoints()
     * @see WeekDayPanelUI#createTaskPanel(SingleTaskManager)
     * @see TaskDialogUI#TaskDialogUI(java.util.HashMap, SingleTaskManager)
     */
    public byte getPriority() {return priority;}

    /**
     * @return a {@link #description} representing the task's description.
     * @see WeekDayPanelUI#createTaskPanel(SingleTaskManager)
     * @see TaskDialogUI#createDescriptionPanel()
     */
    public String getDescription() {return description;}
    //endregion

    //region Setters

    /**
     * Sets the task's name.
     *
     * @param taskName a {@link String} representing the new task name.
     * @see DialogListeners#createTasksComboBoxListener()
     */
    public void setTaskName(String taskName) {this.taskName = taskName;}

    /**
     * Sets the task's time.
     *
     * @param time a {@link String} representing the new task time
     * @see DialogListeners#createTimeSpinnerListener()
     */
    public void setTime(String time) {this.time = time;}

    /**
     * Sets the execution status of the task.
     *
     * @param done a boolean indicating whether the task is done or not
     * @see PlannerDataManager#cleanPlanner(HashSet, String)
     * @see DialogListeners#createIsDoneComboBoxListener()
     */
    public void setDone(boolean done) {this.isDone = done;}

    /**
     * Sets the task's weekday name.
     *
     * @param weekDay a {@link WeekDays} enum representing the new weekday name
     * @see DialogListeners#createWeekDaysComboBoxListener()
     */
    public void setWeekDay(WeekDays weekDay) {this.weekDay = weekDay;}

    /**
     * Sets the task's priority.
     *
     * @param priority a byte representing the new task priority
     * @see DialogListeners#createPrioritySpinnerListener()
     */
    public void setPriority(byte priority) {this.priority = priority;}

    /**
     * Sets the task's description.
     *
     * @param description a {@link String} representing the new task description
     * @see DialogListeners#createDescriptionTextAreaListener()
     */
    public void setDescription(String description) {this.description = description;}
    //endregion

    /**
     * @return a {@link String} representation of the {@link SingleTaskManager}
     */
    @Override
    public String toString() {
        return "taskName='" + taskName + '\'' +
                ", isDone=" + isDone +
                ", weekDay=" + weekDay +
                ", priority=" + priority +
                ", describe='" + description + '\'';
    }
}
