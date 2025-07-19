package com.floweektracker.controller;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.service.*;
import com.floweektracker.view.*;
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
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class TaskEditingDialogControllerTest {
    //region fields
    private static final TestHelper helper = new TestHelper();
    private static SingleTask task = helper.createBaseTask();
    private static final SingleTask editedTask = helper.createEditedTask();
    private static final WeekdayPlannerService weekdayPlannerService = WeekdayPlannerService.getService();
    private static final TasksService tasksService = TasksService.getService();
    private static final PlannerService plannerService = PlannerService.getService();
    private static final Map<WeekDays, Map<LocalTime, SingleTask>> schedule = tasksService.getSchedule();
    private TaskEditingDialog taskEditingDialog;
    private TaskEditingDialogController controller;
    //endregion

    //region setup & cleanup
    @BeforeEach
    void setUp(TestInfo testInfo) {
        this.taskEditingDialog = new TaskEditingDialog();
        this.taskEditingDialog.getTasksComboBox().removeAllItems();
        task = helper.createBaseTask();

        if (!testInfo.getTags().contains("noTaskEditingDialog")) {
            controller = new TaskEditingDialogController(taskEditingDialog);
        }

        if (testInfo.getTags().contains("editTaskCorrect")) {
            tasksService.addTask(task);
            plannerService.addTask(task);
            weekdayPlannerService.addTask(task);
        }
    }

    @AfterEach
    void cleanUp() {
        //clean plannerView
        var plannerView = plannerService.getPlanner();
        plannerView.revalidate();
        plannerView.repaint();

        var tasksList = schedule.values().stream()
                .flatMap(weekday -> weekday.values().stream())
                .toList();

        tasksList.forEach(plannerService::deleteTask);

        //clean weekdayPlanner
        for (WeekDays weekday : WeekDays.values()) {
            var weekdayPlanner = weekdayPlannerService.getWeekdayPlannerView(weekday.name()).getContentPanel();

            while (weekdayPlanner.getComponents().length > 0) {
                weekdayPlanner.remove(0);
            }
        }

        //clean taskNames
        schedule.values().forEach(Map::clear);

        //clean controller
        controller = null;
        this.taskEditingDialog = null;

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
    class ConstructorTests {
        @Test
        void shouldThrowsIllegalArgumentExceptionIfTaskEditingDialogIsNull() {
            assertThrows(IllegalArgumentException.class, () -> new TaskEditingDialogController(null));
        }

        @Tag("noTaskEditingDialog")
        @ParameterizedTest
        @ValueSource(strings = {"confirmButton", "cancelButton"})
        void constructorShouldAddListenersToTheButtonsFromTaskEditingDialog(String buttonName) {
            //given
            var numListenersBefore = countNumListenersInButton(taskEditingDialog, buttonName);

            //when
            var customController = new TaskEditingDialogController(taskEditingDialog);
            var numListenersAfter = countNumListenersInButton(customController.getTaskEditingDialog(), buttonName);

            //then
            assertAll(
                    () -> assertThat(numListenersBefore, is(0)),
                    () -> assertThat(numListenersAfter, is(1))
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"confirmButton", "cancelButton"})
        void shouldNotAddListenersToTheButtonsWhenTheyAreAlreadyAdded(String buttonName) {
            //given
            var numListenersBefore = countNumListenersInButton(taskEditingDialog, buttonName);

            //when
            var customController = new TaskEditingDialogController(taskEditingDialog);
            var numListenersAfter = countNumListenersInButton(customController.getTaskEditingDialog(), buttonName);

            //then
            assertAll(
                    () -> assertThat(numListenersBefore, is(1)),
                    () -> assertThat(numListenersAfter, is(1))
            );
        }

        @NotNull
        private Integer countNumListenersInButton(TaskEditingDialog taskEditingDialog, String buttonName) {
            return taskEditingDialog.getButtons().stream()
                    .filter(b -> b.getName().equals(buttonName))
                    .findFirst()
                    .map(button -> button.getActionListeners().length)
                    .orElse(-1);
        }
    }

    @Nested
    class EditTaskTests {
        @Tag("editTaskCorrect")
        @Test
        void taskShouldBeEditedInSchedule() {
            //given
            var isTaskBeforeInSchedule = schedule.get(task.getWeekday()).containsValue(task);
            var isEditedTaskBeforeInSchedule = schedule.get(editedTask.getWeekday()).containsValue(editedTask);

            //when
            var isTaskEdited = controller.editTask(task, editedTask);
            var isTaskAfterInSchedule = schedule.get(task.getWeekday()).containsValue(task);
            var isEditedTaskAfterInSchedule = schedule.get(editedTask.getWeekday()).containsValue(editedTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskBeforeInSchedule),
                    () -> assertFalse(isEditedTaskBeforeInSchedule),
                    () -> assertTrue(isTaskEdited),
                    () -> assertFalse(isTaskAfterInSchedule),
                    () -> assertTrue(isEditedTaskAfterInSchedule)
            );
        }

        @Tag("editTaskCorrect")
        @Test
        void taskShouldBeEditedInPlanner() {
            //given
            var isTaskBeforeInPlanner = plannerService.isTaskInPlanner(task);
            var isEditedTaskBeforeInPlanner = plannerService.isTaskInPlanner(editedTask);

            //when
            var isTaskEdited = controller.editTask(task, editedTask);

            var isTaskAfterInPlanner = plannerService.isTaskInPlanner(task);
            var isEditedTaskAfterInPlanner = plannerService.isTaskInPlanner(editedTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskBeforeInPlanner),
                    () -> assertFalse(isEditedTaskBeforeInPlanner),
                    () -> assertTrue(isTaskEdited),
                    () -> assertFalse(isTaskAfterInPlanner),
                    () -> assertTrue(isEditedTaskAfterInPlanner)
            );
        }

        @Tag("editTaskCorrect")
        @Test
        void taskShouldBeEditedInWeekdayPlanner() {
            //given
            var weekdayPlannerTask = weekdayPlannerService.getWeekdayPlannerView(task.getWeekday().name());
            var weekdayPlannerEditedTask = weekdayPlannerService.getWeekdayPlannerView(editedTask.getWeekday().name());

            var isTaskBeforeInWeekdayPlanner = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerTask, task);
            var isEditedTaskBeforeInWeekdayPlanner = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerEditedTask, editedTask);

            //when
            var isTaskEdited = controller.editTask(task, editedTask);

            var isTaskAfterInWeekdayPlanner = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerTask, task);
            var isEditedTaskAfterInWeekdayPlanner = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerEditedTask, editedTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskBeforeInWeekdayPlanner),
                    () -> assertFalse(isEditedTaskBeforeInWeekdayPlanner),
                    () -> assertTrue(isTaskEdited),
                    () -> assertFalse(isTaskAfterInWeekdayPlanner),
                    () -> assertTrue(isEditedTaskAfterInWeekdayPlanner)
            );
        }

        @Tag("editTaskCorrect")
        @ParameterizedTest
        @MethodSource("createNullableTasksToEditTaskMethod")
        void shouldNotEditTaskInScheduleIfParamsAreNullable(SingleTask originalTask, SingleTask editedTask) {
            //given
            var isTaskBeforeInSchedule = (originalTask != null) && tasksService.isTaskInSchedule(originalTask);
            var isEditedTaskBeforeInSchedule = (editedTask != null) && tasksService.isTaskInSchedule(editedTask);

            //when
            var isTaskEdited = controller.editTask(originalTask, editedTask);
            var isTaskAfterInSchedule = (originalTask != null) && tasksService.isTaskInSchedule(originalTask);
            var isEditedTaskAfterInSchedule = (editedTask != null) && tasksService.isTaskInSchedule(editedTask);

            //then
            if (originalTask != null) {
                assertTrue(isTaskBeforeInSchedule);
                assertTrue(isTaskAfterInSchedule);
            } else {
                assertFalse(isTaskBeforeInSchedule);
                assertFalse(isTaskAfterInSchedule);
            }
            assertFalse(isTaskEdited);
            assertFalse(isEditedTaskBeforeInSchedule);
            assertFalse(isEditedTaskAfterInSchedule);
        }

        @Tag("editTaskCorrect")
        @ParameterizedTest
        @MethodSource("createNullableTasksToEditTaskMethod")
        void shouldNotEditTaskInPlannerIfParamsAreNullable(SingleTask originalTask, SingleTask editedTask) {
            //given
            var isTaskBeforeInPlanner = (originalTask != null) && plannerService.isTaskInPlanner(originalTask);
            var isEditedTaskBeforeInPlanner = (editedTask != null) && plannerService.isTaskInPlanner(editedTask);

            //when
            var isTaskEdited = controller.editTask(originalTask, editedTask);
            var isTaskAfterInPlanner = (originalTask != null) && plannerService.isTaskInPlanner(originalTask);
            var isEditedTaskAfterInPlanner = (editedTask != null) && plannerService.isTaskInPlanner(editedTask);

            //then
            if (originalTask != null) {
                assertTrue(isTaskBeforeInPlanner);
                assertTrue(isTaskAfterInPlanner);
            } else {
                assertFalse(isTaskBeforeInPlanner);
                assertFalse(isTaskAfterInPlanner);
            }
            assertFalse(isTaskEdited);
            assertFalse(isEditedTaskBeforeInPlanner);
            assertFalse(isEditedTaskAfterInPlanner);
        }

        @Tag("editTaskCorrect")
        @ParameterizedTest
        @MethodSource("createNullableTasksToEditTaskMethod")
        void shouldNotEditTaskInWeekdayPlannerIfParamsAreNullable(SingleTask originalTask, SingleTask editedTask) {
            //given
            var isTaskBeforeInWeekdayPlanner = (originalTask != null) && (weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(originalTask.getWeekday().name()), originalTask));
            var isEditedTaskBeforeInWeekdayPlanner = (editedTask != null) && (weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(editedTask.getWeekday().name()), editedTask));

            //when
            var isTaskEdited = controller.editTask(originalTask, editedTask);

            var isTaskAfterInWeekdayPlanner = (originalTask != null) && (weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(originalTask.getWeekday().name()), originalTask));
            var isEditedTaskAfterInWeekdayPlanner = (editedTask != null) && (weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(editedTask.getWeekday().name()), editedTask));

            //then
            if (originalTask != null) {
                assertTrue(isTaskBeforeInWeekdayPlanner);
                assertTrue(isTaskAfterInWeekdayPlanner);
            } else {
                assertFalse(isTaskBeforeInWeekdayPlanner);
                assertFalse(isTaskAfterInWeekdayPlanner);
            }
            assertFalse(isTaskEdited);
            assertFalse(isEditedTaskBeforeInWeekdayPlanner);
            assertFalse(isEditedTaskAfterInWeekdayPlanner);
        }

        @Test
        void shouldReturnFalseWhenEditingTaskInWeekdayPlannerServiceFails() {
            //given
            tasksService.addTask(task);
            var isTaskInScheduleBefore = tasksService.isTaskInSchedule(task);
            var isEditedTaskInScheduleBefore = tasksService.isTaskInSchedule(editedTask);

            //when
            var isTaskEdited = controller.editTask(task, editedTask);
            var isTaskInScheduleAfter = tasksService.isTaskInSchedule(task);
            var isEditedTaskInScheduleAfter= tasksService.isTaskInSchedule(editedTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskInScheduleBefore),
                    () -> assertFalse(isEditedTaskInScheduleBefore),
                    () -> assertFalse(isTaskEdited),
                    () -> assertTrue(isTaskInScheduleAfter),
                    () -> assertFalse(isEditedTaskInScheduleAfter)
            );
        }

        @Test
        void shouldReturnFalseWhenEditingTaskInPlannerServiceFails(){
            //given
            tasksService.addTask(task);
            weekdayPlannerService.addTask(task);

            //when
            var isTaskEdited = controller.editTask(task, editedTask);

            //then
            assertAll(
                    () -> assertFalse(isTaskEdited),
                    () -> assertTrue(tasksService.isTaskInSchedule(task)),
                    () -> assertFalse(tasksService.isTaskInSchedule(editedTask)),
                    () -> assertTrue(weekdayPlannerService.isTaskInWeekdayPlanner(task)),
                    () -> assertFalse(weekdayPlannerService.isTaskInWeekdayPlanner(editedTask)),
                    () -> assertFalse(plannerService.isTaskInPlanner(task)),
                    () -> assertFalse(plannerService.isTaskInPlanner(editedTask))
            );
        }

        private static Stream<Arguments> createNullableTasksToEditTaskMethod() {
            return Stream.of(
                    Arguments.of(null, null),
                    Arguments.of(null, editedTask),
                    Arguments.of(task, null)
            );
        }
    }

    @Nested
    class ConfirmButtonTests {
        @Tag("editTaskCorrect")
        @Test
        void confirmButtonShouldTransmitTasksToTasksService() {
            //given
            addTaskToComboBoxInTaskEditingDialog(taskEditingDialog);
            taskEditingDialog.makeDialogVisible(task);

            var originalTask = prepareTask(taskEditingDialog);

            var isTaskInScheduleBefore = tasksService.isTaskInSchedule(originalTask);
            var isTaskInPlannerBefore = plannerService.isTaskInPlanner(originalTask);
            var isTaskInWeekdayPlannerBefore = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(originalTask.getWeekday().name()), originalTask);
            var isEditedTaskInScheduleBefore = schedule.get(editedTask.getWeekday()).containsKey(editedTask.getTime());
            var isEditedTaskInPlannerBefore = plannerService.isTaskInPlanner(editedTask);
            var isEditedTaskInWeekdayPlannerBefore = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(editedTask.getWeekday().name()), editedTask);

            //when
            changeDataInTaskEditingDialog(taskEditingDialog, editedTask);
            taskEditingDialog.getButtons().getFirst().doClick();

            var isTaskInScheduleAfter = tasksService.isTaskInSchedule(originalTask);
            var isTaskInPlannerAfter = plannerService.isTaskInPlanner(originalTask);
            var isTaskInWeekdayPlannerAfter = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(originalTask.getWeekday().name()), originalTask);
            var isEditedTaskInScheduleAfter = schedule.get(editedTask.getWeekday()).containsKey(editedTask.getTime());
            var isEditedTaskInPlannerAfter = plannerService.isTaskInPlanner(editedTask);
            var isEditedTaskInWeekdayPlannerAfter = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(editedTask.getWeekday().name()), editedTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskInScheduleBefore),
                    () -> assertTrue(isTaskInPlannerBefore),
                    () -> assertTrue(isTaskInWeekdayPlannerBefore),
                    () -> assertFalse(isEditedTaskInScheduleBefore),
                    () -> assertFalse(isEditedTaskInPlannerBefore),
                    () -> assertFalse(isEditedTaskInWeekdayPlannerBefore),
                    () -> assertFalse(isTaskInScheduleAfter),
                    () -> assertFalse(isTaskInPlannerAfter),
                    () -> assertFalse(isTaskInWeekdayPlannerAfter),
                    () -> assertTrue(isEditedTaskInScheduleAfter),
                    () -> assertTrue(isEditedTaskInPlannerAfter),
                    () -> assertTrue(isEditedTaskInWeekdayPlannerAfter)
            );
        }

        @Test
        void confirmButtonShouldCloseTaskEditingDialogAfterEditTaskInSchedule() {
            var taskEditingDialog = spy(new TaskEditingDialog());
            var controller = spy(new TaskEditingDialogController(taskEditingDialog));
            var confirmButton = controller.getTaskEditingDialog().getButtons().getFirst();
            addTaskToComboBoxInTaskEditingDialog(controller.getTaskEditingDialog());
            Arrays.stream(confirmButton.getActionListeners()).findFirst().ifPresent(confirmButton::removeActionListener);
            controller.initializeListeners();

            //when
            taskEditingDialog.makeDialogVisible(task);
            changeDataInTaskEditingDialog(taskEditingDialog, editedTask);
            confirmButton.doClick();

            //then
            assertAll(
                    () -> then(taskEditingDialog).should().makeDialogInvisible(),
                    () -> verify(controller).editTask(any(SingleTask.class), any(SingleTask.class))
            );
        }
    }

    @Nested
    class CancelButtonTests {

        @Tag("editTaskCorrect")
        @Test
        void cancelButtonShouldNotEditTaskInSchedule() {
            //given
            addTaskToComboBoxInTaskEditingDialog(taskEditingDialog);
            taskEditingDialog.makeDialogVisible(task);

            var originalTask = prepareTask(taskEditingDialog);
            var isTaskInScheduleBefore = tasksService.isTaskInSchedule(originalTask);
            var isEditedTaskInScheduleBefore = schedule.get(editedTask.getWeekday()).containsKey(editedTask.getTime());

            //when
            changeDataInTaskEditingDialog(taskEditingDialog, editedTask);
            taskEditingDialog.getButtons().getLast().doClick();

            var isTaskInScheduleAfter = tasksService.isTaskInSchedule(originalTask);
            var isEditedTaskInScheduleAfter = schedule.get(editedTask.getWeekday()).containsKey(editedTask.getTime());

            //then
            assertAll(
                    () -> assertTrue(isTaskInScheduleBefore),
                    () -> assertFalse(isEditedTaskInScheduleBefore),
                    () -> assertTrue(isTaskInScheduleAfter),
                    () -> assertFalse(isEditedTaskInScheduleAfter)
            );
        }

        @Tag("editTaskCorrect")
        @Test
        void cancelButtonShouldNotEditTaskInPlanner() {
            //given
            addTaskToComboBoxInTaskEditingDialog(taskEditingDialog);
            taskEditingDialog.makeDialogVisible(task);

            var originalTask = prepareTask(taskEditingDialog);
            var isTaskInPlannerBefore = plannerService.isTaskInPlanner(originalTask);
            var isEditedTaskInPlannerBefore = plannerService.isTaskInPlanner(editedTask);

            //when
            changeDataInTaskEditingDialog(taskEditingDialog, editedTask);
            taskEditingDialog.getButtons().getLast().doClick();

            var isTaskInPlannerAfter = plannerService.isTaskInPlanner(originalTask);
            var isEditedTaskInPlannerAfter = plannerService.isTaskInPlanner(editedTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskInPlannerBefore),
                    () -> assertFalse(isEditedTaskInPlannerBefore),
                    () -> assertTrue(isTaskInPlannerAfter),
                    () -> assertFalse(isEditedTaskInPlannerAfter)
            );
        }


        @Tag("editTaskCorrect")
        @Test
        void cancelButtonShouldNotEditTaskInWeekdayPlanner() {
            //given
            addTaskToComboBoxInTaskEditingDialog(taskEditingDialog);
            taskEditingDialog.makeDialogVisible(task);

            var originalTask = prepareTask(taskEditingDialog);
            var isTaskInWeekdayPlannerBefore = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(originalTask.getWeekday().name()), originalTask);
            var isEditedTaskInWeekdayPlannerBefore = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(editedTask.getWeekday().name()), editedTask);

            //when
            changeDataInTaskEditingDialog(taskEditingDialog, editedTask);
            taskEditingDialog.getButtons().getLast().doClick();

            var isTaskInWeekdayPlannerAfter = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(originalTask.getWeekday().name()), originalTask);
            var isEditedTaskInWeekdayPlannerAfter = weekdayPlannerService.isTaskInWeekdayPlanner(weekdayPlannerService.getWeekdayPlannerView(editedTask.getWeekday().name()), editedTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskInWeekdayPlannerBefore),
                    () -> assertFalse(isEditedTaskInWeekdayPlannerBefore),
                    () -> assertTrue(isTaskInWeekdayPlannerAfter),
                    () -> assertFalse(isEditedTaskInWeekdayPlannerAfter)
            );
        }

        @Test
        void cancelButtonShouldCancelTaskEditingDialog() {
            //given
            var taskEditingDialog = spy(new TaskEditingDialog());
            var controller = spy(new TaskEditingDialogController(taskEditingDialog));
            var cancelButton = controller.getTaskEditingDialog().getButtons().getLast();
            Arrays.stream(cancelButton.getActionListeners()).findFirst().ifPresent(cancelButton::removeActionListener);
            controller.initializeListeners();

            //when
            cancelButton.doClick();

            //then
            assertAll(
                    () -> verify(taskEditingDialog).makeDialogInvisible(),
                    () -> verify(controller, never()).editTask(any(SingleTask.class), any(SingleTask.class))
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
            var isTaskNameInComboBoxBefore = taskEditingDialog.getAllTaskNames().contains("taskName");

            //when
            var isTaskNameUpdated = controller.updateTaskNames("taskName");
            var isTaskNameInComboBoxAfter = taskEditingDialog.getAllTaskNames().contains("taskName");

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
            taskEditingDialog.getTasksComboBox().addItem("taskName");
            var isTaskNameInComboBoxBefore = taskEditingDialog.getAllTaskNames().contains("taskName");

            //when
            var isTaskNameUpdated = controller.updateTaskNames("taskName");
            var isTaskNameInComboBoxAfter = taskEditingDialog.getAllTaskNames().contains("taskName");

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
            var taskNamesComboBox = taskEditingDialog.getTasksComboBox();
            taskNamesComboBox.addItem("taskName");

            //when
            var isTaskUpdated = controller.updateTaskNames("newTask");
            var actualTaskNames = taskEditingDialog.getAllTaskNames();

            //then
            assertTrue(isTaskUpdated);
            assertThat(actualTaskNames, is(expectedTaskNames));
        }
    }

    //region helpful methods
    private void addTaskToComboBoxInTaskEditingDialog(TaskEditingDialog taskEditingDialog) {
        if (taskEditingDialog == null) return;

        taskEditingDialog.getTasksComboBox().addItem(task.getTaskName());
        taskEditingDialog.getTasksComboBox().addItem(editedTask.getTaskName());
        taskEditingDialog.getTasksComboBox().setSelectedItem(task.getTaskName());
    }

    private static SingleTask prepareTask(TaskEditingDialog taskAddingDialog) {
        if (taskAddingDialog == null) return null;

        return new SingleTask(
                taskAddingDialog.getTaskName(),
                taskAddingDialog.getDescription(),
                taskAddingDialog.getTime(),
                false,
                taskAddingDialog.getWeekday(),
                taskAddingDialog.getPriority()
        );
    }

    private static void changeDataInTaskEditingDialog(TaskEditingDialog taskEditingDialog, SingleTask editedTask) {
        if ((taskEditingDialog == null) || (editedTask == null)) return;

        taskEditingDialog.getDescriptionTextArea().setText(editedTask.getDescription());
        taskEditingDialog.getStatusComboBox().setSelectedItem(editedTask.isDone() ? "Tak" : "Nie");
        taskEditingDialog.getWeekdaysComboBox().setSelectedItem(editedTask.getWeekday().getWeekdayPL());
    }
    //endregion
}