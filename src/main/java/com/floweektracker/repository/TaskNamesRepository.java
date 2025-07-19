package com.floweektracker.repository;

import com.floweektracker.service.TaskNamesService;
import com.floweektracker.util.DialogUtils;
import com.google.gson.*;
import lombok.*;

import java.io.*;
import java.util.*;

/**
 * Represents the repository of the task names. Repository is a singleton with no args constructor. This class supports
 * saving and loading operations from a {@link #file} in {@code json} format.
 * <br><br>
 * Fields: {@link #repository}, {@link #taskNames}, {@link #file}, {@link #gson}
 * <br><br>
 * Methods: {@link #saveTaskNames()} ()}, {@link #loadTaskNames()} ()}}
 */
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskNamesRepository {
    /**
     * A singleton instance of the repository.
     *
     * @see com.floweektracker.Main
     * @see RepositoryConfigurator
     * @see com.floweektracker.service.TaskNamesService
     */
    @Getter (AccessLevel.PUBLIC)
    private static final TaskNamesRepository repository = new TaskNamesRepository();
    /**
     * Contains task names.It can be set by calling {@link #setTaskNames(Set)} or {@link #loadTaskNames()}.
     *
     * @see #saveTaskNames()
     * @see #loadTaskNames()
     * @see com.floweektracker.service.TaskNamesService
     */
    private Set<String> taskNames;
    /**
     * Contains a path to the file reference used to store {@link #taskNames} data. This file is created to loading and
     * saving user-entered data within the application.
     * <br><br>
     * This file can be set by calling {@link #setFile(File)} method.
     *
     * @see RepositoryConfigurator
     * @see #saveTaskNames()
     * @see #loadTaskNames()
     */
    private File file;
    /**
     * Contains the {#link Gson} object used for saving data from {@link #taskNames} to {@link #file} with json format,
     * and loading data from json {@link #file)} to {@link #taskNames}.
     *
     * @see #saveTaskNames()
     * @see #loadTaskNames()
     */
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Save user-entered data from {@link #taskNames} to the {@link #file}. May throw an {@link NullPointerException} if
     * {@link #taskNames} is null. When it throws an exception, it is caught and a message is displayed to the user, and
     * then returns false.
     *
     * @return true if the file is not empty, false otherwise
     * @see DialogUtils#showMessageDialog(String, String)
     * @see TaskNamesService#saveTaskNames()
     */
    public boolean saveTaskNames() {
        try (var writer = new FileWriter(file)) {
            if (taskNames == null) throw new NullPointerException("TaskNames or file is null");

            gson.toJson(taskNames, writer);
        } catch (NullPointerException | IOException e) {
            DialogUtils.showMessageDialog("Błąd zapisywania", "Lista nazw zadań nie została prawidłowo zapisana");

            return false;
        }

        return true;
    }

    /**
     * Loads data from the {@link #file} and assigns it to {@link #taskNames}. If file is empty or doesn't exist, it
     * returns false. When it throws an exception, it is caught and a message is displayed to the user, and returns
     * false.
     *
     * @return true if the {@link #taskNames} isn't empty, false otherwise
     * @see com.floweektracker.Main
     * @see RepositoryConfigurator
     * @see DialogUtils#showMessageDialog(String, String)
     */
    public boolean loadTaskNames() {
        if (file == null || !file.exists()) return false;

        try (var reader = new FileReader(file)) {
            taskNames = gson.fromJson(reader, new TreeSet<String>() {}.getClass().getGenericSuperclass());
        } catch (NullPointerException | IOException e) {
            taskNames = null;
            DialogUtils.showMessageDialog("Błąd odczytu", "Lista nazw zadań nie została prawidłowo odczytana.");
        }

        return taskNames != null;
    }
}
