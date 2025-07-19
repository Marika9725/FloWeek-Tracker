package com.floweektracker.controller;

import com.floweektracker.*;
import com.floweektracker.model.WeekDays;
import com.floweektracker.service.TasksService;
import com.floweektracker.view.*;
import org.awaitility.Awaitility;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlannerControllerTest {
    private final TestHelper helper = new TestHelper();
    private final MainPanelView mainPanelView = MainPanelView.getView();
    private final TasksService tasksService = TasksService.getService();
    private JTable planner;

    //region setUp methods
    @BeforeEach
    void setUp(TestInfo testInfo) {
        switch (testInfo.getTags().stream().findFirst().orElse(null)) {
            case "noPlannerController" -> prepareForNoPlannerController();
            case "cellWithTask" -> prepareForCellWithTaskTests();
            case null -> prepareForNoTag();
            default -> throw new IllegalStateException();
        }
    }

    private void prepareForNoPlannerController() {
        this.planner = PlannerView.getView();
    }

    private void prepareForCellWithTaskTests() {
        this.planner = PlannerView.getView();

        var model = (DefaultTableModel) planner.getModel();
        model.insertRow(1, new Object[]{"12:00", "baseTask(0/5)", "-", "-", "-", "-", "-", "-"});
        tasksService.addTask(helper.createBaseTask());
    }

    private void prepareForNoTag() {
        this.planner = mainPanelView.getPlanner();
    }
    //endregion

    @AfterEach
    void tearDown() {
        //clean up
        while (planner.getRowCount() > 2) ((DefaultTableModel) planner.getModel()).removeRow(1);

        //close dialogs
        var windows = Arrays.stream(Window.getWindows()).filter(win -> win.getName().equalsIgnoreCase("taskEditingDialog")).toList();
        for (Window value : windows) {
            var window = (JDialog) value;
            window.dispose();
        }

        //show main panel
        var mainCards = MainFrame.getMAIN_FRAME().getContentPane();
        var mainCardsLayout = (CardLayout) mainCards.getLayout();
        mainCardsLayout.show(mainCards, "mainPanel");
    }

    @Tag("noPlannerController")
    @Test
    void shouldAddListenerToPlannerToPlannerView() {
        //given
        while (planner.getMouseListeners().length > 2) {
            planner.removeMouseListener(planner.getMouseListeners()[planner.getMouseListeners().length - 1]);
        }

        var plannerNumListenersBeforeInit = planner.getMouseListeners().length;

        //when
        new PlannerController();
        var plannerNumListenersAfterInit = planner.getMouseListeners().length;

        //then
        assertAll(
                () -> assertThat(plannerNumListenersBeforeInit, is(2)),
                () -> assertThat(plannerNumListenersAfterInit, is(3))
        );
    }

    @Tag("cellWithTask")
    @Test
    void shouldNotOpenTaskEditingDialogWhenTaskIsClickedOnlyOnce(){
        //given
        var isDialogVisible = helper.checkVisibility("taskEditingDialog", JDialog.class);

        //when
        simulateClickOnCell(1, 1, planner, 1);

        //then
        assertFalse(isDialogVisible);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertFalse(helper.checkVisibility("taskEditingDialog", JDialog.class)));
    }

    @Nested
    class CellWithTaskTests {
        @Tag("cellWithTask")
        @Test
        void doubleClickOnTaskInPlannerShouldOpenTaskEditingDialog() {
            //given
            var isDialogVisible = helper.checkVisibility("taskEditingDialog", JDialog.class);

            //when
            simulateClickOnCell(1, 1, planner, 2);

            //then
            assertFalse(isDialogVisible);
            Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> helper.checkVisibility("taskEditingDialog", JDialog.class));
        }

        @Tag("cellWithTask")
        @ParameterizedTest
        @MethodSource("com.floweektracker.TestHelper#createValidCellPoint")
        void doubleClickOnCellWhereIsNoTaskShouldNotOpenTaskEditingDialog(int row, int column) {
            //given
            var isDialogVisible = Arrays.stream(Window.getWindows())
                    .filter(window -> window.getName().equalsIgnoreCase("taskEditingDialog"))
                    .filter(Window::isVisible)
                    .anyMatch(dialog -> dialog instanceof JDialog && dialog.isVisible());

            //when
            simulateClickOnCell(row, column, planner, 2);

            //then
            assertFalse(isDialogVisible);
            Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> {
                var dialogs = Arrays.stream(Window.getWindows())
                        .filter(window -> window.getName().equalsIgnoreCase("taskEditingDialog"))
                        .filter(Window::isVisible);

                return dialogs.noneMatch(dialog -> dialog instanceof JDialog && dialog.isVisible());
            });
        }
    }

    @Nested
    class CellWithWeekdayNameTests {
        @ParameterizedTest
        @MethodSource("createCorrectCells")
        void doubleClickOnCellWhereIsWeekdayNameShouldOpenWeekdayPlannerView(int row, int column) {
            //given+when
            simulateClickOnCell(row, column, planner,2);

            //then
            var comp = findVisibleComponents();

            assertThat(comp, notNullValue());
            assertThat(comp.getName(), is("%sPanel".formatted(WeekDays.getWeekdayAt(column - 1).toString().toUpperCase())));
        }

        @ParameterizedTest
        @MethodSource("createValidCell")
        void doubleClickOnCellWhereIsNoWeekdayNameShouldNotOpenWeekdayPlannerView(int row, int column) {
            //given+when
            simulateClickOnCell(row, column, planner, 2);

            //then
            var comp = findVisibleComponents();

            assertThat(comp, notNullValue());
            assertThat(comp.getName(), is("MainPanel"));
        }

        private static Stream<Arguments> createCorrectCells() {
            return IntStream.rangeClosed(1, 7).mapToObj(column -> Arguments.of(0, column));
        }

        private static Stream<Arguments> createValidCell() {
            return Stream.concat(
                    Stream.of(Arguments.of(0, 0)),
                    IntStream.rangeClosed(0, 7).mapToObj(column -> Arguments.of(1, column))
            );
        }

        @Nullable
        private Component findVisibleComponents() {
            return Arrays.stream(MainFrame.getMAIN_FRAME().getContentPane().getComponents())
                    .filter(Component::isShowing)
                    .findFirst()
                    .orElse(null);
        }
    }

    private void simulateClickOnCell(int row, int column, JTable planner, int clickCount) {
        planner.setRowSelectionInterval(row, row);
        planner.setColumnSelectionInterval(column, column);

        var cellLocation = planner.getCellRect(row, column, true).getLocation();
        var doubleClick = new MouseEvent(
                planner,
                MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(),
                0,
                (int) cellLocation.getX(),
                (int) cellLocation.getY(),
                clickCount,
                false,
                MouseEvent.BUTTON1
        );
        planner.dispatchEvent(doubleClick);
    }
}