package com.floweektracker.controller;

import com.floweektracker.MainFrame;
import com.floweektracker.model.*;
import com.floweektracker.service.*;
import com.floweektracker.util.DialogUtils;
import com.floweektracker.view.MainPanelView;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents the controller for the {@link MainPanelView} class. This class handles the interactions and logic related
 * to the main panel view, including initializing and managing listeners for UI components such as buttons and dialogs.
 * <br><br>
 * Fields: {@link #taskAddingDialogController}, {@link #plannerController}, {@link #cleanerService},
 * {@link #plannerService}, {@link #tasksService}, {@link #taskNamesController}, {@link #view},
 * {@link #weekdayPlannerService}
 * <br><br>
 * Methods: {@link #areButtonsWithoutListeners()}, {@link #addListenerToButtons()}, {@link #createInfoButtonListener()},
 * {@link #createCleanerListener(int)}, {@link #deleteTask()}, {@link #isSelectedValueATask(String)},
 * {@link #deleteTasksForWeekdays(List)}, {@link #resetPoints(List)}
 */
@Getter
public class MainPanelController {
    //region fields
    private final TaskAddingDialogController taskAddingDialogController;
    private final PlannerController plannerController;
    private final CleanerService cleanerService;
    private final PlannerService plannerService;
    private final TasksService tasksService;
    private final TaskNamesController taskNamesController;
    private final MainPanelView view;
    private final WeekdayPlannerService weekdayPlannerService;
    //endregion

    /**
     * Public constructor for the {@link MainPanelController} class. Initializes required services and controllers, and
     * sets up listeners for UI components if they are not already set.
     *
     * @param mainPanelView              the main panel view
     * @param taskAddingDialogController the task adding dialog controller
     * @see #areButtonsWithoutListeners()
     * @see #addListenerToButtons()
     * @see MainFrame
     */
    public MainPanelController(@NotNull MainPanelView mainPanelView, TaskAddingDialogController taskAddingDialogController) {
        this.taskAddingDialogController = taskAddingDialogController;
        this.plannerService = PlannerService.getService();
        this.plannerController = new PlannerController();
        this.cleanerService = CleanerService.getService();
        this.tasksService = TasksService.getService();
        this.taskNamesController = new TaskNamesController(taskAddingDialogController, plannerController.getTaskEditingDialogController());
        this.view = mainPanelView;
        this.weekdayPlannerService = WeekdayPlannerService.getService();

        if (areButtonsWithoutListeners()) addListenerToButtons();
    }

    //region listeners methods
    /**
     * Checks if the buttons in the {@link #view} doesn't have any listeners.
     *
     * @return true if buttons don't have listeners, false otherwise
     * @see #MainPanelController(MainPanelView, TaskAddingDialogController)
     */
    private boolean areButtonsWithoutListeners() {
        return view.getButtons().stream().allMatch(button -> button.getActionListeners().length < 1);
    }

    /**
     * Adds listeners to the buttons in the {@link #view}. Each button is associated with specific actions such as
     * opening dialogs or deleting tasks.
     *
     * @see #createInfoButtonListener()
     * @see #deleteTask()
     * @see #createCleanerListener(int)
     * @see #MainPanelController(MainPanelView, TaskAddingDialogController)
     */
    public void addListenerToButtons() {
        var buttons = view.getButtons();
        var infoButton = view.getInfoButton();

        infoButton.addActionListener(createInfoButtonListener());
        buttons.getFirst().addActionListener(_ -> taskAddingDialogController.getTaskAddingDialog().makeDialogVisible());
        buttons.get(1).addActionListener(_ -> taskNamesController.getTaskNamesDialog().makeDialogVisible());
        buttons.get(2).addActionListener(_ -> deleteTask());
        buttons.get(3).addActionListener(createCleanerListener(3));
        buttons.get(4).addActionListener(createCleanerListener(4));
    }

    /**
     * Creates an ActionListener for the info button from {@link #view} that shows an option dialog to the user. Then it
     * checks if chosen option exists and isn't cancel option. At the end it switches to the corresponding panel.
     *
     * @return the ActionListener for the info button
     * @see #addListenerToButtons()
     * @see MainPanelView
     */
    private ActionListener createInfoButtonListener() {
        return _ -> {
            var options = new String[]{"Baza", "Planer", "Anuluj"};
            int result = JOptionPane.showOptionDialog(
                    null,
                    "Chcę uzyskać informacje o:",
                    "Uzyskaj informacje",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]
            );

            if ((result == -1) ||  (result == 2)) {
                var option = "%sPanel".formatted(options[2].toLowerCase());
                MainFrame.getMAIN_FRAME().switchCard(option);
            }
        };
    }

    /**
     * Creates an ActionListener for cleaner buttons from the {@link #view}. This ActionListener shows an option dialog
     * from {@link CleanerService}, then checks which option was chosen. If it is cancel, it breaks the action.
     * Otherwise, it checks which button was pressed and calls the corresponding deleting method.
     *
     * @param button the index of the button from the {@link #view}
     * @return the ActionListener for the cleaner button
     * @see #addListenerToButtons()
     * @see #deleteTasksForWeekdays(List)
     * @see #resetPoints(List)
     */
    private ActionListener createCleanerListener(int button) {
        return _ -> {
            var chosenOption = cleanerService.getCleaner().showOptionDialog();

            if (chosenOption == 1) return;

            if (button == 3) deleteTasksForWeekdays(cleanerService.getSelectedWeekdays());
            else resetPoints(cleanerService.getSelectedWeekdays());
        };
    }
    //endregion

    /**
     * Deletes the task selected in the {@link com.floweektracker.view.PlannerView} from the {@link PlannerService},
     * {@link TasksService} and {@link WeekdayPlannerService}. If no task is selected, shows to the user a dialog with
     * an error message.
     *
     * @see #addListenerToButtons()
     * @see MainPanelView
     */
    private void deleteTask() {
        var selectedValue = plannerService.getSelectedValue();

        if (isSelectedValueATask(selectedValue)) {
            var weekday = plannerService.getSelectedWeekday();
            var time = plannerService.getSelectedTime();
            var task = tasksService.getTaskByEventTime(weekday, time);

            tasksService.deleteTask(task);
            plannerService.deleteTask(task);
            weekdayPlannerService.deleteTask(task);
        } else {
            DialogUtils.showMessageDialog(
                    "Nie zaznaczono zadania",
                    "W celu usunięcia zadania, zaznacz je, a następnie kliknij przycisk \"Usuń zadanie\""
            );
        }
    }

    /**
     * Checks if the given value is a task based on a pattern and is not null.
     *
     * @param selectedValue the given selected value to check
     * @return true if the given selected value matches the task pattern, false otherwise
     * @see #deleteTask()
     */
    private boolean isSelectedValueATask(String selectedValue) {
        var pattern = "\\((\\d+)/(\\d+)\\)";

        return (selectedValue != null) && Pattern.compile(pattern).matcher(selectedValue).find();
    }

    /**
     * Deletes tasks for the specified weekdays. When {@link List} is null, it breaks the action. Otherwise, it deletes
     * all tasks from the {@link TasksService}, {@link PlannerService} and {@link WeekdayPlannerService}.
     *
     * @param weekdays the list of weekdays for which tasks should be deleted
     * @see #createCleanerListener(int)
     * @see #addListenerToButtons()
     */
    void deleteTasksForWeekdays(List<WeekDays> weekdays) {
        if (weekdays == null) return;

        for (WeekDays weekday : weekdays) {
            var tasks = tasksService.getTasksFromWeekday(weekday);

            tasksService.getSchedule().get(weekday).clear();

            for (SingleTask task : tasks) {
                plannerService.deleteTask(task);
                weekdayPlannerService.deleteTask(task);
            }
        }
    }

    /**
     * Resets the points for tasks on the specified weekdays, marking them as not done. When {@link List} is null, it
     * breaks the action. Otherwise, it resets the points for all tasks from the {@link TasksService},
     * {@link PlannerService} and the {@link WeekdayPlannerService}.
     *
     * @param weekdays the list of weekdays for which tasks' points should be reset
     * @see #createCleanerListener(int)
     * @see #addListenerToButtons()
     */
    void resetPoints(List<WeekDays> weekdays) {
        if (weekdays == null) return;

        for (WeekDays weekday : weekdays) {
            var tasks = tasksService.getTasksFromWeekday(weekday);

            for (SingleTask task : tasks) {
                var editedTask = task.copy();
                editedTask.setDone(false);

                tasksService.editTask(task, editedTask);
                plannerService.editTask(task, editedTask);
                weekdayPlannerService.editTask(task, editedTask);
            }
        }
    }
}