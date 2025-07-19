package com.floweektracker.controller;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.service.*;
import com.floweektracker.util.DialogUtils;
import com.floweektracker.view.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainPanelControllerTest {
    //region fields
    private final TasksService tasksService = TasksService.getService();
    private final MainPanelView mainPanelView = MainPanelView.getView();
    private final PlannerService plannerService = PlannerService.getService();
    private final WeekdayPlannerService weekdayPlannerService = WeekdayPlannerService.getService();
    private final JDialog taskNamesDialog = TaskNamesDialog.getView().getDialog();
    private final TestHelper helper = new TestHelper();
    private SingleTask task;
    private SingleTask[] tasks;
    private MainPanelController mainPanelController;
    private Map<WeekDays, Map<LocalTime, SingleTask>> schedule;
    private JTable planner;
    //endregion

    //region setUp and tearDown methods
    //region setUp
    @BeforeEach
    void setUp(TestInfo testInfo) {
        this.mainPanelController = testInfo.getTags().contains("noMainPanelController") ? null : new MainPanelController(mainPanelView, new TaskAddingDialogController(new TaskAddingDialog()));
        this.schedule = tasksService.getSchedule();
        this.planner = plannerService.getPlanner();
        this.task = helper.createBaseTask();
        this.tasks = new SingleTask[]{helper.createBaseTask(), helper.createBaseTask(), helper.createEditedTask(), helper.createEditedTask()};

        switch (testInfo.getTags().stream().findFirst().orElse("")) {
            case "deleteButtonTests" -> prepareForDeleteButtonTests();
            case "cleanScheduleButtonTests" -> prepareForCleanScheduleButtonTests();
            case "noMainPanelController" -> removeListenersFromButtons();
        }
    }

    private void prepareForCleanScheduleButtonTests() {
        tasks[1].setTime(LocalTime.of(17, 0));
        tasks[3].setWeekday(WeekDays.WEDNESDAY);

        for (SingleTask task : tasks) {
            task.setDone(true);
            tasksService.addTask(task);
            plannerService.addTask(task);
            weekdayPlannerService.addTask(task);
        }
    }

    private void prepareForDeleteButtonTests() {
        task.setDone(true);
        tasksService.addTask(task);
        plannerService.addTask(task);
        weekdayPlannerService.addTask(task);
    }

    private void removeListenersFromButtons() {
        mainPanelView.getButtons().forEach(this::removeListenersFromButton);
        removeListenersFromButton(mainPanelView.getInfoButton());
    }

    private void removeListenersFromButton(JButton button) {
        if (button == null) return;

        for (ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
    }
    //endregion

    //region tearDown
    @AfterEach
    void tearDown() {
        tasksService.getSchedule().values().stream()
                .flatMap(weekdays -> weekdays.values().stream())
                .forEach(task -> {
                    plannerService.deleteTask(task);
                    weekdayPlannerService.deleteTask(task);
                });
        tasksService.getSchedule().values().forEach(Map::clear);

        for (JButton button : mainPanelView.getButtons()) {
            for (ActionListener listener : button.getActionListeners()) {
                button.removeActionListener(listener);
            }
        }

        this.tasks = null;

        //close JDialogs
        closeDialogs(Window.getWindows());
    }

    private void closeDialogs(Window[] windows) {
        var openWindows = Arrays.stream(windows)
                .filter(window -> window.isVisible() && window instanceof JDialog)
                .toArray(Window[]::new);

        for (Window openWindow : openWindows) {
            openWindow.dispose();
        }
    }
    //endregion
    //endregion

    @Tag("noMainPanelController")
    @Test
    void constructorShouldAddListenersToButtonsFromMainView() {
        //given
        var buttonNumListenersBefore = numButtonListeners();
        var infoButtonNumListenersBefore = mainPanelView.getInfoButton().getActionListeners().length;

        //when
        new MainPanelController(MainPanelView.getView(), null);

        var infoButtonsNumListenersAfter = mainPanelView.getInfoButton().getActionListeners().length;

        //then
        assertAll(
                () -> Arrays.stream(buttonNumListenersBefore).forEach(i -> assertThat(i, is(0))),
                () -> assertThat(infoButtonNumListenersBefore, is(0)),
                () -> Arrays.stream(numButtonListeners()).forEach(i -> assertThat(i, is(1))),
                () -> assertThat(infoButtonsNumListenersAfter, is(1))
        );
    }

    @Test
    void infoButtonShouldOpenJOptionPane() {
        //given
        var isDialogVisible = helper.checkVisibility(null, JOptionPane.class);
        var infoButton = mainPanelView.getInfoButton();

        //when
        SwingUtilities.invokeLater(infoButton::doClick);

        //then
        assertFalse(isDialogVisible);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> helper.checkVisibility(null, JOptionPane.class));
    }

    @Test
    void addButtonShouldOpenTaskAddingDialog() {
        //given
        closeDialogs(Window.getWindows());
        var isDialogVisible = helper.checkVisibility("taskAddingDialog", JDialog.class);

        var addButton = mainPanelView.getButtons().getFirst();

        // when
        SwingUtilities.invokeLater(addButton::doClick);

        // then
        assertFalse(isDialogVisible);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> helper.checkVisibility("taskAddingDialog", JDialog.class));
    }

    @Nested
    class DeleteButtonTests {
        @Tag("deleteButtonTests")
        @Test
        void deleteButtonShouldDeleteTaskFromSchedule() {
            //given
            planner.changeSelection(1, 1, false, false);
            var isTaskInScheduleBefore = tasksService.isTaskInSchedule(task);

            //when
            mainPanelView.getButtons().get(2).doClick();

            //then
            assertAll(
                    () -> assertTrue(isTaskInScheduleBefore),
                    () -> assertFalse(tasksService.isTaskInSchedule(task))
            );
        }

        @Tag("deleteButtonTests")
        @Test
        void deleteButtonShouldDeleteTaskFromPlannerView() {
            //given
            planner.changeSelection(1, 1, false, false);
            var isTaskInPlannerViewBefore = plannerService.isTaskInPlanner(task);

            //when
            mainPanelView.getButtons().get(2).doClick();

            //then
            assertAll(
                    () -> assertTrue(isTaskInPlannerViewBefore),
                    () -> assertFalse(plannerService.isTaskInPlanner(task))
            );
        }

        @Tag("deleteButtonTests")
        @Test
        void deleteButtonShouldDeleteTaskFromWeekdayPlannerView() {
            //given
            planner.changeSelection(1, 1, false, false);
            var isTaskInWeekdayPlannerViewBefore = isTaskInWeekdayPlanner();

            //when
            mainPanelView.getButtons().get(2).doClick();

            //then
            assertAll(
                    () -> assertTrue(isTaskInWeekdayPlannerViewBefore),
                    () -> assertFalse(isTaskInWeekdayPlanner())
            );
        }

        @Tag("deleteButtonTests")
        @Test
        void shouldShowInfoDialogToTheUserIfTaskInPlannerIsNotSelected() {
            //given
            var isTaskInPlannerViewBefore = plannerService.isTaskInPlanner(task);

            //when+then
            try (var dialogUtils = mockStatic(DialogUtils.class)) {
                mainPanelView.getButtons().get(2).doClick();

                dialogUtils.verify(() -> DialogUtils.showMessageDialog(anyString(), anyString()));
            } catch (NoSuchMethodError e) {e.printStackTrace();}

            assertAll(
                    () -> assertTrue(isTaskInPlannerViewBefore),
                    () -> assertTrue(plannerService.isTaskInPlanner(task)),
                    () -> assertTrue(isTaskInWeekdayPlanner())
            );
        }

        @Tag("deleteButtonTests")
        @ParameterizedTest
        @MethodSource("com.floweektracker.TestHelper#createValidCellPoint")
        void shouldShowInfoDialogIfIsSelectedCellWithNoTask(int row, int column) {
            //given
            planner.changeSelection(row, column, false, false);

            var isTaskInPlannerViewBefore = plannerService.isTaskInPlanner(task);

            //when
            SwingUtilities.invokeLater(() -> mainPanelView.getButtons().get(2).doClick());

            //then
            assertAll(
                    () -> assertTrue(isTaskInPlannerViewBefore),
                    () -> assertTrue(plannerService.isTaskInPlanner(task)),
                    () -> assertTrue(isTaskInWeekdayPlanner())
            );
        }
    }

    @Nested
    class CleanScheduleButtonTests {
        @Test
        void whenCleanScheduleButtonIsClickedCleanerDialogOpens() {
            //given
            var isDialogVisible = helper.checkVisibility(null, JOptionPane.class);
            var cleanScheduleButton = mainPanelView.getButtons().get(3);

            // when
            SwingUtilities.invokeLater(cleanScheduleButton::doClick);

            // then
            assertFalse(isDialogVisible);
            Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> helper.checkVisibility(null, JOptionPane.class));
        }

        @Tag("cleanScheduleButtonTests")
        @ParameterizedTest
        @MethodSource("com.floweektracker.controller.MainPanelControllerTest#provideWeekdays")
        void shouldDeleteTasksFromScheduleByChosenWeekdays(List<WeekDays> weekdaysToDelete) {
            //given
            var areTasksInScheduleBefore = areTasksInSchedule();

            //when
            mainPanelController.deleteTasksForWeekdays(weekdaysToDelete);

            var areTasksInScheduleAfter = areTasksInSchedule();

            //then
            assertAll(
                    () -> Arrays.stream(areTasksInScheduleBefore).forEach(Assertions::assertTrue),
                    () -> IntStream.range(0, weekdaysToDelete.size()).forEach(i -> assertFalse(areTasksInScheduleAfter[i])),
                    () -> assertTrue(areTasksInScheduleAfter[3])
            );
        }

        @Tag("cleanScheduleButtonTests")
        @ParameterizedTest
        @MethodSource("com.floweektracker.controller.MainPanelControllerTest#provideWeekdays")
        void shouldDeleteTasksFromPlannerByChosenWeekdays(List<WeekDays> weekdaysToDelete) {
            //given
            var areTasksInPlannerViewBefore = areTasksInPlannerViewBefore();

            //when
            mainPanelController.deleteTasksForWeekdays(weekdaysToDelete);

            var areTasksInPlannerViewAfter = areTasksInPlannerViewBefore();

            //then
            assertAll(
                    () -> Arrays.stream(areTasksInPlannerViewBefore).forEach(Assertions::assertTrue),
                    () -> IntStream.range(0, weekdaysToDelete.size()).forEach(i -> assertFalse(areTasksInPlannerViewAfter[i])),
                    () -> assertTrue(areTasksInPlannerViewAfter[3])
            );
        }

        @Tag("cleanScheduleButtonTests")
        @ParameterizedTest
        @MethodSource("com.floweektracker.controller.MainPanelControllerTest#provideWeekdays")
        void shouldDeleteTasksFromWeekdayPlannerByChosenWeekdays(List<WeekDays> weekdaysToDelete) {
            //given
            var areTasksInWeekdayPlannerViewBefore = areTasksInWeekdayPlanner();

            //when
            mainPanelController.deleteTasksForWeekdays(weekdaysToDelete);

            var areTasksInWeekdayPlannerViewAfter = areTasksInWeekdayPlanner();

            //then
            assertAll(
                    () -> Arrays.stream(areTasksInWeekdayPlannerViewBefore).forEach(Assertions::assertTrue),
                    () -> IntStream.range(0, weekdaysToDelete.size()).forEach(i -> assertFalse(areTasksInWeekdayPlannerViewAfter[i])),
                    () -> assertTrue(areTasksInWeekdayPlannerViewAfter[3])
            );
        }

        @Tag("deleteButtonTests")
        @Test
        void shouldNotDeleteTaskWhenNoWeekdaysAreChosen() {
            //given
            var isTaskInScheduleBefore = tasksService.isTaskInSchedule(task);
            var isTaskInPlannerBefore = plannerService.isTaskInPlanner(task);
            var isTaskInWeekdayPlannerBefore = isTaskInWeekdayPlanner();

            //when
            mainPanelController.deleteTasksForWeekdays(null);

            //then
            assertAll(
                    () -> assertTrue(isTaskInScheduleBefore),
                    () -> assertTrue(isTaskInPlannerBefore),
                    () -> assertTrue(isTaskInWeekdayPlannerBefore),
                    () -> assertTrue(tasksService.isTaskInSchedule(task)),
                    () -> assertTrue(plannerService.isTaskInPlanner(task)),
                    () -> assertTrue(isTaskInWeekdayPlanner())
            );
        }

        private Boolean[] areTasksInWeekdayPlanner() {
            return Arrays.stream(tasks)
                    .map(task -> Arrays.stream(weekdayPlannerService.getWeekdayPlannerView(task.getWeekday().toString()).getContentPanel().getComponents())
                            .anyMatch(comp -> comp.getName().equals("%s_%s".formatted(task.getTime(), task.getTaskName()))))
                    .toArray(Boolean[]::new);
        }

        private Boolean[] areTasksInPlannerViewBefore() {
            return Arrays.stream(tasks)
                    .map(plannerService::isTaskInPlanner)
                    .toArray(Boolean[]::new);
        }

        private Boolean[] areTasksInSchedule() {
            return Arrays.stream(tasks)
                    .map(task -> schedule.get(task.getWeekday()).containsValue(task))
                    .toArray(Boolean[]::new);
        }
    }

    @Test
    void addTaskNameButtonShouldOpenTaskNamesView() {
        //given
        var addButton = mainPanelView.getButtons().get(1);
        var isVisibleBefore = taskNamesDialog.isVisible();

        //when
        SwingUtilities.invokeLater(addButton::doClick);

        //then
        helper.checkVisibility(taskNamesDialog);
        assertFalse(isVisibleBefore);
    }

    @Nested
    class ResetPointsButtonTests {
        @Test
        void whenResetPointsButtonIsClickedCleanerDialogOpens() {
            //given
            var isDialogVisible = helper.checkVisibility(null, JOptionPane.class);
            var cleanScheduleButton = mainPanelView.getButtons().get(4);

            // when
            SwingUtilities.invokeLater(cleanScheduleButton::doClick);

            // then
            assertFalse(isDialogVisible);
            Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> helper.checkVisibility(null, JOptionPane.class));
        }

        @Tag("cleanScheduleButtonTests")
        @ParameterizedTest
        @MethodSource("com.floweektracker.controller.MainPanelControllerTest#provideWeekdays")
        void shouldResetTasksPointsFromScheduleByChosenWeekdays(List<WeekDays> weekdaysToResetPoints) {
            //given
            var areTasksInScheduleDoneBefore = areTasksDoneInSchedule();

            //when
            mainPanelController.resetPoints(weekdaysToResetPoints);

            var areTasksInScheduleDoneAfter = areTasksDoneInSchedule();

            //then
            assertAll(
                    () -> Arrays.stream(areTasksInScheduleDoneBefore).forEach(Assertions::assertTrue),
                    () -> IntStream.range(0, weekdaysToResetPoints.size()).forEach(i -> assertFalse(areTasksInScheduleDoneAfter[i])),
                    () -> assertTrue(areTasksInScheduleDoneAfter[3])
            );
        }

        @Tag("cleanScheduleButtonTests")
        @ParameterizedTest
        @MethodSource("com.floweektracker.controller.MainPanelControllerTest#provideWeekdays")
        void shouldResetTasksPointsFromPlannerByChosenWeekdays(List<WeekDays> weekdaysToResetPoints) {
            //given
            var areTasksInPlannerViewDoneBefore = areTasksDoneInPlanner();

            //when
            mainPanelController.resetPoints(weekdaysToResetPoints);

            var areTasksInPlannerViewDoneAfter = areTasksDoneInPlanner();

            //then
            assertAll(
                    () -> Arrays.stream(areTasksInPlannerViewDoneBefore).forEach(Assertions::assertTrue),
                    () -> IntStream.range(0, weekdaysToResetPoints.size()).forEach(i -> assertFalse(areTasksInPlannerViewDoneAfter[i])),
                    () -> assertTrue(areTasksInPlannerViewDoneAfter[3])
            );
        }

        @Tag("cleanScheduleButtonTests")
        @ParameterizedTest
        @MethodSource("com.floweektracker.controller.MainPanelControllerTest#provideWeekdays")
        void shouldResetTasksPointsFromWeekdayPlannerByChosenWeekdays(List<WeekDays> weekdaysToResetPoints) {
            //given
            var areTasksInWeekdayPlannerDoneBefore = areTasksDoneInWeekdayPlanner();

            //when
            mainPanelController.resetPoints(weekdaysToResetPoints);

            var areTasksInWeekdayPlannerDoneAfter = areTasksDoneInWeekdayPlanner();

            //then
            assertAll(
                    () -> Arrays.stream(areTasksInWeekdayPlannerDoneBefore).forEach(Assertions::assertTrue),
                    () -> IntStream.range(0, weekdaysToResetPoints.size()).forEach(i -> assertFalse(areTasksInWeekdayPlannerDoneAfter[i])),
                    () -> assertTrue(areTasksInWeekdayPlannerDoneAfter[3])
            );
        }

        @Tag("deleteButtonTests")
        @Test
        void shouldNoResetTaskPointsWhenNoWeekdaysAreChosen() {
            //given
            var isTaskInScheduleDoneBefore = isTaskDoneInSchedule();
            var isTaskInPlannerDoneBefore = isTaskDoneInPlanner();

            //when
            mainPanelController.deleteTasksForWeekdays(null);

            //then
            assertAll(
                    () -> assertTrue(isTaskInScheduleDoneBefore),
                    () -> assertTrue(isTaskInPlannerDoneBefore),
                    () -> assertTrue(this::isTaskDoneInSchedule),
                    () -> assertTrue(this::isTaskDoneInPlanner)
            );
        }

        private Boolean[] areTasksDoneInSchedule() {
            return Arrays.stream(tasks)
                    .map(task -> schedule.get(task.getWeekday()).get(task.getTime()).isDone())
                    .toArray(Boolean[]::new);
        }

        private Boolean[] areTasksDoneInPlanner() {
            return Arrays.stream(tasks)
                    .map(task -> {
                        var row = plannerService.findRowIndex(task.getTime());
                        var column = plannerService.findColumnIndex(task.getWeekday());
                        var value = plannerService.getPlanner().getValueAt(row, column).toString();

                        return value.contains(String.format("(%d/%d)", task.getPriority(), task.getPriority()));
                    })
                    .toArray(Boolean[]::new);
        }

        private Boolean[] areTasksDoneInWeekdayPlanner() {
            return Arrays.stream(tasks).map(this::isTaskDone).toArray(Boolean[]::new);
        }

        private Boolean isTaskDone(SingleTask task) {
            var taskIdentifier = "%s_%s".formatted(task.getTime(), task.getTaskName());
            var expectedLabel = "%s(%d/%<d)".formatted(task.getTaskName(), task.getPriority());
            var components = weekdayPlannerService.getWeekdayPlannerView(task.getWeekday().toString()).getContentPanel().getComponents();

            return Arrays.stream(components)
                    .filter(comp -> comp.getName().equals(taskIdentifier) && comp instanceof JPanel)
                    .map(comp -> (JPanel) comp)
                    .flatMap(panel -> Arrays.stream(panel.getComponents()))
                    .anyMatch(comp -> comp.getName().equals("taskNameLabel") && comp instanceof JLabel && ((JLabel) comp).getText().equals(expectedLabel));
        }

        private boolean isTaskDoneInPlanner() {
            return plannerService.getPlanner().getValueAt(
                    plannerService.findRowIndex(task.getTime()),
                    plannerService.findColumnIndex(task.getWeekday())
            ).toString().contains(String.format("(%d/%d)", task.getPriority(), task.getPriority()));
        }

        private boolean isTaskDoneInSchedule() {
            return schedule.get(task.getWeekday()).get(task.getTime()).isDone();
        }
    }

    //region helper methods
    private Boolean isTaskInWeekdayPlanner() {
        return Arrays.stream(weekdayPlannerService.getWeekdayPlannerView(task.getWeekday().toString()).getContentPanel().getComponents())
                .anyMatch(comp -> comp.getName().equals("%s_%s".formatted(task.getTime(), task.getTaskName())));
    }

    private static Stream<Arguments> provideWeekdays() {
        return Stream.of(
                Arguments.of((List.of(WeekDays.MONDAY))),
                Arguments.of((List.of(WeekDays.MONDAY, WeekDays.TUESDAY)))
        );
    }

    private Integer[] numButtonListeners() {
        return mainPanelView.getButtons().stream()
                .map(button -> button.getActionListeners().length)
                .toArray(Integer[]::new);
    }
    //endregion
}