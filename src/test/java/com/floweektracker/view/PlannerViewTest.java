package com.floweektracker.view;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.service.TasksService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlannerViewTest {
    private final TestHelper helper = new TestHelper();
    private final PlannerView planner = PlannerView.getView();
    private final TasksService tasksService = TasksService.getService();

    @BeforeEach
    void cleanUp() {
        var model = (DefaultTableModel) planner.getModel();

        for (int i = 1; i < model.getRowCount() - 1; i++) {model.removeRow(i);}
        for (int i = 1; i < 8; i++) model.setValueAt("0/0", model.getRowCount() - 1, i);

        tasksService.getSchedule().values().forEach(Map::clear);
    }

    @Test
    void constructorShouldInitializePlanner() {
        assertAll(
                () -> assertThat(planner, is(notNullValue())),
                () -> assertThat(planner, is(instanceOf(JTable.class))),
                () -> assertThat(planner.getName(), is("planner")),
                () -> assertThat(planner.getAutoResizeMode(), is(JTable.AUTO_RESIZE_ALL_COLUMNS)),
                () -> assertThat(planner.getCellSelectionEnabled(), is(false)),
                () -> assertThat(planner.getModel(), is(instanceOf(DefaultTableModel.class)))
        );
    }

    @Test
    void shouldCreatePlannerWithTasksGotFromTasksService() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        var tasks = createTasksList();
        tasks.forEach(tasksService::addTask);

        //when
        var table = createPlannerViewTable();

        assertAll(
                () -> assertThat(table.getRowCount(), is(5)),
                () -> tasks.forEach(task -> assertTrue(isTaskInPlanner(table, task)))
        );
    }

    @Test
    void firstRowOfPlannerShouldContainsWeekdayNames() {
        //given+when
        var firstRowData = IntStream.range(0, planner.getColumnCount())
                .mapToObj(column -> planner.getValueAt(0, column))
                .toArray();

        var expectedValues = Stream.concat(Stream.of(""), Stream.of(WeekDays.values()).map(WeekDays::getWeekdayPL)).toArray();

        //then
        assertThat(firstRowData, is(expectedValues));
    }

    @Test
    void lastRowOfPlannerShouldContainsPointsSummarize() {
        //given+when
        var actualValues = IntStream.range(0, planner.getColumnCount())
                .mapToObj(column -> planner.getValueAt(planner.getRowCount() - 1, column))
                .toArray();

        var expectedValues = IntStream.range(0, planner.getColumnCount())
                .mapToObj(column -> (column == 0) ? "PUNKTY" : "0/0")
                .toArray();

        //then
        assertThat(actualValues, is(expectedValues));
    }

    //region helper methods
    @NotNull
    private JTable createPlannerViewTable() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        var table = new JTable();
        var method = PlannerView.class.getDeclaredMethod("createPlannerModel");
        method.setAccessible(true);
        table.setModel((DefaultTableModel) method.invoke(planner));

        return table;
    }

    @NotNull
    private ArrayList<SingleTask> createTasksList() {
        var tasks = new ArrayList<SingleTask>();
        while (tasks.size() != 4) tasks.add(helper.createBaseTask());

        tasks.get(1).setWeekday(WeekDays.FRIDAY);
        tasks.get(2).setTime(LocalTime.of(15, 0));
        tasks.get(3).setWeekday(WeekDays.SUNDAY);
        tasks.get(3).setTime(LocalTime.of(9, 30));

        return tasks;
    }

    private boolean isTaskInPlanner(JTable table, SingleTask task) {
        var taskName = "%s(%d/%d)".formatted(task.getTaskName(), 0, task.getPriority());

        int column = IntStream.range(1, table.getColumnCount())
                .filter(colNum -> table.getValueAt(0, colNum).equals(task.getWeekday().getWeekdayPL()))
                .findFirst()
                .orElse(-1);

        int row = IntStream.range(1, table.getRowCount() - 1)
                .filter(rowNum -> table.getValueAt(rowNum, 0).equals(task.getTime().toString()))
                .findFirst()
                .orElse(-1);

        var value = (column > 0 && row > 0) ? table.getValueAt(row, column).toString() : null;

        return (value != null) ? value.equals(taskName) : false;
    }
    //endregion
}