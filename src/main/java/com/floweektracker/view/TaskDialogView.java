package com.floweektracker.view;

import com.floweektracker.MainFrame;
import com.floweektracker.model.WeekDays;
import com.floweektracker.service.TaskNamesService;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

import static javax.swing.BoxLayout.Y_AXIS;

/**
 * Builder for the UI dialogs related to task management. Used to build dialogs such as {@link TaskAddingDialog} and
 * {@link TaskEditingDialog}, or another dialog that uses the same components.
 * <br><br>
 * Fields: {@link #dialog}, {@link #tasksComboBox}, {@link #weekdaysComboBox}, {@link #statusComboBox},
 * {@link #timeSpinner}, {@link #prioritySpinner}, {@link #descriptionScrollPane}, {@link #buttons}
 */
@Getter
public class TaskDialogView {
    private final JDialog dialog;
    private final JComboBox<String> tasksComboBox;
    private final JComboBox<String> weekdaysComboBox;
    private final JComboBox<String> statusComboBox;
    private final JSpinner timeSpinner;
    private final JSpinner prioritySpinner;
    private final JScrollPane descriptionScrollPane;
    private final List<JButton> buttons;

    /**
     * Private constructor initializing TaskDialogView with the specified TaskDialogBuilder.
     *
     * @param builder the TaskDialogBuilder to initialize from
     */
    private TaskDialogView(TaskDialogBuilder builder) {
        this.dialog = builder.dialog;
        this.tasksComboBox = builder.tasksComboBox;
        this.weekdaysComboBox = builder.weekdaysComboBox;
        this.statusComboBox = builder.statusComboBox;
        this.timeSpinner = builder.timeSpinner;
        this.prioritySpinner = builder.prioritySpinner;
        this.descriptionScrollPane = builder.descriptionScrollPane;
        this.buttons = builder.buttons;
    }

    /**
     * Inner static class for building task dialog views with various components.
     */
    public static class TaskDialogBuilder {
        private JDialog dialog;
        private JComboBox<String> tasksComboBox;
        private JComboBox<String> weekdaysComboBox;
        private JComboBox<String> statusComboBox;
        private JSpinner timeSpinner;
        private JSpinner prioritySpinner;
        private JScrollPane descriptionScrollPane;
        private List<JButton> buttons;

        /**
         * Default constructor initializing the dialog with default settings. It creates new instance of the
         * {@link JDialog} with the box layout, size and default name.
         */
        public TaskDialogBuilder() {
            this.dialog = new JDialog(MainFrame.getMAIN_FRAME(), null, Dialog.ModalityType.APPLICATION_MODAL);
            this.dialog.setLayout(new BoxLayout(dialog.getContentPane(), Y_AXIS));
            this.dialog.setSize(400, 400);
            this.dialog.setName("taskDialog");
        }

        /**
         * Constructor initializing the dialog with a given name.
         *
         * @param dialogName the name to set for the dialog
         * @see TaskAddingDialog
         * @see TaskEditingDialog
         */
        public TaskDialogBuilder(String dialogName) {
            this();
            this.dialog.setName(dialogName);
        }

        //region TasksPanel

        /**
         * Adds a {@link JPanel} with {@link #tasksComboBox} to the {@link #dialog}. It contains a label and a combo box
         * which allows the user to choose a task name.
         *
         * @return the current TaskDialogBuilder instance with the tasks panel added
         * @see #createTasksComboBox()
         * @see #createPanel(String, String, Component)
         */
        public TaskDialogBuilder withTasksPanel() {
            dialog.add(createPanel("tasksPanel", "Zadanie: ", createTasksComboBox()));
            return this;
        }

        /**
         * @return a combo box with task names
         * @see #withTasksPanel()
         */
        private JComboBox<String> createTasksComboBox() {
            var taskNames = TaskNamesService.getService().getTaskNames().toArray(new String[0]);
            tasksComboBox = new JComboBox<>(taskNames);
            tasksComboBox.setName("tasksComboBox");
            return tasksComboBox;
        }
        //endregion

        //region WeekdaysPanel

        /**
         * Adds a {@link JPanel} with {@link #weekdaysComboBox} to the {@link #dialog}. It contains a label and a combo
         * box which allows the user to choose a weekday.
         *
         * @return the current TaskDialogBuilder instance with the weekdays panel added
         * @see #createWeekdaysComboBox()
         * @see #createPanel(String, String, Component) 
         */
        public TaskDialogBuilder withWeekdaysPanel() {
            dialog.add(createPanel("weekdaysPanel", "Dzień tygodnia: ", createWeekdaysComboBox()));
            return this;
        }

        /**
         * @return a combo box with weekdays
         * @see #withWeekdaysPanel()
         */
        private JComboBox<String> createWeekdaysComboBox() {
            weekdaysComboBox = new JComboBox<>(WeekDays.getWeekdaysPL());
            weekdaysComboBox.setName("weekdaysComboBox");
            return weekdaysComboBox;
        }
        //endregion

        //region TimePanel
        /**
         * Adds a {@link JPanel} with {@link #timeSpinner} to the {@link #dialog}. It contains a label and a spinner
         * which allows the user to choose an hour and minutes.
         *
         * @return the current TaskDialogBuilder instance with the time panel added
         * @see #createTimeSpinner()
         * @see #createPanel(String, String, Component) 
         */
        public TaskDialogBuilder withTimePanel() {
            dialog.add(createPanel("timePanel", "Godzina: ", createTimeSpinner()));
            return this;
        }

        /**
         * @return a spinner allows to choose a time
         * @see #withTimePanel()
         */
        private JSpinner createTimeSpinner() {
            var calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            var defaultTime = calendar.getTime();
            timeSpinner = new JSpinner(new SpinnerDateModel(defaultTime, null, null, Calendar.HOUR_OF_DAY));
            timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
            timeSpinner.setName("timeSpinner");
            return timeSpinner;
        }
        //endregion

        //region PriorityPanel

        /**
         * Adds a {@link JPanel} with {@link #prioritySpinner} to the {@link #dialog}. It contains a label and a spinner
         * which allows the user to choose priority.
         *
         * @return the current TaskDialogBuilder instance with the priority panel added
         * @see #createPrioritySpinner()
         * @see #createPanel(String, String, Component) 
         */
        public TaskDialogBuilder withPriorityPanel() {
            dialog.add(createPanel("priorityPanel", "Priorytet: ", createPrioritySpinner()));
            return this;
        }

        /**
         * @return a spinner allows to choose priority
         * @see #withPriorityPanel()
         */
        private JSpinner createPrioritySpinner() {
            prioritySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
            prioritySpinner.setName("prioritySpinner");
            return prioritySpinner;
        }
        //endregion

        //region DescriptionPanel

        /**
         * Adds a {@link JPanel} with {@link #descriptionScrollPane} to the {@link #dialog}. It contains a label and a
         * scroll pane which allows the user to write a description.
         *
         * @return the current TaskDialogBuilder instance with the description panel added
         * @see #createDescriptionScrollPane()
         * @see #createPanel(String, String, Component) 
         */
        public TaskDialogBuilder withDescriptionPanel() {
            dialog.add(createPanel("descriptionPanel", "Opis: ", createDescriptionScrollPane()));
            return this;
        }

        /**
         * @return a scroll pane allows to write a description
         * @see #withDescriptionPanel()
         */
        private JScrollPane createDescriptionScrollPane() {
            var descriptionTextArea = new JTextArea(3, 32);
            descriptionTextArea.setName("descriptionTextArea");
            descriptionTextArea.setLineWrap(true);
            descriptionScrollPane = new JScrollPane(descriptionTextArea);
            descriptionScrollPane.setName("descriptionScrollPane");
            descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            return descriptionScrollPane;
        }
        //endregion

        //region StatusPanel

        /**
         * Adds a {@link JPanel} with {@link #statusComboBox} to the {@link #dialog}. It contains a label and a combo
         * box which allows the user to choose a status: done or not.
         *
         * @return the current TaskDialogBuilder instance with the status panel added
         * @see #createStatusComboBox()
         * @see #createPanel(String, String, Component)
         */
        public TaskDialogBuilder withStatusPanel() {
            dialog.add(createPanel("statusPanel", "Wykonano: ", createStatusComboBox()));
            return this;
        }

        /**
         * @return a combo box which allows to choose a status: done or not
         * @see #withStatusPanel()
         */
        private JComboBox<String> createStatusComboBox() {
            statusComboBox = new JComboBox<>(new String[]{"Tak", "Nie"});
            statusComboBox.setName("statusComboBox");
            return statusComboBox;
        }
        //endregion

        //region ButtonsPanel

        /**
         * Adds a {@link JPanel} with {@link #buttons} to the {@link #dialog}. It contains a buttons which allows the
         * user to confirm or cancel.
         *
         * @return the current TaskDialogBuilder instance with buttons added
         * @see #createButtonsPanelWithButtons()
         */
        public TaskDialogBuilder withButtons() {
            dialog.add(createButtonsPanelWithButtons());
            return this;
        }

        /**
         * @return a {@link JPanel} with buttons which allows the user to confirm or cancel
         * @see #withButtons()
         */
        private JPanel createButtonsPanelWithButtons() {
            var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonsPanel.setName("buttonsPanel");
            buttons = new ArrayList<>(Arrays.asList(
                    new JButton("OK"),
                    new JButton("Anuluj")
            ));
            buttons.forEach(button -> {
                var buttonName = button.getText().equals("OK") ? "confirmButton" : "cancelButton";
                button.setName(buttonName);
                buttonsPanel.add(button);
            });
            return buttonsPanel;
        }
        //endregion

        /**
         * Builds the {@link TaskDialogView} from the current {@link TaskDialogBuilder} instance.
         *
         * @return a new TaskDialogView instance
         * @see TaskAddingDialog
         * @see TaskEditingDialog
         */
        public TaskDialogView build() {
            return new TaskDialogView(this);
        }

        /**
         * Creates a {@link JPanel} basing on the provided panel name, label text and component. It is used to create
         * all the panels in the {@link #dialog}. Example of the usage: {@link #withTasksPanel()}.
         *
         * @param panelName a name of the panel
         * @param infoLabel a text and name of the label
         * @param component a components which is added to the panel
         * @return a {@link JPanel} with the given name, label and component
         */
        private JPanel createPanel(String panelName, String infoLabel, Component component) {
            var panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            var label = new JLabel(infoLabel);
            label.setName("infoLabel");
            if (panelName != null) {
                panel.setName(panelName);
                panel.add(label);
                panel.add(component);
            }
            return panel;
        }
    }
}