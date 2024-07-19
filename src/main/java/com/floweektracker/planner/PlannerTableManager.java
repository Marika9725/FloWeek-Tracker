package com.floweektracker.planner;

import com.floweektracker.tasksDatabase.SingleTaskManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents the management of the {@link DefaultTableModel} for a {@link #planner}. This class handles the
 * {@link JTable} that is displayed to the user as their weekly planner. It extends {@link PlannerDataManager} to manage
 * the planner's data.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #planner} - a {@link JTable} representing the UI of the weekly planner, displayed to the user</li>
 * </ul>
 * DefaultTableModel methods:
 * <ul>
 *     <li>{@link #createPlannerModel()} - creates a {@link DefaultTableModel} for the {@link #planner}</li>
 *     <li>{@link #collectSortedTimes()} - collects all unique times from the tasks in {@link #planners} and uses this
 *     information to determinate the number of rows in the {@link #createPlannerModel()} method</li>
 *     <li>{@link #countPoints()} - counts the achieved and total points for each day of the week</li>
 * </ul>
 * Planner Methods:
 * <ul>
 *     <li>{@link #setupPlanner()} - configures the {@link #planner}</li>
 *     <li>{@link #refreshPlanner()} - refreshes {@link #planner} after data changing</li>
 * </ul>
 *
 * Getters: {@link #planner}
 */
public class PlannerTableManager extends PlannerDataManager {
    //region Fields
    /**
     * Contains a new {@link JTable} object representing the UI of the {@link #planners}. This allows the user to see
     * their weekly planner.
     *
     * @see #PlannerTableManager()
     * @see #setupPlanner()
     * @see #refreshPlanner()
     * @see #getPlanner()
     */
    private final JTable planner;
    //endregion

    /**
     * Constructs a new {@link PlannerTableManager} instance. Calls the {@code super()} method to invoke the
     * {@link PlannerDataManager} constructor. Then, initializes the {@link #planner} field. Finally, calls the
     * {@link #setupPlanner()} method to configure the {@link #planner}.
     *
     * @see com.floweektracker.main.MainPanelUI#plannerTableManager
     * @see com.floweektracker.main.MainPanelUI#planner
     */
    public PlannerTableManager() {
        super();
        this.planner = new JTable();

        setupPlanner();
    }

    //region DefaultTableModel methods

    /**
     * Creates a {@link DefaultTableModel} for the {@link #planner}, populating it with data from the {@link #planners}.
     * The table model is initialized with column names and includes task data for each day of the week. A summary row
     * is added at the end to show the achieved and total points.
     *
     * @return a {@link DefaultTableModel} for the {@link #planner}
     * @see #collectSortedTimes()
     * @see #countPoints()
     * @see #refreshPlanner()
     */
    private DefaultTableModel createPlannerModel() {
        final var columnNamesPL = Stream.concat(Stream.of(""), Stream.of(WeekDays.getWeekDaysPL())).toArray(String[]::new);
        final var plannerModel = new DefaultTableModel(columnNamesPL, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        final var times = collectSortedTimes();
        final var data = new String[columnNamesPL.length];

        plannerModel.addRow(columnNamesPL);

        Arrays.stream(times).forEach(time -> {
            data[0] = time;
            byte i = 1;
            SingleTaskManager task;

            for (WeekDays weekDay : WeekDays.values()) {
                if ((task = getPlanners().get(weekDay.name()).get(time)) != null) {
                    byte points = task.getIsDone() ? task.getPriority() : 0;
                    var cellValue = new StringBuilder()
                            .append(task.getTaskName())
                            .append("(").append(points).append("/").append(task.getPriority()).append(")");

                    data[i] = task.getIsDone() ? ("<html><strike>" + cellValue + "</strike></html>") : cellValue.toString();
                } else data[i] = "-";
                i++;
            }

            plannerModel.addRow(data);
        });

        plannerModel.addRow(countPoints());

        return plannerModel;
    }

    /**
     * Collects and sorts times from the {@link #planners}. This method assists the {@link #createPlannerModel()} method
     * by determining the quantity of rows and retrieving data from the {@link #planners}.
     *
     * @return an array of {@link String} representing all sorted times from the {@link #planners}.
     */
    private String[] collectSortedTimes() {
        var times = new TreeSet<String>();

        getPlanners().forEach((_, tasks) -> tasks.forEach((time, _) -> times.add(time)));

        return times.toArray(new String[0]);
    }

    /**
     * Creates the last row of the {@link #planner} representing the summary of points for each day of the week. The
     * summary shows the total points achievable and the points achieved by th user.
     *
     * @return an array of {@link String} representing the summary row for the {@link #planner}
     * @see #createPlannerModel()
     */
    private String[] countPoints() {
        var pointsSummaryForEachDay = new ArrayList<String>();
        pointsSummaryForEachDay.add("PUNKTY");

        Arrays.stream(WeekDays.values()).forEach(weekDay -> {
            int total = getPlanners().get(weekDay.name()).values().stream()
                    .mapToInt(SingleTaskManager::getPriority)
                    .sum();
            int achieved = getPlanners().get(weekDay.name()).values().stream()
                    .filter(SingleTaskManager::getIsDone)
                    .mapToInt(SingleTaskManager::getPriority)
                    .sum();

            pointsSummaryForEachDay.add(achieved + "/" + total);
        });

        return pointsSummaryForEachDay.toArray(new String[0]);
    }
    //endregion

    //region Planner methods

    /**
     * Configures the {@link #planner} by setting its visibility, resizability, and cell selection setting. Finally,
     * invokes the {@link #refreshPlanner()} method to set the model and customize the {@link #planner}.
     *
     * @see #PlannerTableManager()
     */
    private void setupPlanner() {
        planner.setVisible(false);
        planner.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        planner.setCellSelectionEnabled(false);

        refreshPlanner();
    }

    /**
     * Sets the model for the {@link #planner} and customizes its appearance:
     * <ul>
     *     <li>The name of the current weekday, all tasks for the current weekday, and the current time are displayed in
     *     black; others are in gray.</li>
     *     <li>Completed tasks have their names crossed out in the {@link #planner}.</li>
     * </ul>
     *
     * @see com.floweektracker.main.MainListeners#refreshApp()
     * @see #setupPlanner()
     */
    public void refreshPlanner() {
        planner.setModel(createPlannerModel());

        for (int column = 0; column < planner.getColumnCount(); column++) {
            planner.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    var component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);

                    if (column == 0 && row > 0 && row < planner.getRowCount() - 1) {
                        var localTime = LocalTime.now().format(new DateTimeFormatterBuilder().append(DateTimeFormatter.ofPattern("HH")).toFormatter());
                        var plannerHour = value.toString().substring(0, 2);

                        if (localTime.equals(plannerHour)) component.setForeground(Color.black);
                        else component.setForeground(Color.gray);
                    } else if (LocalDate.now().getDayOfWeek().getValue() == column)
                        component.setForeground(Color.black);
                    else component.setForeground(Color.gray);

                    return component;
                }
            });
        }

        for (int column = 1; column < planner.getColumnCount(); column++) {
            for (int row = 1; row < planner.getRowCount() - 1; row++) {
                var value = planner.getValueAt(row, column).toString();
                if (!value.contains("(0/"))
                    planner.setValueAt(("<html><strike>" + value + "</strike></html>"), row, column);
            }
        }

        planner.setVisible(true);
    }
    //endregion

    //region Getters

    /**
     * @return a {@link #planner} representing UI of the user's weekly planner.
     * @see com.floweektracker.main.MainPanelUI#planner
     * @see com.floweektracker.main.MainListeners#createPlannerListener()
     */
    public JTable getPlanner() {return planner;}
    //endregion
}