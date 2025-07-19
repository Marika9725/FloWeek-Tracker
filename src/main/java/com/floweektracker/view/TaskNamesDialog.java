package com.floweektracker.view;

import com.floweektracker.controller.MainPanelController;
import com.floweektracker.service.TaskNamesService;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Represents the UI of the task names database allowing users to add and remove task names in a database. It shows a
 * list of task names. It also provides possibility to add or remove the task name. This class is a singleton.
 * <br><br>
 * Fields:  {@link #view}, {@link #dialog}, {@link #taskNamesScrollPane}, {@link #taskNameInputField}, {@link #buttons},
 * {@link #taskNamesList}, {@link #visible}
 * <br><br>
 * Dialog creating methods: {@link #initTaskNamesDialog()}
 * <br><br>
 * Left panel creating methods: {@link #createLeftPanel()}, {@link #createTaskNamesScrollPane()}
 * <br><br>
 * Right panel creating methods: {@link #createRightPanel()}, {@link #createTaskAddingPanel()},
 * {@link #createTaskNameInputField()}, {@link #createButtonsPanel()}, {@link #createButton(String, String)}
 * <br><br>
 * Helper methods: {@link #createPanel(String, LayoutManager)}, {@link #createLabel(String, String)}
 * <br><br>
 * Getter methods: {@link #getTaskNameFromInputField()}, {@link #getSelectedTaskNameFromTaskNamesList()}
 * <br><br>
 * Setter methods: {@link #setTaskNameInInputField(String)}, {@link #setTaskNamesList(String[])}
 * <br><br>
 * Other methods:{@link #makeDialogVisible()}
 */
@Getter
public class TaskNamesDialog {
    //region Fields
    @Getter
    private static final TaskNamesDialog view = new TaskNamesDialog();
    /**
     * Contains a {@link JDialog} instance representing the UI dialog allowing the user to add or remove a task name in
     * the database. It is displayed to the user to manage task names.
     *
     * @see #TaskNamesDialog()
     * @see #initTaskNamesDialog()
     */
    private final JDialog dialog = new JDialog((Frame) null, "Dodaj zadanie do bazy", true);
    /**
     * Contains a {@link JScrollPane} instance containing a {@link #taskNamesList} with existing task names.
     *
     * @see #TaskNamesDialog()
     * @see #createTaskNamesScrollPane()
     */
    private final JScrollPane taskNamesScrollPane;
    /**
     * Contains a {@link JTextField} instance allowing the user to enter the task name which can be added or removed in
     * the database.
     *
     * @see #TaskNamesDialog()
     * @see #createTaskNameInputField()
     * @see #createTaskAddingPanel()
     */
    private final JTextField taskNameInputField;
    /**
     * Contains an array of the {@link JButton}s representing the buttons for adding, removing, and cancelling task
     * names.
     *
     * @see #TaskNamesDialog()
     * @see #createButtonsPanel()
     */
    private final JButton[] buttons;
    /**
     * Contains a {@link JList} instance with existing task names in the database. It is used in the UI to display the
     * list of task names.
     *
     * @see #TaskNamesDialog()
     * @see #createLeftPanel()
     */
    private final JList<String> taskNamesList;
    /**
     * A flag representing whether the dialog is visible or not. It is used to control the visibility of the dialog.
     */
    private boolean visible = false;
    //endregion

    /**
     * Constructs a new {@link TaskNamesDialog} singleton instance. This constructor initializes references to the
     * {@link #taskNamesList}, {@link #taskNamesScrollPane}, {@link #taskNameInputField} and {@link #buttons}. Finally,
     * it calls {@link #initTaskNamesDialog()} to initialize the UI dialog.
     *
     * @see #createTaskNamesScrollPane()
     * @see #createTaskNameInputField()
     * @see #initTaskNamesDialog()
     * @see TaskNamesService
     */
    private TaskNamesDialog() {
        this.taskNamesList = new JList<>(TaskNamesService.getService().getTaskNames().toArray(new String[0]));
        this.taskNamesScrollPane = createTaskNamesScrollPane();
        this.taskNameInputField = createTaskNameInputField();
        this.buttons = new JButton[]{
                createButton("confirmButton", "OK"),
                createButton("deleteButton", "Usuń"),
                createButton("cancelButton", "Wyczyść"),
        };

        initTaskNamesDialog();
    }

    /**
     * Initializes the UI dialog allowing the user to add or remove a task name in the database. It sets the name,
     * title, layout as {@code FlowLayout} centered and modality as true. Then, it adds the left and right panels to the
     * dialog. Finally, it packs the dialog.
     *
     * @see #createLeftPanel()
     * @see #createRightPanel()
     * @see TaskNamesDialog()
     */
    private void initTaskNamesDialog() {
        dialog.setName("taskNamesDialog");
        dialog.setTitle("Dodaj zadanie do bazy");
        dialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        dialog.setModal(true);

        dialog.add(Box.createRigidArea(new Dimension(20, 10)));
        dialog.add(createLeftPanel());
        dialog.add(Box.createRigidArea(new Dimension(20, 10)));
        dialog.add(createRightPanel());
        dialog.add(Box.createRigidArea(new Dimension(20, 20)));

        dialog.pack();
    }

    //region leftPanel methods

    /**
     * Creates a {@link JPanel} using the {@link #createPanel(String, LayoutManager)} method and adds a title label and
     * the {@link #taskNamesScrollPane}.
     *
     * @return a {@link JPanel} representing left panel of the {@link #dialog} wih the task names scroll pane.
     * @see #initTaskNamesDialog()
     * @see #createLeftPanel()
     * @see #createLabel(String, String)
     */
    private JPanel createLeftPanel() {
        var leftPanel = createPanel("leftPanel", null);
        leftPanel.add(createLabel("titleLabel", "Dostępne zadania:"));
        leftPanel.add(taskNamesScrollPane);

        return leftPanel;
    }

    /**
     * @return a {@link JScrollPane} with task names from the {@link #taskNamesList}.
     * @see TaskNamesDialog()
     * @see #createLeftPanel()
     */
    private JScrollPane createTaskNamesScrollPane() {
        var taskNamesScrollPane = new JScrollPane(taskNamesList);
        taskNamesScrollPane.setName("taskNamesScrollPane");

        return taskNamesScrollPane;
    }
    //endregion

    //region rightPanel methods

    /**
     * Creates a {@link JPanel} using the {@link #createPanel(String, LayoutManager)} method and adds the task adding
     * panel with {@link #taskNameInputField} and buttons panel with {@link #buttons}.
     *
     * @return a {@link JPanel} representing right panel of the {@link #dialog} with the task adding panel and buttons
     * @see #createPanel(String, LayoutManager)
     * @see #createTaskAddingPanel()
     * @see #createButtonsPanel()
     * @see #initTaskNamesDialog()
     */
    private JPanel createRightPanel() {
        var rightPanel = createPanel("rightPanel", null);

        rightPanel.add(Box.createVerticalStrut(100));
        rightPanel.add(createTaskAddingPanel());
        rightPanel.add(createButtonsPanel());
        rightPanel.add(Box.createVerticalStrut(100));

        return rightPanel;
    }

    //region taskAddingPanel methods

    /**
     * Creates a {@link JPanel} using the {@link #createPanel(String, LayoutManager)} method and adds the label and the
     * {@link #taskNameInputField}.
     *
     * @return a {@link JPanel} representing task adding panel with the label and the {@link #taskNameInputField}.
     * @see #createRightPanel()
     */
    private JPanel createTaskAddingPanel() {
        var taskAddingPanel = createPanel("taskAddingPanel", new FlowLayout(FlowLayout.LEFT));
        taskAddingPanel.add(createLabel("informLabel", "Zadanie:"));
        taskAddingPanel.add(taskNameInputField);

        return taskAddingPanel;
    }

    /**
     * @return a created  {@link JTextField} where user can enter a task name.
     * @see TaskNamesDialog()
     * @see #createTaskAddingPanel()
     */
    private JTextField createTaskNameInputField() {
        var taskNameInputField = new JTextField(32);
        taskNameInputField.setName("taskNameInputField");

        return taskNameInputField;
    }
    //endregion

    //region buttonsPanel methods

    /**
     * Creates a {@link JPanel} with centered flow layout using the {@link #createPanel(String, LayoutManager)} method
     * and adds the {@link #buttons}.
     *
     * @return a panel with the buttons
     * @see #createRightPanel()
     */
    private JPanel createButtonsPanel() {
        var buttonsPanel = createPanel("buttonsPanel", new FlowLayout(FlowLayout.CENTER));
        Arrays.stream(buttons).forEach(buttonsPanel::add);

        return buttonsPanel;
    }

    /**
     * @param name a name of the button
     * @param text a text of the button
     * @return a new {@link JButton} with the given name and text
     * @see TaskNamesDialog()
     * @see #createButtonsPanel()
     */
    private JButton createButton(String name, String text) {
        var button = new JButton(text);
        button.setName(name);

        return button;

    }
    //endregion
    //endregion

    /**
     * Makes {@link #dialog} visible. It runs the dialog in a separate thread. Before it sets visibility as true, it sleeps for
     * 1s.
     *
     * @see #isVisible()
     * @see MainPanelController#addListenerToButtons()
     */
    public void makeDialogVisible() {
        visible = false;

        new Thread(() -> {
            try {Thread.sleep(1000);} catch (InterruptedException e) {}

            if (dialog.isVisible()) visible = dialog.isVisible();
        }).start();

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    //region helper methods

    /**
     * Creates a new {@link JPanel} with a given name and layout. If the given layout is null, it sets layout as a box
     * layout. It is used to create all panels in this class.
     *
     * @param panelName a given name of the panel
     * @param layout    a given layout of the panel
     * @return a new {@link JPanel} with the given name and layout
     * @see #createLeftPanel()
     * @see #createRightPanel()
     * @see #createButtonsPanel()
     * @see #createTaskAddingPanel()
     */
    private JPanel createPanel(String panelName, LayoutManager layout) {
        var panel = new JPanel();
        panel.setName(panelName);

        if (layout != null) panel.setLayout(layout);
        else panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        return panel;
    }

    /**
     * @param labelName a given name of the label
     * @param labelText a given text of the label
     * @return a new {@link JLabel} with a given name and text
     * @see #createLeftPanel()
     * @see #createTaskAddingPanel()
     */
    private JLabel createLabel(String labelName, String labelText) {
        var label = new JLabel(labelText);
        label.setName(labelName);

        return label;
    }
    //endregion

    /**
     * @return a {@link String } with the task name from the {@link #taskNameInputField} when it is not blank, otherwise
     * null
     */
    public String getTaskNameFromInputField() {
        var text = taskNameInputField.getText();
        return !text.isBlank() ? text : null;
    }

    /**
     * Sets a given task name in the {@link #taskNameInputField}.
     *
     * @param taskName a given task name which should be set
     */
    public void setTaskNameInInputField(String taskName) {
        taskNameInputField.setText(taskName);
    }

    /**
     * Sets a given list of task names in the {@link #taskNamesList}.
     *
     * @param taskNames a given list of task names
     */
    public void setTaskNamesList(String[] taskNames) {
        taskNamesList.setListData(taskNames);
    }

    /**
     * @return a {@link String} representing the selected task name from the {@link #taskNamesList}
     */
    public String getSelectedTaskNameFromTaskNamesList() {
        return taskNamesList.getSelectedValue();
    }
}