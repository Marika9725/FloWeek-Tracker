package com.floweektracker.view;

import com.floweektracker.model.*;
import com.floweektracker.service.TasksService;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Represents a UI of the planner which is used to display a weekly planner to the user based on their tasks. It extends
 * {@link JTable} and is a singleton.
 * <br><br>
 * Fields: {@link #view}, {@link #tasksService}
 * <br><br>
 * Methods: {@link #setUpPlanner()}, {@link #createPlannerModel()}, {@link #createColumnNames()},
 * {@link #buildRowData(LocalTime)}, {@link #buildPointsRow()}
 */
public class PlannerView extends JTable {
    @Getter
    private static final PlannerView view = new PlannerView();
    private final TasksService tasksService = TasksService.getService();

    /**
     * The private constructor which sets up the {@link PlannerView}.
     *
     * @see #setUpPlanner()
     */
    private PlannerView() {
        setUpPlanner();
    }

    /**
     * Sets up the {@link PlannerView} by setting its name, auto resize mode, cell selection and a model created by the
     * {@link #createPlannerModel()}. It also sets the cell renderer for each column.
     *
     * @see PlannerView()
     * @see #createCellRenderer()
     */
    private void setUpPlanner() {
        setName("planner");
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setCellSelectionEnabled(false);
        setModel(createPlannerModel());
        getColumnModel()
                .getColumns()
                .asIterator()
                .forEachRemaining(column -> column.setCellRenderer(createCellRenderer()));
    }

    /**
     * Creates a model for the weekly planner. First row is reserved for the column names, and last row is reserved for
     * the summary of the points. Other rows represents tasks from the schedule and their times. The cells are set up as
     * not editable.
     *
     * @return a {@link DefaultTableModel} of the planner.
     * @see #createColumnNames()
     * @see TasksService#collectSortedTimes()
     * @see #buildRowData(LocalTime)
     * @see #buildPointsRow()
     * @see #setUpPlanner()
     */
    private DefaultTableModel createPlannerModel() {
        var columnNames = createColumnNames();
        final var model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        var times = tasksService.collectSortedTimes();

        model.addRow(columnNames);
        times.forEach(time -> model.addRow(buildRowData(time)));
        model.addRow(buildPointsRow());

        return model;
    }

    /**
     * Creates a cell renderer for the weekly planner. The renderer is a {@link DefaultTableCellRenderer} that is used
     * to customize the appearance of the cells in the planner. The text is aligned to the center of the cell.
     * <ul>
     *     <li>The first column represents the times of the tasks from the schedule. If the current time is equal to
     *     the time in the cell, the text is displayed in black, otherwise in gray.</li>
     *     <li>Other columns represent the days of the week. The current day of the week is displayed in black, others
     *     are in gray.</li>
     * </ul>
     *
     * @return a custom cell renderer for the weekly planner.
     * @see #setUpPlanner()
     */
    private DefaultTableCellRenderer createCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                var component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                var isFirstColumn = (column == 0) && (row > 0) && (row < view.getRowCount() - 1);
                var isActualDateEqualsPlannerDate = LocalDate.now().getDayOfWeek().getValue() == column;
                var colour = Color.GRAY;

                if (isFirstColumn) {
                    var actualTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH"));
                    var plannerHour = value.toString().substring(0, 2);
                    colour = actualTime.equals(plannerHour) ? Color.black : Color.gray;
                } else if (isActualDateEqualsPlannerDate) {
                    colour = Color.black;
                } else colour = Color.gray;

                component.setForeground(colour);

                return component;
            }
        };
    }

    /**
     * Creates a {@link String} array of the column names for the weekly planner. First column is reserved to the times,
     * others represents days of the week.
     *
     * @return a {@link String} array with column names.
     * @see #createPlannerModel()
     * @see #buildRowData(LocalTime)
     */
    private String[] createColumnNames() {
        return Stream.concat(
                Stream.of(""),
                Stream.of(WeekDays.getWeekdaysPL())
        ).toArray(String[]::new);
    }

    /**
     * Builds a row for the weekly planner based on the given {@link LocalTime}. First column represents the given time,
     * others contains tasks for each day of the week, corresponding with the given time. Each task in the cell has its
     * name, achieved points and total points. When there is no task in the cell, "-" is displayed. When task is done,
     * its name is displayed with a strike through.
     *
     * @param time a {@link LocalTime} of the tasks from the schedule.
     * @return a {@link String} array of the row data
     * @see #createPlannerModel()
     */
    private String[] buildRowData(@NotNull LocalTime time) {
        var cells = new String[createColumnNames().length];

        cells[0] = time.toString();

        for (WeekDays weekday : WeekDays.values()) {
            var cellNum = weekday.getPosition() + 1;
            var task = tasksService.getTaskByEventTime(weekday, time);

            if (task != null) {
                var cellValue = String.format("%s(%d/%d)", task.getTaskName(), task.calculatePoints(), task.getPriority());
                cells[cellNum] = task.isDone() ? String.format("<html><strike>%s</strike></html>", cellValue) : cellValue;
            } else cells[cellNum] = "-";
        }

        return cells;
    }

    /**
     * Builds the last row of the weekly planner, which contains summary of the points for each day of the week. First
     * column is reserved for the word "PUNKTY", others represents days of the week. Summary is built based on the
     * points from the tasks which the user achieved and sum of total points which he could get.
     *
     * @return a {@link String} array of the row data
     * @see #createPlannerModel()
     */
    private String[] buildPointsRow() {
        var pointsSummaryForEachDay = new ArrayList<String>();
        pointsSummaryForEachDay.add("PUNKTY");

        WeekDays.getListedWeekdays().forEach(weekDay -> {
            int achieved = tasksService.countPoints(weekDay, SingleTask::calculatePoints);
            int total = tasksService.countPoints(weekDay, SingleTask::getPriority);

            pointsSummaryForEachDay.add(String.format("%d/%d", achieved, total));
        });

        return pointsSummaryForEachDay.toArray(new String[0]);
    }
}