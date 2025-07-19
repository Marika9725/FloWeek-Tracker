package com.floweektracker.controller;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.service.*;
import com.floweektracker.view.TaskAddingDialog;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import javax.swing.table.DefaultTableModel;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class TaskAddingDialogControllerTest {
    //region fields
    private static final TestHelper helper = new TestHelper();
    private static SingleTask task = helper.createBaseTask();
    private static final WeekdayPlannerService weekdayPlannerService = WeekdayPlannerService.getService();
    private static final PlannerService plannerService = PlannerService.getService();
    private static final Map<WeekDays, Map<LocalTime, SingleTask>> schedule = TasksService.getService().getSchedule();
    private TaskAddingDialog taskAddingDialog;
    private TaskAddingDialogController controller;
    //endregion

    //region setUp and tearDown
    @BeforeEach
    void initialize(TestInfo testInfo) {
        this.taskAddingDialog = new TaskAddingDialog();
        task = helper.createBaseTask();

        if (!testInfo.getTags().contains("noTaskAddingDialog")) {
            this.controller = new TaskAddingDialogController(taskAddingDialog);
        }
    }

    @AfterEach
    void cleanUp() {
        //clean taskNames
        schedule.values().forEach(Map::clear);

        //clean plannerView
        var plannerView = plannerService.getPlanner();
        for (int i = 1; i < plannerView.getRowCount() - 1; i++) {
            ((DefaultTableModel) plannerView.getModel()).removeRow(i);
        }

        //clean weekdayPlanner
        var weekdayPlanner = weekdayPlannerService.getWeekdayPlannerView("MONDAY");
        for (int i = 0; i < weekdayPlanner.getContentPanel().getComponents().length; i++) {
            weekdayPlanner.getContentPanel().remove(i);
        }

        //clean controller
        this.controller = null;
        this.taskAddingDialog = null;
    }
    //endregion

    @Nested
    class ConstructorTests {
        @Test
        void shouldThrowExceptionIfTaskAddingDialogIsNull() {
            assertThrows(IllegalArgumentException.class, () -> new TaskAddingDialogController(null));
        }

        @Tag("noTaskAddingDialog")
        @ParameterizedTest
        @ValueSource(strings = {"confirmButton", "cancelButton"})
        void constructorShouldAddListenersToTheButtonsFromTaskAddingDialog(String buttonName) {
            //given
            var numListenersBefore = countNumListenersInButton(taskAddingDialog, buttonName);

            //when
            var customController = new TaskAddingDialogController(taskAddingDialog);
            var numListenersAfter = countNumListenersInButton(customController.getTaskAddingDialog(), buttonName);

            //then
            assertAll(
                    () -> assertThat(numListenersBefore, is(0)),
                    () -> assertThat(numListenersAfter, is(1))
            );
        }

        @NotNull
        private Integer countNumListenersInButton(TaskAddingDialog taskAddingDialog, String buttonName) {
            return taskAddingDialog.getButtons().stream()
                    .filter(b -> b.getName().equals(buttonName))
                    .findFirst()
                    .map(button -> button.getActionListeners().length)
                    .orElse(-1);
        }
    }

    @Nested
    class AddTaskTests {
        @Test
        void shouldAddTaskToSchedule() {
            //given
            var isTaskBeforeInSchedule = schedule.get(task.getWeekday()).containsValue(task);

            //when
            var isNewTaskAdded = controller.addTask(task);
            var isTaskAfterInSchedule = schedule.get(task.getWeekday()).containsValue(task);

            //then
            assertAll(
                    () -> assertFalse(isTaskBeforeInSchedule),
                    () -> assertTrue(isNewTaskAdded),
                    () -> assertTrue(isTaskAfterInSchedule)
            );
        }

        @Test
        void newTaskShouldBeAddedToPlanner() {
            //given
            var isTaskBeforeInPlanner = plannerService.isTaskInPlanner(task);

            //when
            var isNewTaskAdded = controller.addTask(task);
            var isTaskAfterInPlanner = plannerService.isTaskInPlanner(task);

            //then
            assertAll(
                    () -> assertFalse(isTaskBeforeInPlanner),
                    () -> assertTrue(isNewTaskAdded),
                    () -> assertTrue(isTaskAfterInPlanner)
            );
        }

        @Test
        void newTaskShouldBeAddedToWeekdayPlanner() {
            //given
            var mondayPlanner = weekdayPlannerService.getWeekdayPlannerView("MONDAY");
            var numTasksBeforeInWeekdayPlanner = mondayPlanner.getContentPanel().getComponents().length;

            //when
            var isNewTaskAdded = controller.addTask(task);
            var numTasksAfterInWeekdayPlanner = mondayPlanner.getContentPanel().getComponents().length;

            //then
            assertAll(
                    () -> assertThat(numTasksBeforeInWeekdayPlanner, is(0)),
                    () -> assertTrue(isNewTaskAdded),
                    () -> assertThat(numTasksAfterInWeekdayPlanner, is(1))
            );
        }

        @Test
        void nullTaskShouldNotBeAddedToSchedule() {
            //given
            var isScheduleNullBefore = schedule.values().stream().allMatch(Map::isEmpty);

            //when
            var isNullTaskAdded = controller.addTask(null);
            var isScheduleNullAfter = schedule.values().stream().allMatch(Map::isEmpty);

            //then
            assertAll(
                    () -> assertTrue(isScheduleNullBefore),
                    () -> assertFalse(isNullTaskAdded),
                    () -> assertTrue(isScheduleNullAfter)
            );
        }

        @Test
        void nullTaskShouldNotBeAddedToPlanner() {
            //given
            var numRowsInPlannerBefore = plannerService.getPlanner().getModel().getRowCount();

            //when
            var isNullTaskAdded = controller.addTask(null);

            var numRowsInPlannerAfter = plannerService.getPlanner().getModel().getRowCount();

            //then
            assertAll(
                    () -> assertThat(numRowsInPlannerBefore, is(2)),
                    () -> assertFalse(isNullTaskAdded),
                    () -> assertThat(numRowsInPlannerAfter, is(2))
            );
        }

        @Test
        void nullTaskShouldNotBeAddedToWeekdayPlanner() {
            //given
            var weekdays = WeekDays.values();
            var numTasksBeforeInWeekdayPlanners = countNumTasksInWeekdayPlanners(weekdays);

            //when
            var isNullTaskAdded = controller.addTask(null);

            var numTasksAfterInWeekdayPlanners = countNumTasksInWeekdayPlanners(weekdays);

            //then
            assertAll(
                    () -> numTasksBeforeInWeekdayPlanners.forEach(num -> assertThat(num, is(0))),
                    () -> assertFalse(isNullTaskAdded),
                    () -> numTasksAfterInWeekdayPlanners.forEach(num -> assertThat(num, is(0)))
            );
        }

        @Test
        void shouldReturnFalseWhenAddingTaskToTasksServiceFails() {
            //given
            var tasksService = TasksService.getService();
            tasksService.addTask(task);
            var isTaskInScheduleBefore = tasksService.isTaskInSchedule(task);

            //when
            var isTaskAdded = controller.addTask(task);
            var isTaskInScheduleAfter = tasksService.isTaskInSchedule(task);

            //then
            assertAll(
                    () -> assertTrue(isTaskInScheduleBefore),
                    () -> assertFalse(isTaskAdded),
                    () -> assertTrue(isTaskInScheduleAfter)
            );
        }

        @Test
        void shouldReturnFalseWhenAddingTaskToPlannerServiceFails(){
            //given
            var plannerService = PlannerService.getService();
            plannerService.addTask(task);
            var isTaskInPlannerBefore = plannerService.isTaskInPlanner(task);

            //when
            var isTaskAdded = controller.addTask(task);
            var isTaskInPlannerAfter = plannerService.isTaskInPlanner(task);

            //then
            assertAll(
                    () -> assertTrue(isTaskInPlannerBefore),
                    () -> assertFalse(isTaskAdded),
                    () -> assertTrue(isTaskInPlannerAfter)
            );
        }
    }

    @Nested
    class ConfirmButtonTests {
        @Test
        void confirmButtonFromTaskAddingDialogShouldAddTaskToTheApp() {
            //given
            addTaskToComboBoxInTaskAddingDialog(taskAddingDialog);
            var preparedTask = prepareTask(taskAddingDialog);

            var isTaskInScheduleBefore = schedule.get(preparedTask.getWeekday()).containsKey(preparedTask.getTime());
            var isTaskInPlannerBefore = plannerService.isTaskInPlanner(preparedTask);
            var numTasksInWeekdayPlannerBefore = countNumTasksInWeekdayPlanners(new WeekDays[]{preparedTask.getWeekday()});

            //when
            taskAddingDialog.getButtons().getFirst().doClick();
            var isTaskInScheduleAfter = schedule.get(preparedTask.getWeekday()).containsKey(preparedTask.getTime());
            var isTaskInPlannerAfter = plannerService.isTaskInPlanner(preparedTask);
            var numTasksInWeekdayPlannerAfter = countNumTasksInWeekdayPlanners(new WeekDays[]{preparedTask.getWeekday()});

            //then
            assertAll(
                    () -> assertFalse(isTaskInScheduleBefore),
                    () -> assertTrue(isTaskInScheduleAfter),
                    () -> assertFalse(isTaskInPlannerBefore),
                    () -> assertTrue(isTaskInPlannerAfter),
                    () -> assertThat(numTasksInWeekdayPlannerBefore.getFirst(), is(0)),
                    () -> assertThat(numTasksInWeekdayPlannerAfter.getFirst(), is(1))
            );
        }

        @Test
        void confirmButtonShouldCloseTaskAddingDialog() {
            //given
            var taskAddingDialog = spy(new TaskAddingDialog());
            var controller = spy(new TaskAddingDialogController(taskAddingDialog));
            var confirmButton = controller.getTaskAddingDialog().getButtons().getFirst();
            addTaskToComboBoxInTaskAddingDialog(controller.getTaskAddingDialog());
            confirmButton.removeActionListener(Arrays.stream(confirmButton.getActionListeners()).findFirst().orElse(null));
            controller.initializeListeners();

            //when
            confirmButton.doClick();

            //then
            assertAll(
                    () -> then(taskAddingDialog).should().makeDialogInvisible(),
                    () -> verify(controller).addTask(any(SingleTask.class))
            );
        }
    }

    @Nested
    class CancelButtonTests {
        @Test
        void cancelButtonFromTaskAddingDialogShouldNotAddTaskToTheApp() {
            //given
            addTaskToComboBoxInTaskAddingDialog(taskAddingDialog);
            var preparedTask = prepareTask(taskAddingDialog);

            var isTaskInScheduleBefore = schedule.get(preparedTask.getWeekday()).containsKey(preparedTask.getTime());
            var isTaskInPlannerBefore = plannerService.isTaskInPlanner(preparedTask);
            var numTasksInWeekdayPlannerBefore = countNumTasksInWeekdayPlanners(new WeekDays[]{preparedTask.getWeekday()});

            //when
            taskAddingDialog.getButtons().getLast().doClick();
            var isTaskInScheduleAfter = schedule.get(preparedTask.getWeekday()).containsKey(preparedTask.getTime());
            var isTaskInPlannerAfter = plannerService.isTaskInPlanner(preparedTask);
            var numTasksInWeekdayPlannerAfter = countNumTasksInWeekdayPlanners(new WeekDays[]{preparedTask.getWeekday()});

            //then
            assertAll(
                    () -> assertFalse(isTaskInScheduleBefore),
                    () -> assertFalse(isTaskInScheduleAfter),
                    () -> assertFalse(isTaskInPlannerBefore),
                    () -> assertFalse(isTaskInPlannerAfter),
                    () -> assertThat(numTasksInWeekdayPlannerBefore.getFirst(), is(0)),
                    () -> assertThat(numTasksInWeekdayPlannerAfter.getFirst(), is(0))
            );
        }

        @Test
        void cancelButtonShouldCloseTaskAddingDialog() {
            //given
            var taskAddingDialog = spy(TaskAddingDialog.class);
            var controller = spy(new TaskAddingDialogController(taskAddingDialog));
            var cancelButton = controller.getTaskAddingDialog().getButtons().getLast();
            cancelButton.removeActionListener(Arrays.stream(cancelButton.getActionListeners()).findFirst().orElse(null));
            controller.initializeListeners();

            //when
            cancelButton.doClick();

            //then
            assertAll(
                    () -> verify(taskAddingDialog).makeDialogInvisible(),
                    () -> verify(controller, never()).addTask(any(SingleTask.class))
            );
        }
    }

    @Nested
    class UpdateTaskNamesTests {
        @ParameterizedTest
        @ValueSource(strings = {" ", "", "null"})
        void taskNameShouldNotBeValid(String taskName) {
            //given
            if (taskName.equals("null")) taskName = null;

            //then
            var isTaskUpdated = controller.updateTaskNames(taskName);

            //then
            assertFalse(isTaskUpdated);
        }

        @Test
        void shouldAddTaskNameToTaskNamesComboBoxWhenTaskNameIsNew() {
            //given
            var isTaskNameInComboBoxBefore = taskAddingDialog.getAllTaskNames().contains("taskName");

            //when
            var isTaskNameUpdated = controller.updateTaskNames("taskName");
            var isTaskNameInComboBoxAfter = taskAddingDialog.getAllTaskNames().contains("taskName");

            //then
            assertAll(
                    () -> assertFalse(isTaskNameInComboBoxBefore),
                    () -> assertTrue(isTaskNameUpdated),
                    () -> assertTrue(isTaskNameInComboBoxAfter)
            );
        }

        @Test
        void shouldRemoveTaskNameFromTaskNamesComboBoxWhenTaskNameIsOld() {
            //given
            taskAddingDialog.getTasksComboBox().addItem("taskName");
            var isTaskNameInComboBoxBefore = taskAddingDialog.getAllTaskNames().contains("taskName");

            //when
            var isTaskNameUpdated = controller.updateTaskNames("taskName");
            var isTaskNameInComboBoxAfter = taskAddingDialog.getAllTaskNames().contains("taskName");

            //then
            assertAll(
                    () -> assertTrue(isTaskNameInComboBoxBefore),
                    () -> assertTrue(isTaskNameUpdated),
                    () -> assertFalse(isTaskNameInComboBoxAfter)
            );
        }

        @Test
        void updatedTaskNamesComboBoxShouldBeAlphabetical() {
            //given
            var expectedTaskNames = new ArrayList<>(List.of("newTask", "taskName"));
            var taskNamesComboBox = taskAddingDialog.getTasksComboBox();
            taskNamesComboBox.addItem("taskName");

            //when
            var isTaskUpdated = controller.updateTaskNames("newTask");
            var actualTaskNames = taskAddingDialog.getAllTaskNames();

            //then
            assertTrue(isTaskUpdated);
            assertThat(actualTaskNames, is(expectedTaskNames));
        }
    }

    //region helper methods
    @NotNull
    private List<Integer> countNumTasksInWeekdayPlanners(WeekDays[] weekdays) {
        return Arrays.stream(weekdays)
                .map(weekday -> weekdayPlannerService.getWeekdayPlannerView(weekday.name()))
                .map(weekdayPlanner -> weekdayPlanner.getContentPanel().getComponents().length)
                .toList();
    }

    private void addTaskToComboBoxInTaskAddingDialog(TaskAddingDialog taskAddingDialog) {
        taskAddingDialog.getTasksComboBox().addItem("task1");
        taskAddingDialog.getTasksComboBox().setSelectedItem("task1");
    }

    @NotNull
    private static SingleTask prepareTask(TaskAddingDialog taskAddingDialog) {
        return new SingleTask(
                taskAddingDialog.getTaskName(),
                taskAddingDialog.getDescription(),
                taskAddingDialog.getTime(),
                false,
                taskAddingDialog.getWeekday(),
                taskAddingDialog.getPriority()
        );
    }
    //endregion
}