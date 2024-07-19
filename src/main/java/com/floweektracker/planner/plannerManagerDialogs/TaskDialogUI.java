package com.floweektracker.planner.plannerManagerDialogs;

import com.floweektracker.planner.WeekDays;
import com.floweektracker.tasksDatabase.*;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static javax.swing.BoxLayout.Y_AXIS;

/**
 * Represents the UI of the {@link #dialog} serves as a base for the {@link TaskAddingDialogUI} and
 * {@link TaskEditingDialogUI} classes, allowing the user to add or edit a task.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #dialog} - a {@link JDialog} represents the dialog shown to the user, allowing them to add or edit a
 *     task</li>
 *     <li>{@link #dialogButtons} - an array of {@link JButton} representing buttons displayed within the
 *     {@link #dialog}</li>
 *     <li>{@link #weekDaysComboBox} - a {@link JComboBox} representing a combo box with weekdays, displayed within the
 *     {@link #dialog}</li>
 *     <li>{@link #tasksComboBox} - a {@link JComboBox} representing a combo box with task names, displayed within the
 *     {@link #dialog}</li>
 *     <li>{@link #timeSpinner} - a {@link JSpinner} representing a spinner allowing the user to choose a time, displayed
 *     within the {@link #dialog}</li>
 *     <li>{@link #prioritySpinner} - a {@link JSpinner} representing a spinner allowing the user to choose a priority,
 *     displayed within the {@link #dialog} </li>
 *     <li>{@link #descriptionTextArea} - a {@link JTextArea} representing a text area allowing the user to enter the
 *     description of the task, displayed within the {@link #dialog}</li>
 *     <li>{@link #planners} - a {@link HashMap} containing tasks sorted by each day of the week</li>
 *     <li>{@link #task} - a reference to a {@link SingleTaskManager} representing a single task</li>
 * </ul>
 * Configuration methods:
 * <ul>
 *     <li>{@link #configureDialog()} - configures the {@link #dialog} adding common components and settings for both
 *     {@link TaskAddingDialogUI} and {@link TaskEditingDialogUI}</li>
 * </ul>
 * Dialog panels creators:
 * <ul>
 *     <li>{@link #createTaskSelectionPanel()} - creates a {@link JPanel} containing components allowing the user to
 *     select a name for the {@link #task}</li>
 *     <li>{@link #createWeekDaySelectionPanel()} - creates a {@link JPanel} containing components allowing the user to
 *     select the weekday for the {@link #task}</li>
 *     <li>{@link #createTimeSelectionPanel()} - creates a {@link JPanel} containing components allowing the user to
 *     select the time for the {@link #task}</li>
 *     <li>{@link #createPrioritySelectionPanel()} - creates a {@link JPanel} containing components allowing the user
 *     to select the priority of the {@link #task}</li>
 *     <li>{@link #createDescriptionPanel()} - creates a {@link JPanel} containing components allowing the user to
 *     enter the description of the {@link #task}</li>
 *     <li>{@link #createButtonsPanel()} - creates a {@link JPanel} containing {@link JButton}s allowing the user to
 *     save task or cancel the action</li>
 *     <li>{@link #createPanel(Component, String)} - creates a {@link JPanel} for the other panel creation methods,
 *     based on the provided parameters</li>
 * </ul>
 *
 * Getters: {@link #getPlanners()}, {@link #getDialogButtons()}, {@link #getWeekDaysComboBox()},
 * {@link #getTimeSpinner()}, {@link #getPrioritySpinner()}, {@link #getDescriptionTextArea()},
 * {@link #getTasksComboBox()}, {@link #getTask()}, {@link #getDialog()}
 */
public class TaskDialogUI {
    //region Fields
    /**
     * Contains a new {@link JDialog} instance representing the dialog shown to the user for adding or editing a task.
     * This dialog provides the UI components necessary for the user to input task details.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #configureDialog()
     * @see #getDialog()
     */
    private final JDialog dialog;
    /**
     * Contains an array of {@link JButton}s representing UI buttons displayed within the {@link #dialog}: a save button
     * and a cancel button.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #createButtonsPanel()
     * @see #getDialogButtons()
     * @see DialogListeners#createButtonsListener()
     */
    private final JButton[] dialogButtons;
    /**
     * Contains a {@link JComboBox} with {@link String} elements, displayed within the {@link #dialog}. Allows the user
     * to select a weekday for the {@link #task}.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #createWeekDaySelectionPanel()
     * @see #getWeekDaysComboBox()
     * @see DialogListeners#createWeekDaysComboBoxListener()
     */
    private final JComboBox<String> weekDaysComboBox,
    /**
     * Contains a {@link JComboBox} with {@link String} elements, displayed within the {@link #dialog}. Allows the user
     * to select a name for the {@link #task}.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #createTaskSelectionPanel()
     * @see #getTasksComboBox()
     * @see DialogListeners#createTasksComboBoxListener()
     */
    tasksComboBox;
    /**
     * Contains a {@link JSpinner}, displayed within the {@link #dialog}. Allows the user to select a time for the
     * {@link #task}.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #createTimeSelectionPanel()
     * @see #getTimeSpinner()
     * @see DialogListeners#createTimeSpinnerListener()
     */
    private JSpinner timeSpinner,
    /**
     * Contains a {@link JSpinner}, displayed within the {@link #dialog}. Allows the user to select a priority for the
     * {@link #task}.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #createPrioritySelectionPanel()
     * @see #getPrioritySpinner()
     * @see DialogListeners#createPrioritySpinnerListener()
     */
    prioritySpinner;
    /**
     * Contains a {@link JTextArea}, displayed within the {@link #dialog}. Allows the user to enter a description for
     * the {@link #task}.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #createDescriptionPanel()
     * @see #getDescriptionTextArea()
     * @see DialogListeners#createDescriptionTextAreaListener()
     */
    private final JTextArea descriptionTextArea;
    /**
     * Contains a {@link HashMap} of tasks sorted by each day of the week. The key is a {@link String} representing the
     * weekday name, and the value is a {@link HashMap} where the key is a {@link String} representing the time and the
     * value is a {@link SingleTaskManager} representing the task.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #getPlanners()
     */
    private final HashMap<String, HashMap<String, SingleTaskManager>> planners;
    /**
     * Contains a {@link SingleTaskManager} representing a task with its details.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     * @see #createTaskSelectionPanel()
     * @see #createWeekDaySelectionPanel()
     * @see #createTimeSelectionPanel()
     * @see #createDescriptionPanel()
     * @see #getTask()
     */
    private final SingleTaskManager task;
    //endregion

    /**
     * Constructs a new {@link TaskDialogUI} with the specified {@code planners} and {@code task}. This constructor
     * initializes the {@link #dialog} for adding or editing {@link #task}, and also initializes {@link #task} and
     * {@link #planners} fields. It initializes UI components: {@link #dialogButtons}, {@link #weekDaysComboBox},
     * {@link #timeSpinner}, {@link #prioritySpinner}, {@link #descriptionTextArea} and {@link #tasksComboBox}. Finally,
     * it invokes {@link #configureDialog()} to configure the {@link #dialog} field.
     *
     * @param planners a {@link HashMap} containing {@link SingleTaskManager}s sorted by each day of the week
     * @param task     a {@link SingleTaskManager} representing a single task with its details
     * @see TaskAddingDialogUI#TaskAddingDialogUI(HashMap)
     * @see TaskEditingDialogUI#TaskEditingDialogUI(HashMap, SingleTaskManager)
     */
    TaskDialogUI(HashMap<String, HashMap<String, SingleTaskManager>> planners, SingleTaskManager task) {
        this.dialog = new JDialog();
        this.task = task;
        this.planners = planners;

        this.dialogButtons = new JButton[]{new JButton("Zapisz"), new JButton("Anuluj")};
        this.weekDaysComboBox = new JComboBox<>(WeekDays.getWeekDaysPL());
        this.timeSpinner = null;
        this.prioritySpinner = new JSpinner(new SpinnerNumberModel(task.getPriority(), 1, 10, 1));
        this.descriptionTextArea = new JTextArea(3, 32);
        this.tasksComboBox = new JComboBox<>(new TasksDatabaseManager().getTasksList());

        configureDialog();
    }

    /**
     * Configures the {@link #dialog} by setting its layout, size, modality, and adding the {@link JPanel}s created in
     * the {@link #createTaskSelectionPanel()}, {@link #createWeekDaySelectionPanel()},
     * {@link #createTimeSelectionPanel()}, and {@link #createPrioritySelectionPanel()} methods. These settings are
     * common for the {@link TaskAddingDialogUI} and {@link TaskEditingDialogUI}.
     *
     * @see #TaskDialogUI(HashMap, SingleTaskManager)
     */
    private void configureDialog() {
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), Y_AXIS));
        dialog.setSize(400, 400);
        dialog.setModal(true);
        dialog.add(createTaskSelectionPanel());
        dialog.add(createWeekDaySelectionPanel());
        dialog.add(createTimeSelectionPanel());
        dialog.add(createPrioritySelectionPanel());
    }

    //region Dialog panels creators

    /**
     * Creates a {@link JPanel} containing a {@link JLabel} and the {@link #tasksComboBox}. This method sets the
     * selected item in the {@link #tasksComboBox} to the current task's name, and then creates and returns a
     * {@link JPanel} using the {@link #createPanel(Component, String)} method.
     *
     * @return a {@link JPanel} with a {@link JLabel} and the {@link #tasksComboBox}
     * @see #configureDialog()
     */
    private JPanel createTaskSelectionPanel() {
        tasksComboBox.setSelectedItem(task.getTaskName());
        return createPanel(tasksComboBox, "Zadanie:");
    }

    /**
     * Creates a {@link JPanel} containing a {@link JLabel} and the {@link #weekDaysComboBox}. This method sets the
     * selected item in the {@link #weekDaysComboBox} to the current task's weekday, and then creates and returns a
     * {@link JPanel} using the {@link #createPanel(Component, String)} method.
     *
     * @return a {@link JPanel} with a {@link JLabel} and the {@link #tasksComboBox}
     * @see #configureDialog()
     */
    private JPanel createWeekDaySelectionPanel() {
        weekDaysComboBox.setSelectedItem(task.getWeekDay().getWeekDayPL());
        return createPanel(weekDaysComboBox, "Dzień tygodnia:");
    }

    /**
     * Creates a {@link JPanel} containing a {@link JLabel} and the {@link #tasksComboBox}. This method configure
     * {@link #timeSpinner} so that user can select the time in the "HH:mm" format. It also sets the selected time in
     * the {@link #timeSpinner} to the current task's time, and then creates and returns a {@link JPanel} using the
     * {@link #createPanel(Component, String)} method.
     *
     * @return a {@link JPanel} with a {@link JLabel} and the {@link #tasksComboBox}
     * @see #configureDialog()
     */
    private JPanel createTimeSelectionPanel() {
        var timeParts = task.getTime().split(":");
        var planner = Calendar.getInstance();
        planner.set(Calendar.HOUR_OF_DAY, Byte.parseByte(timeParts[0]));
        planner.set(Calendar.MINUTE, Byte.parseByte(timeParts[1]));

        var defaultTime = planner.getTime();
        timeSpinner = new JSpinner(new SpinnerDateModel(defaultTime, null, null, Calendar.HOUR_OF_DAY));
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, new SimpleDateFormat("HH:mm").toPattern()));

        return createPanel(timeSpinner, "Godzina:");
    }

    /**
     * Creates a {@link JPanel} containing a {@link JLabel} and the {@link #prioritySpinner} using the
     * {@link #createPanel(Component, String)} method.
     *
     * @return a {@link JPanel} with a {@link JLabel} and the {@link #prioritySpinner}
     * @see #configureDialog()
     */
    private JPanel createPrioritySelectionPanel() {return createPanel(prioritySpinner, "Priorytet:");}

    /**
     * Creates a {@link JPanel} containing a {@link JLabel} and the {@link #descriptionTextArea}. This method checks
     * content of the task's description. If description doesn't equal "unknown", the content of the task's description
     * is appended to the {@link #descriptionTextArea}. Then, it creates a {@link JScrollPane} with the
     * {@link #descriptionTextArea} as a parameter and sets the vertical scroll bar polity. Finally, it creates and
     * returns a {@link JPanel} using the {@link #createPanel(Component, String)} method.
     *
     * @return a {@link JPanel} with the {@link JLabel} and the {@link #descriptionTextArea}
     * @see TaskAddingDialogUI#configureTaskAddingDialog()
     * @see TaskEditingDialogUI#configureTaskEditingDialog()
     */
    JPanel createDescriptionPanel() {
        descriptionTextArea.setLineWrap(true);
        if (!task.getDescription().equals("unknown")) descriptionTextArea.append(task.getDescription());

        var descriptionScrollPane = new JScrollPane(descriptionTextArea);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return createPanel(descriptionScrollPane, "Opis:");
    }

    /**
     * Creates a {@link JPanel} containing the {@link #dialogButtons}.
     *
     * @return a {@link JPanel} with the {@link #dialogButtons}
     * @see TaskAddingDialogUI#configureTaskAddingDialog()
     * @see TaskEditingDialogUI#configureTaskEditingDialog()
     */
    JPanel createButtonsPanel() {
        var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        Arrays.stream(dialogButtons).forEach(buttonsPanel::add);

        return buttonsPanel;
    }

    /**
     * Creates and returns a {@link JPanel} containing a {@link JLabel} and a {@link Component}. The {@link JLabel}
     * contains a {@code textLabel}, and is displayed next to the corresponding {@link Component}. The label provides
     * context for the {@link Component}, helping the user understand the expected input.
     *
     * @param component a {@link Component} allowing the user to input data, and the program to download data.
     * @param textLabel a {@link String} representing the component name, used to create a {@link JLabel}.
     * @return a {@link JPanel} with a {@link JLabel} and a {@link Component}
     * @see #createTaskSelectionPanel()
     * @see #createWeekDaySelectionPanel()
     * @see #createTimeSelectionPanel()
     * @see #createPrioritySelectionPanel()
     * @see #createDescriptionPanel()
     * @see TaskEditingDialogUI#createPanel(Component, String)
     */
    JPanel createPanel(final Component component, final String textLabel) {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(textLabel));
        panel.add(component);

        return panel;
    }
    //endregion

    //region Getters

    /**
     * @return a {@link #planners} representing tasks sorted by each day of the week
     * @see DialogListeners#createButtonsListener()
     */
    HashMap<String, HashMap<String, SingleTaskManager>> getPlanners() {return planners;}

    /**
     * @return a {@link #dialogButtons} containing an array of {@link JButton} representing saving and cancelling
     * buttons
     * @see DialogListeners#setupListeners()
     */
    JButton[] getDialogButtons() {return dialogButtons;}

    /**
     * @return a {@link #weekDaysComboBox} containing {@link String}s representing weekdays, allowing the user to select
     * the task's day of the week
     * @see DialogListeners#setupListeners()
     * @see DialogListeners#createWeekDaysComboBoxListener()
     */
    JComboBox<String> getWeekDaysComboBox() {return weekDaysComboBox;}

    /**
     * @return a {@link #timeSpinner} allowing the user to select the task's time
     * @see DialogListeners#setupListeners()
     * @see DialogListeners#createTimeSpinnerListener()
     */
    JSpinner getTimeSpinner() {return timeSpinner;}

    /**
     * @return a {@link #prioritySpinner} allowing the user to select the task's priority
     * @see DialogListeners#setupListeners()
     * @see DialogListeners#createPrioritySpinnerListener()
     */
    JSpinner getPrioritySpinner() {return prioritySpinner;}

    /**
     * @return a {@link #descriptionTextArea} allowing the user to enter the task's description
     * @see DialogListeners#setupListeners()
     * @see DialogListeners#createDescriptionTextAreaListener()
     */
    JTextArea getDescriptionTextArea() {return descriptionTextArea;}

    /**
     * @return a {@link #tasksComboBox} containing {@link String}s representing tasks that user can select
     * @see DialogListeners#setupListeners()
     * @see DialogListeners#createTasksComboBoxListener()
     */
    JComboBox<String> getTasksComboBox() {return tasksComboBox;}

    /**
     * @return a {@link #task} representing the task added or edited by the user
     * @see DialogListeners#DialogListeners
     * @see TaskEditingDialogUI#createIsDoneComboBox()
     */
    SingleTaskManager getTask() {return task;}

    /**
     * @return a {@link #dialog} serving as the base for {@link TaskAddingDialogUI} or {@link TaskEditingDialogUI}
     * @see DialogListeners#createButtonsListener()
     * @see TaskAddingDialogUI#configureTaskAddingDialog()
     * @see TaskEditingDialogUI#TaskEditingDialogUI(HashMap, SingleTaskManager)
     */
    JDialog getDialog() {return dialog;}
    //endregion
}
