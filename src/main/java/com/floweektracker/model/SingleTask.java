package com.floweektracker.model;

import lombok.*;

import java.time.LocalTime;

/**
 * Represents the {@link SingleTask} object in the application. Contains field with information about the task and
 * access methods for it.
 * <br><br>
 * Fields: {@link #taskName}, {@link #description}, {@link #time}, {@link #isDone}, {@link #weekday}, {@link #priority}
 * <br><br>
 * Methods: {@link #calculatePoints()}, {@link #copy}, {@link #setPriority(byte)},
 * {@link #areEventTimeAndPriorityCorrect(LocalTime, WeekDays, byte)}
 */
@Data
public class SingleTask {
    private String taskName;
    private String description;
    private LocalTime time;
    private boolean isDone;
    private WeekDays weekday;
    @Setter(AccessLevel.NONE)
    private byte priority;

    /**
     * Public constructor for the {@link SingleTask} object. Checks if the {@link #time}, {@link #weekday} and
     * {@link #priority} are correct. If not, throws and {@link IllegalArgumentException}.
     *
     * @param taskName    a name of the task
     * @param description a description of the task
     * @param time        a time of the task
     * @param isDone      a flag if the task is done
     * @param weekday     a day of the week
     * @param priority    a priority of the task
     * @see #areEventTimeAndPriorityCorrect(LocalTime, WeekDays, byte)
     * @see com.floweektracker.view.TaskAddingDialog
     * @see com.floweektracker.view.TaskEditingDialog
     * @see com.floweektracker.service.TasksService
     */
    public SingleTask(String taskName, String description, LocalTime time, boolean isDone, WeekDays weekday, byte priority) {
        if (!areEventTimeAndPriorityCorrect(time, weekday, priority))
            throw new IllegalArgumentException("Event time shouldn't be null");

        this.taskName = taskName;
        this.description = description;
        this.time = time;
        this.isDone = isDone;
        this.weekday = weekday;
        this.priority = priority;
    }

    /**
     * Checks if the {@link #time}, {@link #weekday} are not null, and if the {@link #priority} is in the range.
     *
     * @param time     a time of the task
     * @param weekday  a day of the week
     * @param priority a priority of the task
     * @return true if event time is not null, otherwise false
     * @see SingleTask(String, String, LocalTime, boolean, WeekDays, byte)
     */
    private boolean areEventTimeAndPriorityCorrect(LocalTime time, WeekDays weekday, byte priority) {
        var isEventTimeNotNull = (time != null) && (weekday != null);
        var isPriorityInRange = (priority >= 1) && (priority <= 10);

        return isEventTimeNotNull && isPriorityInRange;
    }

    /**
     * @return {#link priority} if the task is done, otherwise 0
     * @see com.floweektracker.service.PlannerService
     * @see com.floweektracker.service.WeekdayPlannerService
     * @see com.floweektracker.view.PlannerView
     * @see com.floweektracker.view.WeekdayPlannerView
     */
    public byte calculatePoints() {
        return this.isDone() ? priority : 0;
    }

    /**
     * Creates a copy of the current {@link SingleTask} instance.
     *
     * @return a new instance of {@link SingleTask} with the same values as the current instance
     */
    public SingleTask copy() {
        return new SingleTask(this.taskName, this.description, this.time, this.isDone, this.weekday, this.priority);
    }

    /**
     * Set given priority to the {@link #priority} field. Throws an {@link IllegalArgumentException} if the priority is
     * out of range.
     *
     * @param priority a priority of the task
     */
    public void setPriority(byte priority) {
        if ((priority < 1) || (priority > 10)) throw new IllegalArgumentException("Priority is out of range");
        else this.priority = priority;
    }
}