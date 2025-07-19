package com.floweektracker.view;

import com.floweektracker.controller.*;
import com.floweektracker.model.*;
import lombok.*;

import javax.swing.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

/**
 * UI of the task adding dialog that allows the user to add a task to the planner. It enables to choose task name,
 * weekday, time and priority. It also permits to write description of the task. At the bottom of the dialog, there are
 * two buttons, which are used to add or cancel a task.
 * <br><br>
 * Fields: {@link #view}, {@link #dialog}, {@link #tasksComboBox}, {@link #weekdaysComboBox}, {@link #timeSpinner},
 * {@link #descriptionScrollPane}, {@link #buttons}
 * <br><br>
 * Visibility methods: {@link #makeDialogVisible()}, {@link #setDefaultValues()}, {@link #makeDialogInvisible()}
 * <br><br>
 * Getter methods: {@link #getTaskName()}, {@link #getDescription()}, {@link #getWeekday()}, {@link #getTime()},
 * {@link #getPriority()}, {@link #getAllTaskNames()}
 */
@Getter
public class TaskAddingDialog extends JDialog {
    @Getter(AccessLevel.PRIVATE)
    private final TaskDialogView view = new TaskDialogView.TaskDialogBuilder("taskAddingDialog")
            .withTasksPanel().withDescriptionPanel().withWeekdaysPanel().withTimePanel().withPriorityPanel()
            .withButtons().build();

    private final JDialog dialog = view.getDialog();
    /**
     * Combo box with task names. It is displayed to the user in the {@link #dialog} and allows to choose a task name.
     *
     * @see #setDefaultValues()
     * @see #getTaskName()
     * @see #getAllTaskNames()
     * @see com.floweektracker.controller.TaskAddingDialogController#updateTaskNames(String)
     */
    private final JComboBox<String> tasksComboBox = view.getTasksComboBox();
    /**
     * Combo box with weekdays. It is displayed to the user in the {@link #dialog} and allows to choose a weekday.
     *
     * @see #setDefaultValues()
     * @see #getWeekday()
     */
    private final JComboBox<String> weekdaysComboBox = view.getWeekdaysComboBox();
    /**
     * Spinner which is displayed to the user in the {@link #dialog} and allows to choose a task's time.
     *
     * @see #setDefaultValues()
     * @see #getTime()
     */
    private final JSpinner timeSpinner = view.getTimeSpinner();
    /**
     * Spinner which is displayed to the user in the {@link #dialog} and allows to choose a task's priority
     *
     * @see #setDefaultValues()
     * @see #getPriority() .
     */
    private final JSpinner prioritySpinner = view.getPrioritySpinner();
    /**
     * Scroll pane which is displayed to the user in the {@link #dialog} and allows to write a task's description.
     *
     * @see #setDefaultValues()
     * @see #getDescription()
     */
    private final JScrollPane descriptionScrollPane = view.getDescriptionScrollPane();
    /**
     * Buttons which are displayed to the user in the {@link #dialog} and allows to confirm or cancel a task.
     *
     * @see TaskAddingDialogController#initializeListeners()
     */
    private final List<JButton> buttons = view.getButtons();

    /**
     * Public constructor which sets title to the {@link #dialog} and packs it.
     *
     * @see com.floweektracker.MainFrame
     */
    public TaskAddingDialog() {
        dialog.setTitle("Dodaj nowe zadanie");
        dialog.pack();
    }

    //region makeDialogVisible() methods

    /**
     * Makes dialog visible. Before it sets visibility as true, it sets default values in the components of the
     * {@link #dialog} and sets location of the dialog to the center of the screen.
     *
     * @see #setDefaultValues()
     * @see MainPanelController#addListenerToButtons()
     */
    public void makeDialogVisible() {
        setDefaultValues();
        dialog.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> {
            try {dialog.setVisible(true);} catch (Exception _) {}
        });
    }

    /**
     * Sets default values for the components in the {@link #dialog}. Initializes task name as null,  weekday as monday,
     * time as 00:00, priority as 5 (medium) and description as empty.
     *
     * @see #makeDialogVisible()
     */
    private void setDefaultValues() {
        var calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        tasksComboBox.setSelectedItem(null);
        weekdaysComboBox.setSelectedItem("Poniedziałek");
        timeSpinner.setValue(calendar.getTime());
        prioritySpinner.setValue(5);
        ((JTextArea) descriptionScrollPane.getViewport().getView()).setText("");
    }
    //endregion

    /**
     * Makes dialog invisible.
     *
     * @see TaskAddingDialogController#initializeListeners()
     * @see TaskAddingDialogController#addTask(SingleTask)
     */
    public void makeDialogInvisible() {
        dialog.setVisible(false);
    }

    /**
     * Gets selected task name from the {@link #tasksComboBox}, and checks, if it isn't null.
     *
     * @return a {@link String} with task name or null
     */
    public String getTaskName() {
        var taskName = tasksComboBox.getSelectedItem();

        return (taskName != null) ? taskName.toString() : null;
    }

    /**
     * Gets description from the {@link #descriptionScrollPane}, and then checks if it isn't null or blank.
     *
     * @return a {@link String} with description or null
     */
    public String getDescription() {
        var descriptionTextArea = (JTextArea) descriptionScrollPane.getViewport().getView();
        var description = descriptionTextArea.getText();

        return ((description != null) && !description.isBlank()) ? description : null;
    }

    /**
     * @return a selected {@link WeekDays} object getting from the {@link #weekdaysComboBox}
     */
    public WeekDays getWeekday() {
        var weekdayString = (String) weekdaysComboBox.getSelectedItem();

        return WeekDays.getListedWeekdays().stream()
                .filter(weekday -> weekday.getWeekdayPL().equals(weekdayString))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets selected time from the {@link #timeSpinner}, and converts it to the {@link LocalTime}.
     *
     * @return a chosen {@link LocalTime} based on a value from the {@link #timeSpinner}
     */
    public LocalTime getTime() {
        var date = (Date) timeSpinner.getValue();
        var localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        return localDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES);
    }

    /**
     * Gets selected priority from the {@link #prioritySpinner}, and converts it to the {@code byte}.
     *
     * @return a chosen {@code byte} based on a chosen value from the {@link #prioritySpinner}
     */
    public byte getPriority() {
        return Byte.parseByte(prioritySpinner.getValue().toString());
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