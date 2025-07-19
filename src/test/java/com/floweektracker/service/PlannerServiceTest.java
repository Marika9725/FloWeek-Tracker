package com.floweektracker.service;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlannerServiceTest {
    private static final TestHelper helper = new TestHelper();
    private final PlannerService plannerService = PlannerService.getService();
    private final TasksService tasksService = TasksService.getService();
    private static final SingleTask baseTask = helper.createBaseTask();
    private final Map<WeekDays, Map<LocalTime, SingleTask>> schedule = tasksService.getSchedule();
    private final JTable planner = plannerService.getPlanner();

    @BeforeEach
    void setUp(TestInfo testInfo) {
        if ((testInfo != null) && testInfo.getTags().contains("addTask")) plannerService.addTask(baseTask);
    }

    @AfterEach
    void cleanUp() {
        var model = (DefaultTableModel) planner.getModel();

        while (model.getRowCount() > 2) model.removeRow(1);

        for (Map<LocalTime, SingleTask> tasks : schedule.values()) tasks.clear();
    }

    @Nested
    class AddTaskTests {
        @Test
        void shouldAddRowWithTaskWhenTimeOfNewTaskNotExistInPlanner() {
            //given
            var numRowBefore = planner.getRowCount();

            //when
            var isTaskAdded = plannerService.addTask(baseTask); var numRowAfter = planner.getRowCount();

            //then
            assertAll(() -> assertThat(numRowBefore, is(2)), () -> assertTrue(isTaskAdded), () -> assertThat(numRowAfter, is(3)));
        }

        @Test
        void shouldAddOnlyNewValueWhenItsTimeExistInPlanner() {
            //given
            var newTask = helper.createEditedTask(); plannerService.addTask(baseTask);

            var numRowBefore = planner.getRowCount();

            //when
            var isTaskAdded = plannerService.addTask(newTask); var numRowAfter = planner.getRowCount();
            var isOldTaskExist = plannerService.isTaskInPlanner(baseTask);
            var isNewTaskExist = plannerService.isTaskInPlanner(newTask);

            //then
            assertAll(() -> assertThat(numRowBefore, is(3)), () -> assertTrue(isTaskAdded), () -> assertThat(numRowAfter, is(3)), () -> assertTrue(isOldTaskExist), () -> assertTrue(isNewTaskExist));
        }

        @Tag("addTask")
        @Test
        void shouldHaveCorrectTimeSequenceAfterAddNewRow() {
            //given
            var tasks = prepareTasks();

            var expectedSequence = Stream.concat(Stream.of(baseTask.getTime().toString()), tasks.stream().map(task -> task.getTime().toString())).sorted().toArray(String[]::new);

            //when
            tasks.forEach(plannerService::addTask);

            var actualSequence = IntStream.range(1, 5).mapToObj(i -> planner.getModel().getValueAt(i, 0).toString()).toArray(String[]::new);

            //then
            assertThat(actualSequence, is(expectedSequence));
        }

        @NotNull
        private ArrayList<SingleTask> prepareTasks() {
            var tasks = new ArrayList<>(List.of(helper.createBaseTask(), helper.createBaseTask(), helper.createBaseTask()));
            tasks.getFirst().setTime(LocalTime.of(13, 30)); tasks.get(1).setTime(LocalTime.of(9, 30));
            tasks.getLast().setTime(LocalTime.of(12, 30)); return tasks;
        }

        @Test
        void shouldActualizePointsSummaryOfWeekdayWhenTaskIsAdded() {
            //given
            var expectedPointsSum = String.format("%s/%s", baseTask.calculatePoints(), baseTask.getPriority());

            //when
            tasksService.addTask(baseTask); plannerService.addTask(baseTask);

            var actualPointsSum = planner.getValueAt(planner.getRowCount() - 1, 1);

            //then
            assertThat(actualPointsSum, is(expectedPointsSum));
        }

        @Test
        void shouldNotAddTaskIfTaskIsValid() {
            assertFalse(plannerService.addTask(null));
        }
    }

    @Nested
    class DeleteTaskWithSingleTaskTests {
        @Tag("addTask")
        @Test
        void controllerShouldDeleteRowWhenThereIsOnlyOneTask_SingleTask() {
            //given
            var rowCountBefore = planner.getRowCount();
            var isTaskExistBefore = plannerService.isTaskInPlanner(baseTask);

            //when
            var isTaskDeleted = plannerService.deleteTask(baseTask); var rowCountAfter = planner.getRowCount();
            var isTaskExistAfter = plannerService.isTaskInPlanner(baseTask);

            //then
            assertAll(() -> assertThat(rowCountBefore, is(3)), () -> assertTrue(isTaskExistBefore), () -> assertTrue(isTaskDeleted), () -> assertThat(rowCountAfter, is(2)), () -> assertFalse(isTaskExistAfter));
        }

        @Test
        void controllerShouldDeleteOnlyTaskWhenThereIsMoreThanOneInRow_SingleTask() {
            //given
            var task2 = helper.createEditedTask(); plannerService.addTask(helper.createBaseTask());
            plannerService.addTask(task2);

            var rowCountBefore = planner.getRowCount();
            var isTask1ExistBefore = plannerService.isTaskInPlanner(baseTask);
            var isTask2ExistBefore = plannerService.isTaskInPlanner(task2);

            //when
            var isTaskDeleted = plannerService.deleteTask(baseTask);

            var rowCountAfter = planner.getRowCount(); var isTask1ExistAfter = plannerService.isTaskInPlanner(baseTask);
            var isTask2ExistAfter = plannerService.isTaskInPlanner(task2);

            //then
            assertAll(() -> assertThat(rowCountBefore, is(3)), () -> assertTrue(isTask1ExistBefore), () -> assertTrue(isTask2ExistBefore), () -> assertTrue(isTaskDeleted), () -> assertThat(rowCountAfter, is(3)), () -> assertFalse(isTask1ExistAfter), () -> assertTrue(isTask2ExistAfter));
        }

        @Test
        void shouldActualizePointsSummaryOfWeekdayWhenTaskIsDeleted_SingleTask() {
            //given
            tasksService.addTask(baseTask); plannerService.addTask(baseTask);

            var expectedPointsSumBefore = ("0/5");
            var pointsSumBefore = planner.getValueAt(planner.getRowCount() - 1, 1);

            //when
            tasksService.deleteTask(baseTask); plannerService.deleteTask(baseTask);

            var expectedPointsSumAfter = ("0/0"); var pointsSumAfter = planner.getValueAt(planner.getRowCount() - 1, 1);

            //then
            assertAll(() -> assertThat(pointsSumBefore, is(expectedPointsSumBefore)), () -> assertThat(pointsSumAfter, is(expectedPointsSumAfter)));
        }

        @Test
        void shouldReturnFalseIfParamIsInvalid() {
            assertFalse(plannerService.deleteTask(null));
        }
    }

    @Nested
    class DeleteTaskWithEvenTimeTests {
        @Tag("addTask")
        @Test
        void controllerShouldDeleteRowWhenThereIsOnlyOneTask_EventTime() {
            //given
            var rowCountBefore = planner.getRowCount();
            var isTaskExistBefore = plannerService.isTaskInPlanner(helper.createBaseTask());

            //when
            var isTaskDeleted = plannerService.deleteTask(baseTask); var rowCountAfter = planner.getRowCount();
            var isTaskExistAfter = plannerService.isTaskInPlanner(helper.createBaseTask());

            //then
            assertAll(() -> assertThat(rowCountBefore, is(3)), () -> assertTrue(isTaskExistBefore), () -> assertTrue(isTaskDeleted), () -> assertThat(rowCountAfter, is(2)), () -> assertFalse(isTaskExistAfter));
        }

        @Tag("addTask")
        @Test
        void controllerShouldDeleteOnlyTaskWhenThereIsMoreThanOneInRow_EventTime() {
            //given
            var task2 = helper.createEditedTask(); plannerService.addTask(task2);

            var rowCountBefore = planner.getRowCount();
            var isTask1ExistBefore = plannerService.isTaskInPlanner(baseTask);
            var isTask2ExistBefore = plannerService.isTaskInPlanner(task2);

            //when
            var isTaskDeleted = plannerService.deleteTask(baseTask); var rowCountAfter = planner.getRowCount();
            var isTask1ExistAfter = plannerService.isTaskInPlanner(baseTask);
            var isTask2ExistAfter = plannerService.isTaskInPlanner(task2);

            //then
            assertAll(() -> assertThat(rowCountBefore, is(3)), () -> assertTrue(isTask1ExistBefore), () -> assertTrue(isTask2ExistBefore), () -> assertTrue(isTaskDeleted), () -> assertThat(rowCountAfter, is(3)), () -> assertFalse(isTask1ExistAfter), () -> assertTrue(isTask2ExistAfter));
        }

        @Test
        void shouldActualizePointsSummaryOfWeekdayWhenTaskIsDeleted_EventTime() {
            //given
            tasksService.addTask(baseTask); plannerService.addTask(baseTask);

            var expectedPointsSumBefore = ("0/5");
            var pointsSumBefore = planner.getValueAt(planner.getRowCount() - 1, 1);

            //when
            tasksService.deleteTask(baseTask); plannerService.deleteTask(baseTask);

            var expectedPointsSumAfter = ("0/0"); var pointsSumAfter = planner.getValueAt(planner.getRowCount() - 1, 1);

            //then
            assertAll(() -> assertThat(pointsSumBefore, is(expectedPointsSumBefore)), () -> assertThat(pointsSumAfter, is(expectedPointsSumAfter)));
        }
    }

    @Nested
    class IsTaskInPlannerTests {
        @Tag("addTask")
        @Test
        void shouldFindTaskInPlannerByEventTime() {
            //given+when
            var isTaskInPlanner = plannerService.isTaskInPlanner(baseTask);

            //then
            assertTrue(isTaskInPlanner);
        }

        @Test
        void shouldNotFindTaskInPlannerByEventTimeIfTaskIsNotExist() {
            //given+when
            var isTaskInPlanner = plannerService.isTaskInPlanner(baseTask);

            //then
            assertFalse(isTaskInPlanner);
        }
    }

    @Nested
    class EditTaskTests {
        @ParameterizedTest
        @MethodSource("createTasksForEditTask")
        void shouldDeleteOldTask(SingleTask baseTask, SingleTask editedTask) {
            //given
            plannerService.addTask(baseTask);

            //when
            var isTaskEdited = plannerService.editTask(baseTask, editedTask);

            //then
            assertAll(() -> assertTrue(isTaskEdited), () -> assertFalse(plannerService.isTaskInPlanner(baseTask)));
        }

        @ParameterizedTest
        @MethodSource("createTasksForEditTask")
        void shouldAddNewTask(SingleTask baseTask, SingleTask editedTask) {
            //given
            plannerService.addTask(baseTask);

            //when
            var isTaskEdited = plannerService.editTask(baseTask, editedTask);

            //then
            assertAll(() -> assertTrue(isTaskEdited), () -> assertTrue(plannerService.isTaskInPlanner(editedTask)));
        }

        @ParameterizedTest
        @MethodSource("createValidTasksForEditTask")
        void shouldNotActualizePlannerIfTasksAreValid(SingleTask baseTask, SingleTask editedTask) {
            //given+when
            var isTaskEdited = plannerService.editTask(baseTask, editedTask);

            //then
            assertFalse(isTaskEdited);
        }

        @Test
        void shouldReturnFalseIfOldTaskIsNotInPlannerBeforeEditing() {
            //given
            var editedTask = helper.createEditedTask();

            //when
            var isTaskEdited = plannerService.editTask(baseTask, editedTask);

            //then
            assertAll(() -> assertFalse(isTaskEdited), () -> assertFalse(plannerService.isTaskInPlanner(baseTask)), () -> assertFalse(plannerService.isTaskInPlanner(editedTask)));
        }

        @Tag("addTask")
        @Test
        void shouldReturnFalseIfEditedTaskIsInThePlannerBeforeEditing() {
            //given
            var editedTask = helper.createEditedTask();
            plannerService.addTask(editedTask);

            //when
            var isTaskEdited = plannerService.editTask(baseTask, editedTask);

            //then
            assertAll(
                    () -> assertFalse(isTaskEdited),
                    () -> assertTrue(plannerService.isTaskInPlanner(baseTask)),
                    () -> assertTrue(plannerService.isTaskInPlanner(editedTask)));
        }

        @Test
        void shouldReturnFalseIfBothTasksAreEquals() {
            //given
            var editedTask = helper.createBaseTask();

            //when
            var isTaskEdited = plannerService.editTask(baseTask, editedTask);

            //then
            assertFalse(isTaskEdited);
        }

        private static Stream<Arguments> createTasksForEditTask() {
            var taskWithEditedWeekday = helper.createEditedTask();

            var taskWithEditedTime = helper.createBaseTask(); taskWithEditedTime.setTime(LocalTime.of(15, 0));

            return Stream.of(Arguments.of(baseTask, taskWithEditedWeekday), Arguments.of(baseTask, taskWithEditedTime));
        }

        private static Stream<Arguments> createValidTasksForEditTask() {
            return Stream.of(Arguments.of(null, null), Arguments.of(baseTask, null), Arguments.of(null, helper.createEditedTask()));
        }
    }

    @Nested
    class GetSelectedValueTests {
        @Tag("addTask")
        @Test
        void shouldReturnCorrectValueWhenTaskInPlannerViewIsSelected() {
            //given
            var expectedValue = "%s(%d/%d)".formatted(baseTask.getTaskName(), baseTask.calculatePoints(), baseTask.getPriority());

            //when
            selectCellInPlannerView(1, 1);
            var actualValue = plannerService.getSelectedValue();

            //then
            assertThat(actualValue, is(expectedValue));
        }

        @Tag("addTask")
        @Test
        void shouldReturnNullWhenTaskInPlannerIsNotSelected() {
            //given+when
            var actualValue = plannerService.getSelectedValue();

            //then
            assertThat(actualValue, is(nullValue()));
        }
    }

    @Nested
    class GetSelectedTimeTests {
        @Tag("addTask")
        @Test
        void shouldReturnCorrectTimeWhenTaskIsSelected() {
            //given
            var expectedTime = baseTask.getTime();

            //when
            planner.changeSelection(1, 1, true, false); var actualTime = plannerService.getSelectedTime();

            //then
            assertThat(actualTime, is(expectedTime));
        }

        @Tag("addTask")
        @Test
        void shouldReturnCorrectTimeIfEmptyCellIsSelected() {
            //given
            var expectedTime = baseTask.getTime();

            //when
            planner.changeSelection(1, 1, true, false);
            var actualTime = plannerService.getSelectedTime();

            //then
            assertThat(actualTime, is(expectedTime));
        }

        @Test
        void shouldReturnNullIfWeekDayNameIsSelected() {
            //given+when
            planner.changeSelection(0, 1, true, false);
            var actualTime = plannerService.getSelectedTime();

            //then
            assertThat(actualTime, is(nullValue()));
        }

        @Test
        void shouldReturnNullIfPointsSummaryIsSelected() {
            //given+when
            planner.changeSelection(1, 1, true, false);
            var actualTime = plannerService.getSelectedTime();

            //then
            assertThat(actualTime, is(nullValue()));
        }

        @Tag("addTask")
        @Test
        void shouldReturnCorrectTimeIfTimeCellIsSelected() {
            //given
            var expectedTime = baseTask.getTime();

            //when
            selectCellInPlannerView(1, 0);
            var actualTime = plannerService.getSelectedTime();

            //then
            assertThat(actualTime, is(expectedTime));
        }
    }

    @Nested
    class GetSelectedWeekdayTests {
        @Tag("addTask") @Test
        void shouldReturnCorrectWeekdayWhenTaskIsSelected() {
            //given
            var expectedWeekday = baseTask.getWeekday();

            //when
            selectCellInPlannerView(1, 1);
            var actualWeekday = plannerService.getSelectedWeekday();

            //then
            assertThat(actualWeekday, is(expectedWeekday));
        }

        @Tag("addTask") @Test
        void shouldReturnCorrectWeekdayIfEmptyCellIsSelected() {
            //given+when
            selectCellInPlannerView(1, 2);
            var actualWeekday = plannerService.getSelectedWeekday();

            //then
            assertThat(actualWeekday, is(WeekDays.TUESDAY));
        }

        @Test
        void shouldReturnCorrectWeekdayIfWeekdayCellIsSelected() {
            //given
            var expectedWeekday = baseTask.getWeekday();

            //when
            selectCellInPlannerView(0, 1);
            var actualWeekday = plannerService.getSelectedWeekday();

            //then
            assertThat(actualWeekday, is(expectedWeekday));
        }

        @Test
        void shouldReturnNullWhenTimeCellIsSelected() {
            //given+when
            planner.changeSelection(1, 0, true, false);
            var actualWeekday = plannerService.getSelectedWeekday();

            //then
            assertThat(actualWeekday, is(nullValue()));
        }
    }

    private void selectCellInPlannerView(int row, int column) {
        planner.setRowSelectionInterval(row, row);
        planner.setColumnSelectionInterval(column, column);
    }
}