package com.floweektracker.service;

import com.floweektracker.*;
import com.floweektracker.model.*;
import com.floweektracker.view.WeekdayPlannerView;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class WeekdayPlannerServiceTest {
    //region Fields
    private static final TestHelper helper = new TestHelper();
    private SingleTask baseTask;
    private final List<String> weekdays = Arrays.asList("MONDAY", "TUESDAY");
    private final WeekdayPlannerService weekdayPlannerService = WeekdayPlannerService.getService();
    //endregion

    //region setup & cleanup
    @BeforeEach
    void setUp(TestInfo testInfo) {
        this.baseTask = helper.createBaseTask();
        if (testInfo.getTags().contains("withTask")) weekdayPlannerService.addTask(baseTask);
    }

    @AfterEach
    void cleanUp() {
        this.baseTask = null;
        var weekdays = new WeekDays[]{WeekDays.MONDAY, WeekDays.TUESDAY};

        for (WeekDays weekday : weekdays) {
            var contentPanel = getWeekdayPlannerView(weekday.toString()).getContentPanel();

            for (var taskPanel : contentPanel.getComponents()) {contentPanel.remove(taskPanel);}
        }

        //close taskEditingDialog
        for (Window window : Window.getWindows()) {
            var title = window.getName();

            if (title != null && title.equalsIgnoreCase("taskEditingDialog")) {
                window.dispose();
            }
        }
    }
    //endregion

    @Nested
    class AddTaskTests {
        @Test
        void shouldAddNewTaskToWeekdayPlanner() {
            //given
            var numTasksBefore = getTaskCount("MONDAY");

            //when
            var isTaskAdded = weekdayPlannerService.addTask(baseTask);
            var numTasksAfter = getTaskCount("MONDAY");

            //then
            assertAll(
                    () -> assertThat(numTasksBefore, is(0)),
                    () -> assertTrue(isTaskAdded),
                    () -> assertThat(numTasksAfter, is(1))
            );
        }

        @Test
        void shouldNotAddNewTaskToWeekdayPlannerWhenTaskIsNull() {
            //given
            var numTasksBefore = Arrays.stream(WeekDays.values()).map(weekday -> getTaskCount(weekday.toString())).toArray();

            //when
            var isTaskAdded = weekdayPlannerService.addTask(null);

            var numTasksAfter = Arrays.stream(WeekDays.values()).map(weekday -> getTaskCount(weekday.toString())).toArray();

            //then
            assertAll(
                    () -> Arrays.stream(numTasksBefore).forEach(num -> assertThat(num, is(0))),
                    () -> assertFalse(isTaskAdded),
                    () -> Arrays.stream(numTasksAfter).forEach(num -> assertThat(num, is(0)))
            );
        }

        @Test
        void addedTaskShouldBePlacedCorrectlyInWeekdayPlanner() {
            //given
            var tasks = helper.prepareSingleTasks();

            //when
            for (SingleTask task : tasks) {weekdayPlannerService.addTask(task);}

            var taskPanels = Objects.requireNonNull(getWeekdayPlannerView("MONDAY"))
                    .getContentPanel()
                    .getComponents();

            //then
            assertAll(
                    () -> assertThat(taskPanels, notNullValue()),
                    () -> assertThat(taskPanels.length, is(3)),
                    () -> assertThat(taskPanels[0].getName(), is("09:30_baseTask")),
                    () -> assertThat(taskPanels[1].getName(), is("12:00_baseTask")),
                    () -> assertThat(taskPanels[2].getName(), is("15:45_baseTask"))
            );
        }

        @Tag("withTask")
        @Test
        void shouldNotAddNewTaskToWeekdayPlannerIfAddedTaskExist() {
            //given+when
            weekdayPlannerService.addTask(baseTask);

            var weekdayPlannerView = getWeekdayPlannerView("MONDAY");
            var taskPanel = weekdayPlannerView.createTaskPanel(baseTask);

            //then
            assertAll(
                    () -> assertThat(weekdayPlannerView.getContentPanel().getComponents().length, is(1)),
                    () -> assertThat(weekdayPlannerView.getContentPanel().getComponents()[0].getName(), is(taskPanel.getName()))
            );
        }
    }

    @Nested
    class DeleteTaskTests {
        @Tag("withTask")
        @Test
        void shouldDeleteTaskFromWeekdayPlanner() {
            //given
            var numTasksBeforeDelete = getTaskCount("MONDAY");

            //when
            var isTaskDeleted = weekdayPlannerService.deleteTask(baseTask);
            var numTasksAfterDelete = getTaskCount("MONDAY");

            //then
            assertAll(
                    () -> assertThat(numTasksBeforeDelete, is(1)),
                    () -> assertTrue(isTaskDeleted),
                    () -> assertThat(numTasksAfterDelete, is(0))
            );
        }

        @Tag("withTask")
        @Test
        void shouldNotDeleteTaskIfTaskInWeekdayPlannerViewNotExist() {
            //given
            var task = helper.createBaseTask();
            task.setTime(LocalTime.of(13, 0));

            var numTasksBeforeDelete = getTaskCount("MONDAY");

            //when
            var isTaskDeleted = weekdayPlannerService.deleteTask(task);
            var numTasksAfterDelete = getTaskCount("MONDAY");

            //then
            assertAll(
                    () -> assertThat(numTasksBeforeDelete, is(1)),
                    () -> assertFalse(isTaskDeleted),
                    () -> assertThat(numTasksAfterDelete, is(1))
            );
        }

        @Tag("withTask")
        @Test
        void shouldNotDeleteTaskWhenTaskIsNull() {
            //given
            var numTasksBeforeDelete = getTaskCount("MONDAY");

            //when
            var isTaskDeleted = weekdayPlannerService.deleteTask(null);
            var numTasksAfterDelete = getTaskCount("MONDAY");

            //then
            assertAll(
                    () -> assertThat(numTasksBeforeDelete, is(1)),
                    () -> assertFalse(isTaskDeleted),
                    () -> assertThat(numTasksAfterDelete, is(1))
            );
        }

        @Test
        void tasksShouldBePlacedCorrectlyAfterDeletingTask() {
            //given
            var tasks = helper.prepareSingleTasks();
            for (SingleTask task : tasks) {weekdayPlannerService.addTask(task);}

            //when
            weekdayPlannerService.deleteTask(tasks[0]);

            var weekdayPlannerView = getWeekdayPlannerView("MONDAY");
            var taskPanels = Objects.requireNonNull(weekdayPlannerView).getContentPanel().getComponents();

            //then
            assertAll(
                    () -> assertThat(taskPanels, notNullValue()),
                    () -> assertThat(taskPanels.length, is(2)),
                    () -> assertThat(taskPanels[0].getName(), is("09:30_baseTask")),
                    () -> assertThat(taskPanels[1].getName(), is("15:45_baseTask"))
            );
        }
    }

    @Nested
    class EditTaskTests {
        @Tag("withTask")
        @Test
        void shouldEditValuesInWeekdayPlanner() {
            //given
            var editedTask = helper.createEditedTask();

            var weekdaysBefore = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //when
            var isTaskEdited = weekdayPlannerService.editTask(baseTask, editedTask);

            var weekdaysAfter = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //then
            assertAll(
                    () -> assertThat(weekdaysBefore[0], is(1)),
                    () -> assertThat(weekdaysBefore[1], is(0)),
                    () -> assertTrue(isTaskEdited),
                    () -> assertThat(weekdaysAfter[0], is(0)),
                    () -> assertThat(weekdaysAfter[1], is(1))
            );
            assertThat(
                    getWeekdayPlannerView("TUESDAY").getContentPanel().getComponent(0).getName(),
                    is("%s_%s".formatted(editedTask.getTime(), editedTask.getTaskName()))
            );
        }

        @ParameterizedTest
        @MethodSource("prepareNullableValues")
        void shouldNotEditTaskIfParamsAreNull(SingleTask task, SingleTask editedTask) {
            //given
            if (task != null) weekdayPlannerService.addTask(task);

            var weekdaysBefore = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //when
            var isTaskEdited = weekdayPlannerService.editTask(task, editedTask);

            var weekdaysAfter = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //then
            if (task != null) {
                assertThat(
                        getWeekdayPlannerView("MONDAY").getContentPanel().getComponent(0).getName(),
                        is("%s_%s".formatted(task.getTime(), task.getTaskName()))
                );
            }
            assertAll(
                    () -> assertThat(weekdaysBefore[0], is(task == null ? 0 : 1)),
                    () -> assertThat(weekdaysBefore[1], is(0)),
                    () -> assertFalse(isTaskEdited),
                    () -> assertThat(weekdaysAfter[0], is(task == null ? 0 : 1)),
                    () -> assertThat(weekdaysAfter[1], is(0))
            );
        }

        @Tag("withTask")
        @Test
        void shouldNotEditValuesIfOldTaskDoesNotExist() {
            //given
            var task = helper.createBaseTask();
            task.setTime(LocalTime.of(14, 0));

            var weekdaysBefore = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //when
            var isTaskEdited = weekdayPlannerService.editTask(task, helper.createEditedTask());

            var weekdaysAfter = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //then
            assertAll(
                    () -> assertThat(weekdaysBefore[0], is(1)),
                    () -> assertThat(weekdaysBefore[1], is(0)),
                    () -> assertFalse(isTaskEdited),
                    () -> assertThat(weekdaysAfter[0], is(1)),
                    () -> assertThat(weekdaysAfter[1], is(0))
            );
            assertThat(
                    getWeekdayPlannerView("MONDAY").getContentPanel().getComponent(0).getName(),
                    is("%s_%s".formatted(baseTask.getTime(), baseTask.getTaskName()))
            );
        }

        @Tag("withTask")
        @Test
        void shouldNotEditValuesIfEditedVersionExist() {
            //given
            var editedTask = helper.createEditedTask();
            weekdayPlannerService.addTask(editedTask);

            var weekdaysBefore = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //when
            var isTaskEdited = weekdayPlannerService.editTask(baseTask, editedTask);

            var weekdaysAfter = weekdays.stream().map(WeekdayPlannerServiceTest.this::getTaskCount).toArray();

            //then
            assertAll(
                    () -> assertThat(weekdaysBefore[0], is(1)),
                    () -> assertThat(weekdaysBefore[1], is(1)),
                    () -> assertFalse(isTaskEdited),
                    () -> assertThat(weekdaysAfter[0], is(1)),
                    () -> assertThat(weekdaysAfter[1], is(1))
            );
            assertThat(
                    getWeekdayPlannerView("MONDAY").getContentPanel().getComponent(0).getName(),
                    is("%s_%s".formatted(baseTask.getTime(), baseTask.getTaskName()))
            );
            assertThat(
                    getWeekdayPlannerView("TUESDAY").getContentPanel().getComponent(0).getName(),
                    is("%s_%s".formatted(editedTask.getTime(), editedTask.getTaskName()))
            );
        }

        @Tag("withTask")
        @Test
        void shouldNotEditValuesInWeekdayPlannerIfValuesInOldTaskAreSameAsInEditedVersion() {
            //given
            var editedTask = helper.createBaseTask();

            var mondayBeforeEdit = getTaskCount("MONDAY");

            //when
            var isTaskEdited = weekdayPlannerService.editTask(baseTask, editedTask);

            var mondayAfterEdit = getTaskCount("MONDAY");

            //then
            assertAll(
                    () -> assertThat(mondayBeforeEdit, is(1)),
                    () -> assertFalse(isTaskEdited),
                    () -> assertThat(mondayAfterEdit, is(1))
            );
        }

        @NotNull
        private static Stream<Arguments> prepareNullableValues() {
            return Stream.of(
                    Arguments.of(helper.createBaseTask(), null),
                    Arguments.of(null, helper.createEditedTask()),
                    Arguments.of(null, null)
            );
        }
    }

    @Nested
    class FinderTaskTests_OneParam {
        @Test
        @Tag("withTask")
        void shouldReturnTrueIfTaskIsAddedToWeekdayPlanner() {
            assertTrue(weekdayPlannerService.isTaskInWeekdayPlanner(baseTask));
        }

        @Test
        void shouldReturnFalseIfTaskIsNotAddedToWeekdayPlanner() {
            assertFalse(weekdayPlannerService.isTaskInWeekdayPlanner(baseTask));
        }

        @Test
        void shouldReturnFalseIfTaskIsNullable() {
            assertFalse(weekdayPlannerService.isTaskInWeekdayPlanner(null));
        }
    }

    @Nested
    class FinderTaskTests_TwoParams {
        @Tag("withTask") @Test
        void shouldReturnFalseIfViewIsNullable() {
            //given + when
            var actualResult = weekdayPlannerService.isTaskInWeekdayPlanner(null, baseTask);

            //then
            assertFalse(actualResult);
        }

        @Test
        void shouldReturnFalseIfTaskIsNullable() {
            //given
            var views = WeekDays.getListedWeekdays().stream()
                    .map(weekday -> getWeekdayPlannerView(weekday.toString()))
                    .toList();

            //when
            var actualResult = views.stream()
                    .anyMatch(view -> weekdayPlannerService.isTaskInWeekdayPlanner(view, null));

            //then
            assertFalse(actualResult);
        }

        @Test
        void shouldReturnFalseIfViewAndTaskAreNullable() {
            //given + when
            var actualResult = weekdayPlannerService.isTaskInWeekdayPlanner(null, null);

            //then
            assertFalse(actualResult);
        }

        @Test
        void shouldReturnFalseIfTaskIsNotAddedToTheGivenWeekdayPlanner() {
            //given
            var view = getWeekdayPlannerView(baseTask.getWeekday().toString());

            //when
            var actualResult = weekdayPlannerService.isTaskInWeekdayPlanner(view, baseTask);

            //then
            assertFalse(actualResult);
        }

        @Tag("withTask") @Test
        void shouldReturnTrueIfParamsAreNotNullAndTaskIsAddedToWeekdayPlanner() {
            //given
            var view = getWeekdayPlannerView(baseTask.getWeekday().toString());

            //when
            var actualResult = weekdayPlannerService.isTaskInWeekdayPlanner(view, baseTask);

            //then
            assertTrue(actualResult);
        }
    }

    //region helpful methods
    private WeekdayPlannerView getWeekdayPlannerView(String weekdayName) {
        return (WeekdayPlannerView) Arrays.stream(MainFrame.getMAIN_FRAME().getCardPanel().getComponents())
                .filter(comp -> comp.getName().contains(weekdayName))
                .findFirst()
                .orElse(null);
    }

    private int getTaskCount(String weekday) {
        return Objects.requireNonNull(getWeekdayPlannerView(weekday))
                .getContentPanel()
                .getComponents()
                .length;
    }
    //endregion
}