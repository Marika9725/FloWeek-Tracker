package com.floweektracker.planner.plannerManagerDialogs;

import com.floweektracker.planner.WeekDays;
import com.floweektracker.tasksDatabase.SingleTaskManager;

import javax.swing.event.*;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Represents the UI handling for instances of {@link TaskAddingDialogUI} and {@link TaskEditingDialogUI}, managing user
 * interactions and events within the {@link javax.swing.JDialog}.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #dialog} - a {@link javax.swing.JDialog} reference containing an instance of type {@link T}</li>
 *     <li>{@link #task} - a {@link SingleTaskManager} reference containing a task instance of type {@link T}</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #setupListeners()} - adds listeners to all components of {@link #dialog}</li>
 * </ul>
 * Listeners creators:
 * <ul>
 *     <li>{@link #createButtonsListener()} - creates a listener for buttons in the {@link T} instance</li>
 *     <li>{@link #createWeekDaysComboBoxListener()} - creates a listener for the weekdays'
 *     {@link javax.swing.JComboBox} in the {@link T} instance</li>
 *     <li>{@link #createTimeSpinnerListener()} - creates a listener for the time's {@link javax.swing.JSpinner} in the
 *     {@link T} instance</li>
 *     <li>{@link #createDescriptionTextAreaListener()} - creates a listener for the description's
 *     {@link javax.swing.JTextArea} in the {@link T} instance</li>
 *     <li>{@link #createTasksComboBoxListener()} - creates a listener for the tasks' {@link javax.swing.JComboBox} in
 *     the {@link T} instance</li>
 *     <li>{@link #createIsDoneComboBoxListener()} - creates a listener for the isDone's {@link javax.swing.JComboBox}
 *     in the {@link T} instance</li>
 *     <li>{@link #createPrioritySpinnerListener()} - creates a listener for the priority's {@link javax.swing.JSpinner}
 *     in the {@link T} instance</li>
 * </ul>
 *
 * @param <T> the type of dialog handled by {@link DialogListeners}, which can be {@link TaskAddingDialogUI} or its
 *            subclass
 */
public class DialogListeners<T extends TaskDialogUI> {
    //region Fields
    /**
     * Contains a {@link T} instance of the dialog, which can be {@link TaskAddingDialogUI} or its subclass.
     *
     * @see #DialogListeners(T)
     * @see #setupListeners()
     * @see #createButtonsListener()
     * @see #createWeekDaysComboBoxListener()
     * @see #createTimeSpinnerListener()
     * @see #createPrioritySpinnerListener()
     * @see #createDescriptionTextAreaListener()
     * @see #createTasksComboBoxListener()
     * @see #createIsDoneComboBoxListener()
     */
    private final T dialog;
    /**
     * Contains a {@link SingleTaskManager} instance representing a specified task from the {@link T} instance.
     *
     * @see #DialogListeners(T)
     * @see #createButtonsListener()
     * @see #createWeekDaysComboBoxListener()
     * @see #createTimeSpinnerListener()
     * @see #createPrioritySpinnerListener()
     * @see #createDescriptionTextAreaListener()
     * @see #createTasksComboBoxListener()
     * @see #createIsDoneComboBoxListener()
     */
    private final SingleTaskManager task;
    //endregion

    /**
     * Constructs a new {@link DialogListeners} instance with the specified {@code dialog}. This constructor initializes
     * references to the {@link #task} and {@link #dialog} fields, and then calls the {@link #setupListeners()} method
     * to add listeners to the {@link #dialog}'s components.
     *
     * @param dialog a {@link javax.swing.JDialog} instance representing {@link TaskAddingDialogUI} or its subclass
     * @see TaskAddingDialogUI#configureTaskAddingDialog()
     * @see TaskEditingDialogUI#configureTaskEditingDialog()
     */
    public DialogListeners(T dialog) {
        this.task = dialog.getTask();
        this.dialog = dialog;

        setupListeners();
    }

    /**
     * Adds listeners to the components within the {@link #dialog} instance. Listeners are created using the methods
     * from this class: {@link #createButtonsListener()}, {@link #createTasksComboBoxListener()},
     * {@link #createWeekDaysComboBoxListener()}, {@link #createTimeSpinnerListener()},
     * {@link #createPrioritySpinnerListener()}, {@link #createDescriptionTextAreaListener()}, and
     * {@link #createIsDoneComboBoxListener()}.
     *
     * @see #DialogListeners(T)
     */
    private void setupListeners() {
        Arrays.stream(dialog.getDialogButtons()).forEach(button -> button.addActionListener(createButtonsListener()));
        dialog.getTasksComboBox().addActionListener(createTasksComboBoxListener());
        dialog.getWeekDaysComboBox().addActionListener(createWeekDaysComboBoxListener());
        dialog.getTimeSpinner().addChangeListener(createTimeSpinnerListener());
        dialog.getPrioritySpinner().addChangeListener(createPrioritySpinnerListener());
        dialog.getDescriptionTextArea().getDocument().addDocumentListener(createDescriptionTextAreaListener());

        if (dialog instanceof TaskEditingDialogUI) {
            ((TaskEditingDialogUI) dialog).getIsDoneComboBox().addActionListener(createIsDoneComboBoxListener());
        }
    }

    //region Listeners creators

    /**
     * Creates an {@link ActionListener} for the saving button within the {@link #dialog} field. Depending on the type
     * of {@link #dialog}, this method performs two kinds of saving action:
     * <br><br>
     * <ul>
     *     <li>If {@link #dialog} is of type {@link TaskEditingDialogUI}, it removes the old version of the task from
     *     {@link TaskEditingDialogUI#planners} and adds the edited one. Then, it invokes
     *     {@link TaskEditingDialogUI#setTaskEdited(boolean)} method with a value of true, and disposes the dialog.</li>
     *     <li>If {@link #dialog} is of type {@link TaskAddingDialogUI}, it adds the new task to
     *     {@link TaskAddingDialogUI#planners}, invokes the {@link TaskAddingDialogUI#setTaskAdded(boolean)} with a
     *     value of true, and disposes the dialog.</li>
     * </ul>
     *
     * @return an {@link ActionListener} for the saving button in the {@link #dialog} field. May produce null if
     * {@link #dialog} is neither {@link TaskEditingDialogUI} nor {@link TaskAddingDialogUI}.
     * @see #setupListeners()
     */
    private ActionListener createButtonsListener() {
        if (dialog instanceof TaskEditingDialogUI) {
            return buttonEvent -> {
                if (buttonEvent.getActionCommand().equals("Zapisz")) {
                    dialog.getPlanners().get(((TaskEditingDialogUI) dialog).getOldWeekDayEN()).remove(((TaskEditingDialogUI) dialog).getOldTime());
                    dialog.getPlanners().get(task.getWeekDay().name()).put(task.getTime(), task);
                    ((TaskEditingDialogUI) dialog).setTaskEdited(true);
                } dialog.getDialog().dispose();
            };
        } else if (dialog instanceof TaskAddingDialogUI) {
            return buttonEvent -> {
                if (buttonEvent.getActionCommand().equals("Zapisz")) {
                    dialog.getPlanners().get(task.getWeekDay().name()).put(task.getTime(), task);
                    ((TaskAddingDialogUI) dialog).setTaskAdded(true);
                } dialog.getDialog().dispose();
            };
        }

        return null;
    }

    /**
     * Creates an {@link ActionListener} for {@link T#weekDaysComboBox} that listens for user selection changes. When a
     * user selects an item in the combo box, this listener retrieves the selected {@link WeekDays} instance based on
     * the selected index and assigns it to the {@link SingleTaskManager#weekDay} property of {@link #task}.
     *
     * @return an {@link ActionListener} for the {@link T#weekDaysComboBox}
     * @see #setupListeners()
     */
    private ActionListener createWeekDaysComboBoxListener() {
        return _ -> {
            var selectedWeekDay = WeekDays.values()[dialog.getWeekDaysComboBox().getSelectedIndex()];
            if (selectedWeekDay != null) task.setWeekDay(selectedWeekDay);
        };
    }

    /**
     * Creates a {@link ChangeListener} for {@link T#timeSpinner} that listens for changes in the selected time value.
     * When a user selects a value in the spinner, this listener retrieves the selected value, converts it to a
     * {@link String}, and assigns it to the {@link SingleTaskManager#time} property of {@link #task}.
     *
     * @return a {@link ChangeListener} for the {@link T#timeSpinner}
     * @see #setupListeners()
     */
    private ChangeListener createTimeSpinnerListener() {
        return _ -> task.setTime(dialog.getTimeSpinner().getValue().toString().substring(11, 16));
    }

    /**
     * Creates a {@link ChangeListener} for {@link T#prioritySpinner} that listens for changes in the selected priority
     * value. When a user selects a value in the spinner, this listener retrieves the selected value, converts it to a
     * {@link Byte}, and assigns it to the {@link SingleTaskManager#priority} property of {@link #task}.
     *
     * @return a {@link ChangeListener} for the {@link T#prioritySpinner}
     * @see #setupListeners()
     */
    private ChangeListener createPrioritySpinnerListener() {
        return _ -> task.setPriority(Byte.parseByte(dialog.getPrioritySpinner().getValue().toString()));
    }

    /**
     * Creates a {@link DocumentListener} for {@link T#descriptionTextArea} that listens for changes in the text area.
     * When a user inserts or removes text in the text area, this listener retrieves the updated text content and
     * assigns it to the {@link SingleTaskManager#description} property of {@link #task}.
     *
     * @return a {@link DocumentListener} for the {@link T#descriptionTextArea}
     * @see #setupListeners()
     */
    private DocumentListener createDescriptionTextAreaListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {task.setDescription(dialog.getDescriptionTextArea().getText());}

            @Override
            public void removeUpdate(DocumentEvent e) {task.setDescription(dialog.getDescriptionTextArea().getText());}

            @Override
            public void changedUpdate(DocumentEvent e) {task.setDescription(dialog.getDescriptionTextArea().getText());}
        };
    }

    /**
     * Creates an {@link ActionListener} for {@link T#tasksComboBox} that listens for user selection changes. When a
     * user selects an item in the combo box, this listener retrieves the selected task name, converts it to the
     * {@link String}, and assigns it to the {@link SingleTaskManager#taskName} property of {@link #task}.
     *
     * @return an {@link ActionListener} for the {@link T#tasksComboBox}
     * @see #setupListeners()
     */
    private ActionListener createTasksComboBoxListener() {
        return _ -> task.setTaskName((String) dialog.getTasksComboBox().getSelectedItem());
    }

    /**
     * Creates an {@link ActionListener} for {@link TaskEditingDialogUI#isDoneComboBox} that listens for user selection
     * changes. When a user selects an item in the combo box, this listener retrieves the selected item and assigns its
     * boolean equivalent to the {@link SingleTaskManager#isDone} property of {@link #task}.
     *
     * @return an {@link ActionListener} for the {@link TaskEditingDialogUI#isDoneComboBox}
     * @see #setupListeners()
     */
    private ActionListener createIsDoneComboBoxListener() {
        return _ -> task.setDone(Objects.requireNonNull(((TaskEditingDialogUI) dialog).getIsDoneComboBox().getSelectedItem()).equals("Tak"));
    }
    //endregions
}