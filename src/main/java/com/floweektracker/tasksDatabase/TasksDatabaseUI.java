package com.floweektracker.tasksDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Represents the UI of the task names database allowing users to add and remove task names in a database. This class
 * extends from the {@link TasksDatabaseManager} class to leverage database management functionality.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #taskDatabaseDialog} - a {@link JDialog} representing the dialog allowing the user to add or remove a
 *     task name in the database, displayed to the user</li>
 *     <li>{@link #taskInputField} - a {@link JTextField} allowing the user to enter the task name which can be add or
 *     remove from the database</li>
 *     <li>{@link #buttons} - an array of {@link JButton}s representing adding, removing, and cancelling buttons /li>
 *     <li>{@link #tasksList} - a {@link JList} reference containing {@link String}s representing the existing list of
 *     task names</li>
 * </ul>
 * Dialog creating methods:
 * <ul><li>{@link #setupTaskDialog()} - configures {@link #taskDatabaseDialog} by adding dedicated components and
 * settings</li></ul>
 * Left panel creating methods:
 * <ul><li>{@link #createLeftPanel()} - creates {@link JPanel} for {@link #taskDatabaseDialog} containing
 * {@link #tasksList}</li></ul>
 * Right panel creating methods:
 * <ul>
 *     <li>{@link #createRightPanel()} - creates a {@link JPanel} for {@link #taskDatabaseDialog} containing
 *     {@link #taskInputField} and {@link #buttons}</li>
 *     <li>{@link #createTaskAddingPanel()} - creates a {@link JPanel} containing {@link #taskInputField}, used in the
 *     {@link #createRightPanel()} method</li>
 *     <li>{@link #createButtonsPanel()} - creates a {@link JPanel} containing {@link #buttons}, used in the
 *     {@link #createRightPanel()} method</li>
 * </ul>
 */
public class TasksDatabaseUI extends TasksDatabaseManager {
    //region Fields
    /**
     * Contains a {@link JDialog} instance representing the UI dialog allowing the user to add or remove a task name in
     * the database. It is displayed to the user to manage task names.
     *
     * @see #TasksDatabaseUI()
     * @see #setupTaskDialog()
     */
    private final JDialog taskDatabaseDialog;
    /**
     * Contains a {@link JTextField} instance allowing the user to enter the task name which can be added or removed in
     * the database. It is used in the UI to provide functionality for managing task names.
     *
     * @see #TasksDatabaseUI()
     * @see #createTaskAddingPanel()
     * @see #getTaskInputField()
     */
    private final JTextField taskInputField;
    /**
     * Contains an array of the {@link JButton}s representing the buttons for adding, removing, and cancelling tasks.
     * This array is used in the UI to provide functionality for managing task names.
     *
     * @see #TasksDatabaseUI()
     * @see #createButtonsPanel()
     * @see #getButtons()
     */
    private final JButton[] buttons;

    /**
     * Contains a {@link JList} instance with {@link String}s representing the existing task names. It is used in the UI
     * to display and manage the list of task names.
     *
     * @see #TasksDatabaseUI()
     * @see #createLeftPanel()
     * @see #getTasksList()
     */
    private final JList<String> tasksList;
    //endregion

    /**
     * Constructs a new {@link TasksDatabaseUI} instance. This constructor initializes references to the
     * {@link #taskDatabaseDialog}, {@link #taskInputField}, {@link #buttons}, and {@link #tasksList}. Finally, it
     * invokes the {@link #setupTaskDialog()} method to add components and setting to the {@link #taskDatabaseDialog}.
     *
     * @see com.floweektracker.main.MainListeners#createButtonsListener()
     */
    public TasksDatabaseUI() {
        this.taskDatabaseDialog = new JDialog();
        this.taskInputField = new JTextField(32);
        this.buttons = new JButton[]{new JButton("Dodaj"), new JButton("Usuń"), new JButton("Anuluj")};
        this.tasksList = new JList<>(getTasksList());

        setupTaskDialog();
    }

    //region Dialog creating method

    /**
     * Adds components and settings to the {@link #taskDatabaseDialog}. Sets the layout, size, title, and modality.
     * Then, it adds rigid areas and {@link JPanel}s from the {@link #createLeftPanel()} and
     * {@link #createRightPanel()}. Next, it creates new {@link TasksDatabaseListeners} to add listeners to the
     * components. Finally, it invokes the {@code pack()} method to resize the window, center it and set its
     * visibility.
     *
     * @see #TasksDatabaseUI()
     */
    private void setupTaskDialog() {
        taskDatabaseDialog.setLayout(new FlowLayout());
        taskDatabaseDialog.setSize(400, 400);
        taskDatabaseDialog.setTitle("Dodaj zadanie do bazy");
        taskDatabaseDialog.setModal(true);

        taskDatabaseDialog.add(Box.createRigidArea(new Dimension(20, 10)));
        taskDatabaseDialog.add(createLeftPanel());
        taskDatabaseDialog.add(Box.createRigidArea(new Dimension(20, 10)));
        taskDatabaseDialog.add(createRightPanel());
        taskDatabaseDialog.add(Box.createRigidArea(new Dimension(20, 20)));

        new TasksDatabaseListeners(this);

        taskDatabaseDialog.pack();
        taskDatabaseDialog.setLocationRelativeTo(null);
        taskDatabaseDialog.setVisible(true);
    }

    //region Left panel creating methods

    /**
     * Creates a {@link JPanel} containing a {@link JLabel} informing the user about the list content, and a
     * {@link JScrollPane} with the {@link #tasksList}, providing a scrollable view of the task names.
     *
     * @return a {@link JPanel} representing the left part of the {@link #taskDatabaseDialog}
     * @see #setupTaskDialog()
     */
    private JPanel createLeftPanel() {
        var tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.add(new JLabel("Dostępne zadania:"));
        tasksPanel.add(new JScrollPane(tasksList));

        var leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(tasksPanel);

        return leftPanel;
    }
    //endregion

    //region Right panel creating methods

    /**
     * Creates a {@link JPanel} containing panels from the {@link #createTaskAddingPanel()} and
     * {@link #createButtonsPanel()}, and vertical space at the top and bottom for visual separation.
     *
     * @return a {@link JPanel} representing the right part of the {@link #taskDatabaseDialog}
     * @see #setupTaskDialog()
     */
    private JPanel createRightPanel() {
        var rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(Box.createVerticalStrut(100));
        rightPanel.add(createTaskAddingPanel());
        rightPanel.add(createButtonsPanel());
        rightPanel.add(Box.createVerticalStrut(100));

        return rightPanel;
    }

    /**
     * Creates a {@link JPanel} containing:
     * <ul>
     *     <li>a {@link JLabel} informing the user about the {@link #taskInputField} content</li>
     *     <li>the {@link #taskInputField} to allow user input</li>
     * </ul>
     *
     * @return a {@link JPanel} representing the top part of the panel from the {@link #createRightPanel()}
     */
    private JPanel createTaskAddingPanel() {
        var taskAddingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        taskAddingPanel.add(new JLabel("Zadanie"));
        taskAddingPanel.add(taskInputField);

        return taskAddingPanel;
    }

    /**
     * Creates a {@link JPanel} containing {@link #buttons}.
     *
     * @return a {@link JPanel} containing the buttons defined in {@link #buttons}.
     */
    private JPanel createButtonsPanel() {
        var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        Arrays.stream(buttons).forEach(buttonsPanel::add);

        return buttonsPanel;
    }
    //endregion

    //endregion

    //region Getters

    /** @return the {@link JTextField} used for task input */
    JTextField getTaskInputField() {return taskInputField;}

    /** @return the array of {@link JButton} objects representing the buttons */
    JButton[] getButtons() {return buttons;}

    /** @return the {@link JList} of {@link String} representing the task names list */
    JList<String> getTasksJList() {return tasksList;}
    //endregion
}
