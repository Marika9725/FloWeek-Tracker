package com.floweektracker.planner;

import com.floweektracker.main.*;
import com.floweektracker.planner.plannerManagerDialogs.*;
import com.google.gson.*;
import com.floweektracker.tasksDatabase.SingleTaskManager;

import java.io.*;
import java.util.*;

/**
 * Represents data management for a weekly planner. This class manages tasks scheduled in the planner and supports
 * saving/loading operations from a {@code Planner.json}.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #gson} - a {@link Gson} reference used in I/O operations</li>
 *     <li>{@link #plannerFile} - a {@link File} reference representing the {@code Planner.json} file containing planner
 *     data</li>
 *     <li>{@link #planners} - a {@link HashMap} containing tasks sorted by each day of the week</li>
 *     <li>{@link #weekDays} - an array of all the {@link WeekDays} enum values</li>
 *
 * </ul>
 * I/O operations:
 * <ul>
 *     <li>{@link #loadPlanner()} - loads tasks from the {@code Planner.json} file</li>
 *     <li>{@link #savePlanner()} - saves {@link #planners} to the {@code Planner.json} file</li>
 * </ul>
 * Task management operations:
 * <ul>
 *     <li>{@link #addTask()} - adds a task to the {@link #planners}</li>
 *     <li>{@link #cleanPlanner(HashSet, String)} - based on parameters, removes tasks or resets points for the
 *     specified days of the week</li>
 *     <li>{@link #deleteTask(String, String)} - removes a task from the {@link #planners}</li>
 *     <li>{@link #editTask(String, String)} - edits a task in the {@link #planners}</li>
 * </ul>
 *
 * Getters: {@link #getPlanners()}
 */
public class PlannerDataManager {
    //region Fields
    /**
     * Contains tasks sorted by each day of the week. Each key in the outer {@link HashMap} represents a day of the week
     * as a String (e.g. "Monday"). The value associated with each day is another {@link HashMap} where the
     * {@link String} represents an hour, and {@link SingleTaskManager} represents a task.
     * <br><br>
     * Example usage:
     * <pre>{@code
     * String weekDay = "MONDAY";
     * String hour = "12:30";
     *
     * HashMap<String, SingleTaskManager> weekDaySchedule = planners.get(weekDay);
     * SingleTaskManager task = weekDaySchedule.get(hour);
     * }</pre>
     *
     * @see #PlannerDataManager()
     * @see #loadPlanner()
     * @see #savePlanner()
     * @see #addTask()
     * @see #editTask(String, String)
     * @see #deleteTask(String, String)
     * @see #cleanPlanner(HashSet, String)
     * @see #getPlanners()
     * @see PlannerTableManager
     */
    private HashMap<String, HashMap<String, SingleTaskManager>> planners;
    /**
     * Contains an array of the enum values from the {@link WeekDays}. This array holds all the constants of the
     * {@link WeekDays} enum in the order they are declared.
     *
     * @see #PlannerDataManager()
     * @see #cleanPlanner(HashSet, String)
     */
    private final WeekDays[] weekDays = WeekDays.values();
    /**
     * Contains the {@code Planner.json} file reference used to store {@link #planners} data. This file is utilized for
     * loading and saving user-entered data within the application.
     *
     * @see #PlannerDataManager()
     * @see #loadPlanner()
     * @see #savePlanner()
     */
    private final File plannerFile;
    /**
     * Contains the {@link Gson} object used for loading and saving data from the {@link #plannerFile}.
     *
     * @see #PlannerDataManager()
     * @see #loadPlanner()
     * @see #savePlanner()
     */
    private final Gson gson;
    //endregion

    /**
     * Constructs a new {@link PlannerDataManager} instance. This constructor initializes references to
     * {@link #plannerFile} and {@link #gson}. It checks if the file exists. If it does, the {@link #loadPlanner()}
     * method is invoked. If not, a new {@link #planners} instance is initialized with empty mappings for each
     * {@link WeekDays}, and the {@link #savePlanner()} method is called to create the file.
     */
    PlannerDataManager() {
        this.plannerFile = new File(Main.plannerFolder.getPath(), "/Planner.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (plannerFile.exists()) loadPlanner();
        else {
            planners = new HashMap<>();
            Arrays.stream(weekDays).forEach(weekDay -> planners.put(weekDay.name(), new HashMap<>()));
            savePlanner();
        }
    }

    //region I/O operations

    /**
     * Loads data from the {@link #plannerFile} and assigns it to {@link #planners}. May throw an {@link IOException} if
     * there's an issue with input/output operations.
     *
     * @see #PlannerDataManager()
     */
    private void loadPlanner() {
        try (var reader = new FileReader(plannerFile)) {
            planners = gson.fromJson(reader, new HashMap<String, HashMap<String, SingleTaskManager>>() {}.getClass().getGenericSuperclass());
        } catch (IOException e) {
            Main.showErrorDialog("IOException", "Błąd wejścia-wyjścia. Nie można wczytać planera.");
        }
    }

    /**
     * Saves user-entered data from {@link #planners} to the {@link #plannerFile}. May throw an {@link IOException} if
     * there's an issue with input/output operations.
     *
     * @see com.floweektracker.main.MainListeners#configureWindowsAdapter()
     * @see #PlannerDataManager()
     */
    public void savePlanner() {
        try (var writer = new FileWriter(plannerFile)) {
            gson.toJson(planners, writer);
        } catch (IOException e) {
            Main.showErrorDialog("IOException", "Błąd wejścia-wyjścia. Nie można zapisać planera.");
        }
    }
    //endregion

    //region Task management operations

    /**
     * Adds task to the {@link #planners} by creating and displaying a new {@link TaskAddingDialogUI} dialog.
     *
     * @return {@code true} if the task was successfully added, otherwise {@code false}
     * @see PlannerTableManager
     * @see com.floweektracker.main.MainListeners#createButtonsListener()
     */
    public boolean addTask() {
        var dialog = new TaskAddingDialogUI(this.planners);

        return dialog.isTaskAdded();
    }

    /**
     * Edits the task chosen by the user by creating and displaying a new {@link TaskEditingDialogUI} dialog. The passed
     * parameters are used to get chosen task from the {@link #planners}.
     *
     * @param weekDayPL a {@link String} representing the Polish name of the weekday chosen by the user
     * @param time      a {@link String} representing the time of the task chosen by the user
     * @return {@code true} if the task was successfully edited, {@code false} otherwise
     * @see com.floweektracker.main.MainListeners#createPlannerListener()
     */
    public boolean editTask(final String weekDayPL, final String time) {
        var task = planners.get(WeekDays.getWeekDayEN(weekDayPL)).get(time);
        var dialog = new TaskEditingDialogUI(this.planners, task);

        return dialog.isTaskEdited();
    }

    /**
     * Deletes the task chosen by the user from {@link #planners}. The passed parameters are used to locate and remove
     * the chosen task.
     *
     * @param weekDayPL a {@link String} representing the Polish name of the weekday chosen by the user
     * @param time      a {@link String} representing the time of the task chosen by the user
     * @return {@code true} if the {@link #planners} still contains the deleted task, {@code false} otherwise
     * @see com.floweektracker.main.MainListeners#deleteTask()
     */
    public boolean deleteTask(final String weekDayPL, final String time) {
        planners.get(WeekDays.getWeekDayEN(weekDayPL)).remove(time);
        return planners.get(WeekDays.getWeekDayEN(weekDayPL)).containsKey(time);
    }

    /**
     * Removes all tasks or resets all points from the chosen weekdays in {@link #planners}. This method uses
     * {@link java.util.stream.Stream} to filter weekdays from {@link #weekDays} matching the values from the passed
     * {@code checkedWeekDays} parameter. Depending on the {@code buttonName} parameter, all tasks or points in the
     * filtered schedules are either deleted or reset.
     *
     * @param checkedWeekDays a {@link HashSet} containing weekdays chosen by the user
     * @param buttonName      a {@link String} representing the name of the button clicked by the user ("Wyczyść planer"
     *                        to clear all tasks, "Wyzeruj punkty" to reset all points)
     * @see com.floweektracker.main.MainListeners#cleanPlanner(String)
     */
    public void cleanPlanner(final HashSet<String> checkedWeekDays, final String buttonName) {
        Arrays.stream(weekDays)
                .filter(weekDay -> checkedWeekDays.contains(weekDay.getWeekDayPL()))
                .forEach(weekDay -> {
                    var times = planners.get(weekDay.name()).keySet().toArray(String[]::new);

                    if (buttonName.equals("Wyczyść planer"))
                        Arrays.stream(times).forEach(time -> planners.get(weekDay.name()).remove(time));
                    else if (buttonName.equals("Wyzeruj punkty"))
                        Arrays.stream(times).forEach(time -> planners.get(weekDay.name()).get(time).setDone(false));
                });
    }
    //endregion

    //region Getters

    /**
     * @return {@link #planners} containing user-entered tasks for each day of the week
     * @see com.floweektracker.main.MainListeners#createPlannerListener()
     * @see PlannerTableManager#PlannerTableManager()
     */
    public HashMap<String, HashMap<String, SingleTaskManager>> getPlanners() {return planners;}
    //endregion
}