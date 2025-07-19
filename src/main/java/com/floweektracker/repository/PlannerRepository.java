package com.floweektracker.repository;


import com.floweektracker.model.*;
import com.floweektracker.util.DialogUtils;
import com.google.gson.*;
import lombok.*;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

/**
 * Represents the repository of the planner. Repository is a singleton with no args constructor. This class supports
 * saving and loading operations from a {@link #file} in {@code json} format.
 * <br><br>
 * Fields: {@link #repository}, {@link #schedule}, {@link #file}, {@link #gson}
 * <br><br>
 * Methods: {@link #savePlanner()}, {@link #loadPlanner()}}
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlannerRepository {
    /**
     * A singleton instance of the repository.
     *
     * @see com.floweektracker.Main
     * @see RepositoryConfigurator
     * @see com.floweektracker.service.TasksService
     */
    @Getter
    private static final PlannerRepository repository = new PlannerRepository();
    /**
     * Contains tasks sorted by each day of the week. Each key in the outer map represents a day of the week as a String
     * (e.g. "Monday"). The value associated with each day is another map where the {@link LocalTime} represents an
     * hour, and {@link SingleTask} represents a task.
     * <br><br>
     * Schedule can be set by calling {@link #setSchedule(Map)} or {@link #loadPlanner()}.
     *
     * @see #savePlanner()
     * @see #loadPlanner()
     * @see com.floweektracker.service.TasksService
     */
    private Map<WeekDays, Map<LocalTime, SingleTask>> schedule;
    /**
     * Contains a path to the file reference used to store {@link #schedule} data. This file is created to loading and
     * saving user-entered data within the application.
     * <br><br>
     * This file can be set by calling {@link #setFile(File)} method.
     *
     * @see RepositoryConfigurator
     * @see #savePlanner()
     * @see #loadPlanner()
     */
    private File file;
    /**
     * Contains the {#link Gson} object used for saving data from {@link #schedule} to {@link #file} with json format,
     * and loading data from json {@link #file)} to {@link #schedule}.
     *
     * @see #savePlanner()
     * @see #loadPlanner()
     */
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .setPrettyPrinting()
            .create();

    /**
     * Save user-entered data from {@link #schedule} to the {@link #file}. May throw an {@link NullPointerException} if
     * schedule is null. When it throws an exception, it is caught and a message is displayed to the user, and then
     * return false.
     *
     * @return true if the file is not empty, false otherwise
     * @see DialogUtils#showMessageDialog(String, String)
     * @see com.floweektracker.service.TasksService
     */
    public boolean savePlanner() {
        try (var writer = new FileWriter(file)) {
            if (schedule == null) throw new NullPointerException("Schedule or file is null");

            gson.toJson(schedule, writer);
        } catch (NullPointerException | IOException e) {
            DialogUtils.showMessageDialog("Błąd zapisywania", "Lista nazw zadań nie została prawidłowo zapisana");

            return false;
        }

        return true;
    }

    /**
     * Loads data from the {@link #file} and assigns it to {@link #schedule}. If file is empty or doesn't exist, it
     * returns false. When it throws an exception, it is caught and a message is displayed to the user, and returns
     * false.
     *
     * @return true if the schedule isn't empty, false otherwise
     * @see com.floweektracker.Main
     * @see RepositoryConfigurator
     * @see DialogUtils#showMessageDialog(String, String)
     */
    public boolean loadPlanner() {
        if (file == null || !file.exists()) return false;

        try (var reader = new FileReader(file)) {
            schedule = gson.fromJson(reader, new HashMap<WeekDays, HashMap<LocalTime, SingleTask>>() {}.getClass().getGenericSuperclass());
        } catch (NullPointerException | IOException e) {
            schedule = null;
            DialogUtils.showMessageDialog("Błąd odczytu", "Lista zadań nie została prawidłowo odczytana.");
        }

        return schedule != null;
    }
}