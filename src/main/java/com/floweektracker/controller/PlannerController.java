package com.floweektracker.controller;

import com.floweektracker.MainFrame;
import com.floweektracker.service.*;
import com.floweektracker.view.*;
import lombok.*;

import java.awt.event.*;
import java.util.regex.Pattern;

/**
 * Represents the controller for the {@link PlannerView}. This class handles the interactions and logic related to the
 * {@link PlannerView}, communicating with the {@link PlannerService} and {@link TaskEditingDialogController}.
 * <br><br>
 * FIELDS: {@link #plannerService}, {@link #planner}, {@link #taskEditingDialogController}
 * <br><br>
 * LISTENERS METHODS: {@link #addListenerToPlanner()}, {@link #createPlannerViewListener()},
 * {@link #isTaskClicked(int, int)}, {@link #isLeftButtonClickedTwice(MouseEvent)},
 * {@link #isWeekdayNameClicked(int, int)}
 * <br><br>
 * OTHER METHODS: {@link #editTask()}, {@link #openFullWeekdaySchedule()}
 */
@Getter
public class PlannerController {
    private final PlannerService plannerService = PlannerService.getService();
    @Getter(AccessLevel.PRIVATE)
    private final PlannerView planner = plannerService.getPlanner();
    private final TaskEditingDialogController taskEditingDialogController;

    /**
     * Creates a controller for the {@link PlannerView}, initializing the {@link #taskEditingDialogController}. It also
     * adds a listener to the {@link #planner} if there are less than 3 listeners.
     *
     * @see #addListenerToPlanner()
     * @see MainPanelController
     */
    public PlannerController() {
        this.taskEditingDialogController = new TaskEditingDialogController(new TaskEditingDialog());

        if (planner.getMouseListeners().length < 3) addListenerToPlanner();
    }

    //region listeners methods

    /**
     * Adds a mouse listener to the {@link #planner}.
     *
     * @see #createPlannerViewListener()
     * @see PlannerController()
     */
    private void addListenerToPlanner() {
        plannerService.getPlanner().addMouseListener(createPlannerViewListener());
    }

    /**
     * Creates a mouse listener for the {@link #planner}. If the left mouse button isn't clicked twice, it breaks the
     * methods. If the task was clicked twice, it calls the {@link #editTask()} method. If the weekday name was clicked
     * twice, it calls the {@link #openFullWeekdaySchedule()} method.
     *
     * @return a new mouse listener for the {@link #planner}
     * @see #isLeftButtonClickedTwice(MouseEvent)
     * @see #isTaskClicked(int, int)
     * @see #isWeekdayNameClicked(int, int)
     * @see #editTask()
     * @see #openFullWeekdaySchedule()
     * @see #addListenerToPlanner()
     */
    private MouseListener createPlannerViewListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent click) {
                if (!isLeftButtonClickedTwice(click)) return;

                if (isTaskClicked(planner.getSelectedRow(), planner.getSelectedColumn())) editTask();
                else if (isWeekdayNameClicked(planner.getSelectedRow(), planner.getSelectedColumn()))
                    openFullWeekdaySchedule();
            }
        };
    }

    /**
     * @param click a mouse event which should be checked
     * @return true if the {@code click} is not null, and the left button was clicked twice, otherwise false
     * @see #createPlannerViewListener()
     */
    private boolean isLeftButtonClickedTwice(MouseEvent click) {
        return (click != null) && (click.getClickCount() == 2) && (click.getButton() == MouseEvent.BUTTON1);
    }

    /**
     * @param row    a row of the {@link #planner}
     * @param column a column of the {@link #planner}
     * @return true if the clicked cell is not a first column, is not an edge row and contains a task, otherwise false
     * @see #createPlannerViewListener()
     */
    private boolean isTaskClicked(int row, int column) {
        var pattern = "\\((\\d+)/(\\d+)\\)";
        var value = planner.getValueAt(row, column).toString();
        var isNotEdgeRow = ((row > 0) && (row < planner.getRowCount() - 1));

        return (column > 0) && isNotEdgeRow && Pattern.compile(pattern).matcher(value).find();
    }

    /**
     * Checks if the clicked cell represents weekday header.
     *
     * @param row    a given row of the {@link #planner}
     * @param column a given column of the {@link #planner}
     * @return true if the clicked cell if the first row and not the first column, otherwise false
     */
    private boolean isWeekdayNameClicked(int row, int column) {
        return (row == 0) && (column > 0);
    }
    //endregion

    /**
     * Makes the {@link TaskEditingDialog} visible, which allows the user to edit a chosen task. It is called, when the
     * user clicks on a task in the {@link #planner}.
     *
     * @see #addListenerToPlanner()
     */
    private void editTask() {
        var weekday = plannerService.getSelectedWeekday();
        var time = plannerService.getSelectedTime();
        var task = TasksService.getService().getTaskByEventTime(weekday, time);

        if (task != null) taskEditingDialogController.getTaskEditingDialog().makeDialogVisible(task);
    }

    /**
     * Opens the full schedule for the selected weekday. Retrieves the selected weekday from {@link #plannerService} and
     * switches the card in the main frame to the corresponding panel.
     *
     * @see #createPlannerViewListener()
     */
    private void openFullWeekdaySchedule() {
        var weekday = plannerService.getSelectedWeekday().toString().toLowerCase();

        MainFrame.getMAIN_FRAME().switchCard("%sPanel".formatted(weekday));
    }
}