package com.floweektracker.tasksDatabase;

import com.floweektracker.planner.plannerManagerDialogs.TaskDialogUI;
import com.google.gson.*;
import com.floweektracker.main.Main;

import java.io.*;
import java.util.TreeSet;

/**
 * Representing the manager for the task names database, which saves, loads and sorts task names in the
 * {@link #tasksListFile}. It is the base class for the {@link TasksDatabaseUI}.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #tasksList} - a {@link TreeSet} containing {@link String}s representing the task names</li>
 *     <li>{@link #gson} - a {@link Gson} reference used to save and load tasks from the {@link #tasksListFile}</li>
 *     <li>{@link #tasksListFile} - a {@link File} reference providing access to the {@code TasksList.json} file</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #addTaskToList(String)} - adds a task to the {@link #tasksList} and {@link #tasksListFile}</li>
 *     <li>{@link #deleteTaskFromList(String)} - deletes a task from the {@link #tasksList} and {@link #tasksListFile}
 *     </li>
 * </ul>
 *
 * Getters: {@link #getTasksList()}
 */
public class TasksDatabaseManager {
    //region Fields
    /**
     * Contains a {@link String} representing the task names. They are sorted alphabetically by the
     * {@link TasksListComparator}.
     *
     * @see #TasksDatabaseManager()
     * @see #addTaskToList(String)
     * @see #deleteTaskFromList(String)
     * @see #getTasksList()
     */
    private TreeSet<String> tasksList;
    /**
     * Contains a {@link GsonBuilder} used in the {@link #addTaskToList(String)} and {@link #deleteTaskFromList(String)}
     * methods to add and remove task names from the {@link #tasksListFile}. It is also used in the
     * {@link TasksDatabaseManager} constructor to load task names from the {@link #tasksListFile}.
     */
    private final Gson gson;
    /**
     * Provides access to the {@code TasksList.json} file which contains task names from the {@link #tasksList}.
     *
     * @see #TasksDatabaseManager()
     * @see #addTaskToList(String)
     * @see #deleteTaskFromList(String)
     */
    private final File tasksListFile;
    //endregion

    /**
     * Constructs a new {@link TasksDatabaseManager} instance. This constructor initializes the {@link #gson} and
     * {@link #tasksListFile} references. It then checks whether the {@link #tasksListFile} exists. If it does,the
     * content from this file is loaded into the {@link #tasksList} and sorted alphabetically by the
     * {@link TasksListComparator}. Otherwise, a new {@link TreeSet} instance is created.
     * <br><br>
     * May produce an exception. If it does, the {@link javax.swing.JDialog} with information will be shown to the user.
     * The expected structure of the {@code TasksList.json} file is a JSON array of task name strings.
     *
     * @see TaskDialogUI#TaskDialogUI(java.util.HashMap, SingleTaskManager)
     * @see TasksDatabaseUI
     * @see Main#showErrorDialog(String, String)
     */
    public TasksDatabaseManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        tasksListFile = new File(Main.plannerFolder, "/TasksList.json");

        if (tasksListFile.exists()) {
            try (var reader = new FileReader(tasksListFile)) {
                tasksList = gson.fromJson(reader, new TreeSet<>(new TasksListComparator()) {}.getClass().getGenericSuperclass());
            } catch (IOException e) {
                Main.showErrorDialog(
                        "Błąd wczytywania pliku",
                        "Wystąpił błąd w trakcie ładowania zadań z pliku: <html><u>" + tasksListFile.getPath() + "</u></html>."
                );
                tasksList = new TreeSet<>(new TasksListComparator());
            }
        } else {tasksList = new TreeSet<>(new TasksListComparator());}
    }

    //region Methods

    /**
     * Adds a {@code task} to the {@link #tasksList} and updates the {@link #tasksListFile}. It checks whether the
     * {@code task} is successfully added to the {@link #tasksList}. If it is, this task is then saved in the
     * {@link #tasksListFile}.
     * <br><br>
     * May produce an exception. If it does, the {@link javax.swing.JDialog} with information will be shown to the user.
     * The expected structure of the {@code TasksList.json} file is a JSON array of task name strings.
     *
     * @param task a {@link String} representing a task name entered by the user
     * @see TasksDatabaseListeners#createButtonsListener()
     * @see Main#showErrorDialog(String, String)
     */
    void addTaskToList(String task) {
        try (var writer = new FileWriter(tasksListFile)) {
            if (this.tasksList.add(task)) gson.toJson(tasksList, writer);
        } catch (IOException e) {
            Main.showErrorDialog("Błąd zapisywania zadania", "Zapisywanie zadania do bazy zakończyło się niepowodzeniem.");
        }
    }

    /**
     * Deletes a {@code task} from the {@link #tasksList} and updates the {@link #tasksListFile}. It checks whether the
     * {@code task} is successfully removed from the {@link #tasksList}. If it is, this task is then removed from the
     * {@link #tasksListFile}.
     * <br><br>
     * May produce an exception. If it does, the {@link javax.swing.JDialog} with information will be shown to the user.
     * The expected structure of the {@code TasksList.json} file is a JSON array of task name strings.
     *
     * @param task a {@link String} representing a task name entered by the user
     * @see TasksDatabaseListeners#createButtonsListener() 
     * @see Main#showErrorDialog(String, String) 
     */
    void deleteTaskFromList(String task) {
        if (!tasksList.isEmpty() && tasksList.remove(task)) {
            try (var writer = new FileWriter(tasksListFile)) {
                gson.toJson(tasksList, writer);
            } catch (IOException e) {
                Main.showErrorDialog("Błąd usuwania zadania", "Usuwanie zadania z bazy zakończyło się niepowodzeniem.");
            }
        }
    }
    //endregion

    //region Getters

    /**
     * @return an array of the {@link String}s representing the task names from the {@link #tasksList}.
     * @see TaskDialogUI#TaskDialogUI(java.util.HashMap, SingleTaskManager)
     * @see TasksDatabaseListeners#createButtonsListener()
     * @see TasksDatabaseUI#TasksDatabaseUI()
     */
    public String[] getTasksList() {return this.tasksList.toArray(new String[0]);}
    //endregion
}
