package com.floweektracker.tasksDatabase;

import javax.swing.event.*;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Represents the UI handling for {@link TasksDatabaseUI} instance, managing user interactions and events within the
 * {@link TasksDatabaseUI#taskDatabaseDialog}. This class encapsulates the logic for handling user interactions and
 * events in the tasks database UI.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #tasksDatabaseUI} - a {@link TasksDatabaseUI} reference representing the UI of the task names database
 *     </li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #setupListeners()} - adds listeners to all components of the
 *     {@link TasksDatabaseUI#taskDatabaseDialog}</li>
 * </ul>
 * Listeners creators:
 * <ul>
 *     <li>{@link #createButtonsListener()} - creates a listener for the {@link TasksDatabaseUI#buttons}</li>
 *     <li>{@link #createTasksJListListener()} - creates a listener for the {@link TasksDatabaseUI#tasksList}</li>
 *     <li>{@link #createTaskInputFieldListener()} - creates a listener for the {@link TasksDatabaseUI#taskInputField}
 *     </li>
 * </ul>
 */
class TasksDatabaseListeners {
    //region Fields
    /**
     * Contains a {@link TasksDatabaseUI} instance obtained from the parameter, providing components used in the
     * {@link TasksDatabaseUI#taskDatabaseDialog}.
     *
     * @see #TasksDatabaseListeners(TasksDatabaseUI)
     * @see #setupListeners()
     * @see #createButtonsListener()
     * @see #createTasksJListListener()
     * @see #createTaskInputFieldListener()
     */
    final TasksDatabaseUI tasksDatabaseUI;
    //endregion

    /**
     * Constructs a new {@link TasksDatabaseListeners} instance with the specified {@code tasksDatabaseUI}. This
     * constructor initializes reference to the {@link #tasksDatabaseUI}. Finally, it invokes the
     * {@link #setupListeners()} method to set up listeners for the components in the
     * {@link TasksDatabaseUI#taskDatabaseDialog}.
     *
     * @param tasksDatabaseUI a {@link TasksDatabaseUI} instance representing the UI of the task names database.
     * @see TasksDatabaseUI#setupTaskDialog()
     */
    TasksDatabaseListeners(TasksDatabaseUI tasksDatabaseUI) {
        this.tasksDatabaseUI = tasksDatabaseUI;

        setupListeners();
    }

    //region Method

    /**
     * Adds listeners to the components in the {@link TasksDatabaseUI#taskDatabaseDialog}. These listeners include those
     * for buttons, task list selection, and task input field changes.
     *
     * @see #TasksDatabaseListeners(TasksDatabaseUI)
     * @see #createButtonsListener()
     * @see #createTaskInputFieldListener()
     * @see #createTasksJListListener()
     */
    void setupListeners() {
        Arrays.stream(tasksDatabaseUI.getButtons()).forEach(button -> button.addActionListener(createButtonsListener()));
        tasksDatabaseUI.getTasksJList().addListSelectionListener(createTasksJListListener());
        tasksDatabaseUI.getTaskInputField().getDocument().addDocumentListener(createTaskInputFieldListener());
    }
    //endregion

    //region Listeners creators

    /**
     * Creates an {@link ActionListener} for the {@link TasksDatabaseUI#buttons}. This listener retrieves the task name
     * from the {@link TasksDatabaseUI#taskInputField} first and then determines which button was clicked.
     * <br><br>
     * If the adding button is clicked:
     * <ul>
     *     <li>the task is added to the database using {@link TasksDatabaseUI#addTaskToList(String)}</li>
     *     <li>the {@link TasksDatabaseUI#tasksList} is updated</li>
     *     <li>the {@link TasksDatabaseUI#taskInputField} is cleared</li>
     * </ul>
     * If the deleting button is clicked:
     * <ul>
     *     <li>the task is removed from the database using {@link TasksDatabaseUI#deleteTaskFromList(String)}</li>
     *     <li>the {@link TasksDatabaseUI#tasksList} is updated</li>
     *     <li>the {@link TasksDatabaseUI#taskInputField} is cleared</li>
     * </ul>
     * If the cancelling button is clicked, the {@link TasksDatabaseUI#taskInputField} is cleared.
     *
     * @return an {@link ActionListener} for the {@link TasksDatabaseUI#buttons}
     * @see #setupListeners()
     */
    private ActionListener createButtonsListener() {
        return buttonEvent -> {
            final String task = tasksDatabaseUI.getTaskInputField().getText();

            switch (buttonEvent.getActionCommand()) {
                case "Dodaj" -> {
                    if (task != null && !task.isBlank()) {
                        tasksDatabaseUI.addTaskToList(task);
                        tasksDatabaseUI.getTasksJList().setListData(tasksDatabaseUI.getTasksList());
                        tasksDatabaseUI.getTaskInputField().setText("");
                    }
                }
                case "Usuń" -> {
                    if (task != null && !task.isBlank()) {
                        tasksDatabaseUI.deleteTaskFromList(task);
                        tasksDatabaseUI.getTasksJList().setListData(tasksDatabaseUI.getTasksList());
                        tasksDatabaseUI.getTaskInputField().setText("");
                    }
                }
                case "Anuluj" -> tasksDatabaseUI.getTaskInputField().setText("");
            }
        };
    }

    /**
     * Creates a {@link ListSelectionListener} for the {@link TasksDatabaseUI#tasksList}. This listener checks if the
     * selected value in the {@link TasksDatabaseUI#tasksList} is adjusting. If it's not, it sets the selected value
     * into the {@link TasksDatabaseUI#taskInputField}.
     *
     * @return a {@link ListSelectionListener} for the {@link TasksDatabaseUI#tasksList}
     * @see #setupListeners()
     */
    private ListSelectionListener createTasksJListListener() {
        return listEvent -> {
            if (listEvent.getValueIsAdjusting()) return;

            var task = tasksDatabaseUI.getTasksJList().getSelectedValue();
            tasksDatabaseUI.getTaskInputField().setText(task);
        };
    }

    /**
     * Creates a {@link DocumentListener} for the {@link TasksDatabaseUI#taskInputField}. This listener monitors changes
     * in the {@link javax.swing.JTextField} and reacts accordingly.
     *
     * @return a {@link DocumentListener} for the {@link TasksDatabaseUI#taskInputField}
     * @see #setupListeners()
     */
    private DocumentListener createTaskInputFieldListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tasksDatabaseUI.getTaskInputField().getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tasksDatabaseUI.getTaskInputField().getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                tasksDatabaseUI.getTaskInputField().getText();
            }
        };
    }
    //endregion
}