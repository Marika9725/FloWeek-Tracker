package com.floweektracker.view;

import com.floweektracker.controller.*;
import com.floweektracker.model.*;
import com.floweektracker.util.DialogUtils;
import lombok.*;

import javax.swing.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

/**
 * UI of the task editing dialog that allows the user to edit a task in the planner. It enables to edit task name,
 * weekday, time, description, done status and priority. At the bottom of the dialog, there are two buttons, which are
 * used to confirm editing or cancel it.
 * <br><br>
 * Fields: {@link #originalTask}, {@link #view}, {@link #dialog}, {@link #tasksComboBox}, {@link #weekdaysComboBox},
 * {@link #timeSpinner}, {@link #descriptionTextArea}, {@link #prioritySpinner}, {@link #statusComboBox},
 * {@link #buttons}, {@link #isDialogOpen}
 * <br><br>
 * Visibility methods: {@link #makeDialogVisible(SingleTask)}, {@link #setValuesFromOriginalTask(SingleTask)},
 * {@link #makeDialogInvisible()}
 * <br><br>
 * Getter methods: {@link #getTaskName()}, {@link #getDescription()}, {@link #getWeekday()}, {@link #getTime()},
 * {@link #getPriority()}, {@link #getStatus()}, {@link #getAllTaskNames()}
 */
@Getter
public class TaskEditingDialog {
    //region fields
    /**
     * Original task that is being edited.
     */
    private SingleTask originalTask;
    @Getter(AccessLevel.PRIVATE)
    private final TaskDialogView view = new TaskDialogView.TaskDialogBuilder("taskEditingDialog")
            .withTasksPanel().withDescriptionPanel().withWeekdaysPanel().withTimePanel().withPriorityPanel()
            .withStatusPanel().withButtons().build();
    private final JDialog dialog = view.getDialog();
    /**
     * Combo box with task names. It is displayed to the user in the {@link #dialog} and allows to edit a task name.
     *
     * @see #setValuesFromOriginalTask(SingleTask)
     * @see #getTaskName()
     * @see #getAllTaskNames()
     * @see com.floweektracker.controller.TaskEditingDialogController#updateTaskNames(String)
     */
    private final JComboBox<String> tasksComboBox = view.getTasksComboBox();
    /**
     * Text area which is displayed to the user in the {@link #dialog} and allows to edit a task's description.
     *
     * @see #setValuesFromOriginalTask(SingleTask)
     * @see #getDescription()
     */
    private final JTextArea descriptionTextArea = (JTextArea) view.getDescriptionScrollPane().getViewport().getView();
    /**
     * Combo box with weekdays. It is displayed to the user in the {@link #dialog} and allows to edit a weekday.
     *
     * @see #setValuesFromOriginalTask(SingleTask)
     * @see #getWeekday()
     */
    private final JComboBox<String> weekdaysComboBox = view.getWeekdaysComboBox();
    /**
     * Spinner which is displayed to the user in the {@link #dialog} and allows to edit a task's time.
     *
     * @see #setValuesFromOriginalTask(SingleTask)
     * @see #getTime()
     */
    private final JSpinner timeSpinner = view.getTimeSpinner();
    /**
     * Spinner which is displayed to the user in the {@link #dialog} and allows to edit a task's priority
     *
     * @see #setValuesFromOriginalTask(SingleTask)
     * @see #getPriority() .
     */
    private final JSpinner prioritySpinner = view.getPrioritySpinner();
    /**
     * Combo box which is displayed to the user in the {@link #dialog} and allows to edit a task's status.
     *
     * @see #setValuesFromOriginalTask(SingleTask)
     * @see #getStatus()
     */
    private final JComboBox<String> statusComboBox = view.getStatusComboBox();
    /**
     * Buttons which are displayed to the user in the {@link #dialog} and allows to confirm or cancel a description
     * editing.
     *
     * @see com.floweektracker.controller.TaskEditingDialogController#initializeListeners()
     */
    private final List<JButton> buttons = view.getButtons();
    /**
     * Contains information whether the dialog is currently open or not.
     *
     * @see #makeDialogVisible(SingleTask)
     */
    private boolean isDialogOpen = false;
    //endregion

    /**
     * Public constructor which sets title to the {@link #dialog} and packs it.
     *
     * @see com.floweektracker.controller.PlannerController#PlannerController()
     */
    public TaskEditingDialog() {
        dialog.setTitle("Edytuj zadanie");
        dialog.pack();
    }

    //region makeDialogVisible() methods

    /**
     * Makes dialog visible. Before it sets visibility as true, it marks {@link #isDialogOpen} as false. Then, it checks
     * if the given task is null. If it is, it shows message to the user. Otherwise, it sets values from original task
     * to the dialog's components, sets location of the dialog to the center of the screen and finally makes dialog
     * visible.
     *
     * @see DialogUtils#showMessageDialog(String, String)
     * @see #setValuesFromOriginalTask(SingleTask)
     * @see PlannerController#editTask()
     */
    public boolean makeDialogVisible(SingleTask originalTask) {
        isDialogOpen = false;

        if (originalTask == null) {
            DialogUtils.showMessageDialog(
                    "Błąd zadania",
                    "Wskazane pole jest puste. Kliknij podwójnie w pole z istniejącym zadaniem, by je edytować."
            );
        } else {
            this.originalTask = originalTask;
            setValuesFromOriginalTask(originalTask);
            dialog.setLocationRelativeTo(null);

            SwingUtilities.invokeLater(() -> dialog.setVisible(true));
        }

        return isDialogOpen;
    }

    /**
     * Sets values from the given task to the dialog's components. It sets task name, description, weekday, time,
     * priority and status to the corresponding components.
     *
     * @param originalTask a given task which values will be set
     * @see #makeDialogVisible(SingleTask)
     */
    private void setValuesFromOriginalTask(SingleTask originalTask) {
        tasksComboBox.setSelectedItem(originalTask.getTaskName());
        descriptionTextArea.setText(originalTask.getDescription());
        weekdaysComboBox.setSelectedItem(originalTask.getWeekday().getWeekdayPL());

        var calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, originalTask.getTime().getHour());
        calendar.set(Calendar.MINUTE, originalTask.getTime().getMinute());
        timeSpinner.setValue(calendar.getTime());

        prioritySpinner.setValue(originalTask.getPriority());
        statusComboBox.setSelectedItem(originalTask.isDone() ? "Tak" : "Nie");
    }
    //endregion

    /**
     * Makes dialog invisible.
     *
     * @see TaskEditingDialogController#createEditCancelButtonListener() ()
     * @see TaskEditingDialogController#editTask(SingleTask, SingleTask)
     */
    public void makeDialogInvisible() {
        dialog.setVisible(false);
    }

    /**
     * @return a {@link String} with edited task name or null
     */
    public String getTaskName() {
        return (String) tasksComboBox.getSelectedItem();
    }

    /**
     * @return a {@link String} with description from the {@link #descriptionTextArea} or null
     */
    public String getDescription() {
        return descriptionTextArea.getText();
    }

    /**
     * @return an edited {@link WeekDays} object getting from the {@link #weekdaysComboBox} or null
     */
    public WeekDays getWeekday() {
        var weekdayPL = (String) weekdaysComboBox.getSelectedItem();

        return WeekDays.getListedWeekdays().stream()
                .filter(weekday -> weekday.getWeekdayPL().equals(weekdayPL))
                .findFirst().orElse(null);
    }

    /**
     * Gets edited time from the {@link #timeSpinner}, and converts it to the {@link LocalTime}.
     *
     * @return an edited {@link LocalTime} based on a value from the {@link #timeSpinner}
     */
    public LocalTime getTime() {
        var date = (Date) timeSpinner.getValue();
        var localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES);
    }

    /**
     * Gets edited priority from the {@link #prioritySpinner}, and converts it to the {@code byte}.
     *
     * @return a {@code byte} based on an edited value from the {@link #prioritySpinner}
     */
    public Byte getPriority() {
        return Byte.parseByte(prioritySpinner.getValue().toString());
    }

    /**
     * @return true when status is set to "Tak", otherwise false
     */
    public boolean getStatus() {
        return (Objects.requireNonNull(statusComboBox.getSelectedItem())).equals("Tak");
    }

    /**
     * @return an {@link ArrayList} with {@link String}s representing all task names from the {@link #tasksComboBox}
     */
    public ArrayList<String> getAllTaskNames() {
        return IntStream.range(0, tasksComboBox.getItemCount())
                .mapToObj(tasksComboBox::getItemAt)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
