package com.floweektracker.service;

import com.floweektracker.controller.*;
import com.floweektracker.model.*;
import com.floweektracker.repository.PlannerRepository;
import com.floweektracker.util.DialogUtils;
import com.floweektracker.view.PlannerView;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Represents a service for handling tasks. This class is a singleton and is used to store and manage tasks.
 * <br><br>
 * Fields: {@link #service}, {@link #schedule}, {@link #plannerRepository}
 * <br><br>
 * CRUD methods: {@link #addTask(SingleTask)}, {@link #deleteTask(SingleTask)},
 * {@link #editTask(SingleTask, SingleTask)}, {@link #savePlanner()}
 * <br><br>
 * Getter methods: {@link #getTaskByEventTime(WeekDays, LocalTime)}, {@link #getTasksFromWeekday(WeekDays)}
 * <br><br>
 * Other methods: {@link #initSchedule()}, {@link #collectSortedTimes()}, {@link #countPoints(WeekDays, Function)},
 * {@link #isTaskInSchedule(SingleTask)}, {@link #isTimeOccupied(SingleTask)}
 */
@Getter
public class TasksService {
    /**
     * The singleton instance of the {@link TasksService} class.
     *
     * @see com.floweektracker.MainFrame
     * @see com.floweektracker.controller.MainPanelController
     * @see com.floweektracker.controller.PlannerController
     * @see com.floweektracker.controller.TaskAddingDialogController
     * @see com.floweektracker.controller.TaskEditingDialogController
     * @see PlannerService
     * @see com.floweektracker.view.PlannerView
     * @see com.floweektracker.view.WeekdayPlannerView
     */
    @Getter
    private static final TasksService service = new TasksService();
    /**
     * Contains tasks sorted by each day of the week. Each key in the outer map represents a day of the week as a String
     * (e.g. "Monday"). The value associated with each day is another map where the {@link LocalTime} represents an
     * hour, and {@link SingleTask} represents a task.
     *
     * @see PlannerRepository
     * @see com.floweektracker.controller.MainPanelController
     * @see com.floweektracker.view.WeekdayPlannerView
     * @see #initSchedule()
     * @see #addTask(SingleTask)
     * @see #deleteTask(SingleTask)
     * @see #editTask(SingleTask, SingleTask)
     * @see #collectSortedTimes()
     * @see #countPoints(WeekDays, Function)
     * @see #isTaskInSchedule(SingleTask)
     * @see #getTaskByEventTime(WeekDays, LocalTime)
     * @see #getTasksFromWeekday(WeekDays)
     * @see #savePlanner()
     * @see #isTimeOccupied(SingleTask)
     */
    private final Map<WeekDays, Map<LocalTime, SingleTask>> schedule = new HashMap<>();
    /**
     * A singleton instance of the {@link PlannerRepository} class.
     *
     * @see #initSchedule()
     * @see #savePlanner()
     */
    private final PlannerRepository plannerRepository = PlannerRepository.getRepository();

    /**
     * Private constructor to create a singleton instance of the {@link TasksService} class. Calls
     * {@link #initSchedule()} method.
     *
     * @see TasksService
     */
    private TasksService() {
        initSchedule();
    }

    /**
     * Initializes the {@link #schedule} by loading it from the {@link #plannerRepository} or creating a new empty
     * schedule if no schedule is found.
     *
     * @see TasksService#TasksService()
     */
    private void initSchedule() {
        var loadedSchedule = plannerRepository.getSchedule();

        if (loadedSchedule != null) schedule.putAll(loadedSchedule);
        else {
            for (WeekDays day : WeekDays.values()) schedule.put(day, new HashMap<>());
        }
    }

    /**
     * Add task to the {@link #schedule}. If the task is null or the time is occupied, return false.
     *
     * @param task represents the task to be added
     * @return true if task is successfully added to schedule, otherwise false.
     * @see #isTimeOccupied(SingleTask)
     * @see #isTaskInSchedule(SingleTask)
     * @see com.floweektracker.controller.TaskAddingDialogController
     */
    public boolean addTask(SingleTask task) {
        if (task == null || isTimeOccupied(task)) return false;
        schedule.get(task.getWeekday()).put(task.getTime(), task);

        return isTaskInSchedule(task);
    }

    /**
     * Delete task from the {@link #schedule}. If the task is not exist or the time is not occupied, return false.
     *
     * @param task represents the task to be deleted
     * @return true if task is successfully deleted, otherwise false.
     * @see #isTimeOccupied(SingleTask)
     * @see #isTaskInSchedule(SingleTask)
     * @see com.floweektracker.controller.MainPanelController
     * @see com.floweektracker.controller.TaskAddingDialogController
     */
    public boolean deleteTask(SingleTask task) {
        if ((task == null) || !isTimeOccupied(task)) return false;

        return schedule.get(task.getWeekday()).remove(task.getTime(), task);
    }

    /**
     * Edits a task in the {@link #schedule} by replacing the old task with a new task. The operation involves deleting
     * the old task and adding the new one. If any of the following conditions are met, the method returns false:
     * <ul>
     *     <li>Either the old task or the new task is null.</li>
     *     <li>The old task and new task are identical.</li>
     *     <li>The old task is not found in the schedule.</li>
     *     <li>The new task is already in the schedule.</li>
     * </ul>
     * The method attempts to delete the old task and add the new task. If the addition fails, it will attempt to
     * roll back.
     *
     * @param task       represents an old task to be replaced.
     * @param editedTask represents a new task to be added.
     * @return true if the task is successfully edited, otherwise false.
     * @see #isTaskInSchedule(SingleTask)
     * @see #deleteTask(SingleTask)
     * @see #addTask(SingleTask)
     * @see DialogUtils#rollback(SingleTask, Consumer, Function)
     * @see com.floweektracker.controller.MainPanelController
     * @see com.floweektracker.controller.TaskEditingDialogController
     */
    public boolean editTask(SingleTask task, SingleTask editedTask) {
        if ((task == null) || (editedTask == null) || task.equals(editedTask)) return false;

        if (!deleteTask(task)) return false;
        if (!addTask(editedTask)) return !DialogUtils.rollback(task, this::addTask, this::isTaskInSchedule);

        return !isTaskInSchedule(task) && isTaskInSchedule(editedTask);
    }

    /**
     * Collects all the local times from the {@link #schedule} into a sorted set
     *
     * @return a {@link TreeSet} with all the local times in the schedule. The set is sorted in ascending order.
     * @see PlannerView#createPlannerModel()
     */
    public TreeSet<LocalTime> collectSortedTimes() {
        return schedule.values().stream()
                .flatMap(tasks -> tasks.keySet().stream())
                .collect(Collectors.toCollection(TreeSet::new));
    }


    /**
     * Counts the points of the tasks in the schedule based on the given {@link Function}.
     * The function takes a task and returns the points of the task. The points are then summed up.
     * The method returns the sum of points of all the tasks in the given weekday.
     *
     * @param weekday    the weekday to count the points from
     * @param singleTaskMethod the function to apply to each task
     * @return the sum of points of all the tasks in the given weekday
     * @see PlannerService#actualizeSummarizeForWeekday(WeekDays)
     * @see PlannerView#buildPointsRow()
     */
    public int countPoints(WeekDays weekday, Function<SingleTask, Byte> singleTaskMethod) {
        if (singleTaskMethod == null) return 0;
        return schedule.get(weekday).values().stream().mapToInt(singleTaskMethod::apply).sum();
    }

    /**
     * Checks if the given task is in the schedule. Returns false if task is null.
     *
     * @param task a given task to check
     * @return true if the task is in the schedule, otherwise false
     * @see com.floweektracker.controller.TaskAddingDialogController#isTaskAdded(SingleTask)
     * @see com.floweektracker.controller.TaskEditingDialogController#isTaskEdited(SingleTask, SingleTask)
     * @see #addTask(SingleTask)
     * @see #deleteTask(SingleTask)
     * @see #editTask(SingleTask, SingleTask)
     */
    public boolean isTaskInSchedule(SingleTask task) {
        if (task == null) return false;
        return schedule.get(task.getWeekday()).containsValue(task);
    }

    /**
     * Find a {@link SingleTask} in the {@link #schedule} by given weekday and time
     *
     * @param weekday a give weekday
     * @param time a given time
     * @return a {@link SingleTask} if it is found in the {@link #schedule}, otherwise null
     * @see MainPanelController#deleteTask()
     * @see PlannerController#editTask()
     * @see PlannerView#buildRowData(LocalTime)
     */
    public SingleTask getTaskByEventTime(WeekDays weekday, LocalTime time) {
        return schedule.get(weekday).get(time);
    }

    /**
     * Get all the {@link SingleTask}s from the {@link #schedule} by given weekday.
     *
     * @param weekday a given weekday
     * @return a {@link List} with all the {@link SingleTask}s from the given weekday
     * @see com.floweektracker.controller.MainPanelController#deleteTasksForWeekdays(List)
     * @see com.floweektracker.controller.MainPanelController#resetPoints(List)
     */
    public List<SingleTask> getTasksFromWeekday(WeekDays weekday) {
        return schedule.get(weekday).values().stream().toList();
    }

    /**
     * Sets actual {@link #schedule} in the {@link PlannerRepository} and then saves it to the database.
     * @see com.floweektracker.MainFrame
     */
    public void savePlanner() {
        plannerRepository.setSchedule(schedule);
        plannerRepository.savePlanner();
    }

    //region helper methods
    /**
     * Checks if the given time is occupied in the {@link #schedule}.
     *
     * @param task a given tas
     * @return true if time is occupied by another task, otherwise false
     * @see #addTask(SingleTask)
     * @see #deleteTask(SingleTask)
     */
    private boolean isTimeOccupied(@NotNull SingleTask task) {
        return schedule.get(task.getWeekday()).containsKey(task.getTime());
    }
    //endregion
}