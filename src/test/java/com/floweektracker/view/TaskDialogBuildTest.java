package com.floweektracker.view;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.service.TaskNamesService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.*;

import static com.floweektracker.model.PanelNames.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskDialogBuildTest {
    private final TestHelper helper = new TestHelper();
    private TaskDialogView view;
    private JPanel panel;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        var panelName = testInfo.getTags().stream().findFirst().orElse(null);

        initFields(panelName);
    }

    private void initFields(String panelName) {
        if (panelName != null && !panelName.equals("noSetUp")) {
            switch (panelName) {
                case "buttonsPanel" -> view = new TaskDialogView.TaskDialogBuilder().withButtons().build();
                case "descriptionPanel" -> view = new TaskDialogView.TaskDialogBuilder().withDescriptionPanel().build();
                case "priorityPanel" -> view = new TaskDialogView.TaskDialogBuilder().withPriorityPanel().build();
                case "timePanel" -> view = new TaskDialogView.TaskDialogBuilder().withTimePanel().build();
                case "weekdaysPanel" -> view = new TaskDialogView.TaskDialogBuilder().withWeekdaysPanel().build();
                case "tasksPanel" -> view = new TaskDialogView.TaskDialogBuilder().withTasksPanel().build();
                case "statusPanel" -> view = new TaskDialogView.TaskDialogBuilder().withStatusPanel().build();
            }

            panel = (JPanel) helper.findComponent(panelName, view.getDialog().getContentPane());
        }
    }

    @AfterEach
    void cleanUp() {
        view = null;
        panel = null;
    }

    @Nested
    class BaseTests {
        @Tag("noSetUp")
        @ParameterizedTest
        @ValueSource(strings = {"taskAddingDialog", "taskDialog"})
        void shouldCreateTaskDialogView(String dialogName) {
            //given
            var isDialogNameSpecified = dialogName.equals("taskAddingDialog");

            //when
            var view = (isDialogNameSpecified) ? new TaskDialogView.TaskDialogBuilder(dialogName).build() : new TaskDialogView.TaskDialogBuilder().build();

            //then
            assertAll(
                    () -> assertThat(view.getDialog().getContentPane().getComponentCount(), is(0)),
                    () -> assertThat(view.getDialog().getName(), is(dialogName))
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"buttonsPanel", "descriptionPanel", "priorityPanel", "timePanel", "weekdaysPanel", "tasksPanel", "statusPanel"})
        void shouldCreateTaskDialogViewWithSpecificPanel(String panelName) {
            //given+when
            initFields(panelName);

            //then
            assertAll(
                    () -> assertNotNull(panel),
                    () -> assertThat(view.getDialog().getContentPane().getComponentCount(), is(1)),
                    () -> assertInstanceOf(JPanel.class, panel),
                    () -> assertSame(view.getDialog().getContentPane(), Objects.requireNonNull(panel).getParent()),
                    () -> assertInstanceOf(FlowLayout.class, Objects.requireNonNull(panel).getLayout()),
                    () -> assertEquals(panelName, Objects.requireNonNull(panel).getName())
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"descriptionPanel", "priorityPanel", "timePanel", "weekdaysPanel", "tasksPanel", "statusPanel"})
        void specificPanelShouldHaveLabel(String panelName) {
            //given+when
            initFields(panelName);
            var infoLabel = helper.findComponent("infoLabel", Objects.requireNonNull(panel));

            //then
            assertAll(
                    () -> assertNotNull(infoLabel),
                    () -> assertInstanceOf(JLabel.class, infoLabel),
                    () -> assertEquals("infoLabel", Objects.requireNonNull(infoLabel).getName())
            );
        }

        @Tag("noSetUp")
        @ParameterizedTest
        @MethodSource("createTaskDialogViewsWithComponentClassAndName")
        void specificPanelShouldHaveComponent(String panelName, String componentName, Class<?> componentClass) {
            //given+when
            initFields(panelName);
            var component = helper.findComponent(componentName, Objects.requireNonNull(panel));

            //then
            assertAll(
                    () -> assertNotNull(component),
                    () -> assertInstanceOf(componentClass, component),
                    () -> assertEquals(componentName, Objects.requireNonNull(component).getName())
            );
        }

        private static Stream<Arguments> createTaskDialogViewsWithComponentClassAndName() {
            return Stream.of(
                    Arguments.of(BUTTONS_PANEL.getPanelName(), "confirmButton", JButton.class),
                    Arguments.of(BUTTONS_PANEL.getPanelName(), "cancelButton", JButton.class),
                    Arguments.of(DESCRIPTION_PANEL.getPanelName(), "descriptionScrollPane", JScrollPane.class),
                    Arguments.of(PRIORITY_PANEL.getPanelName(), "prioritySpinner", JSpinner.class),
                    Arguments.of(TIME_PANEL.getPanelName(), "timeSpinner", JSpinner.class),
                    Arguments.of(WEEKDAYS_PANEL.getPanelName(), "weekdaysComboBox", JComboBox.class),
                    Arguments.of(TASKS_PANEL.getPanelName(), "tasksComboBox", JComboBox.class),
                    Arguments.of(STATUS_PANEL.getPanelName(), "statusComboBox", JComboBox.class)
            );
        }
    }

    @Tag("buttonsPanel")
    @Test
    void buttonsPanelFromTaskDialogViewShouldHaveTwoButtons() {
        //then
        assertAll(
                () -> assertThat(panel, notNullValue()),
                () -> assertThat(Arrays.stream(panel.getComponents()).count(), is(2L)),
                () -> Arrays.stream(panel.getComponents()).forEach(button -> assertThat(button, is(instanceOf(JButton.class))))
        );
    }

    @Tag("descriptionPanel")
    @Nested
    class DescriptionPanelTests {
        @Test
        void textAreaFromDescriptionPanelShouldBeEditable() {
            //given+when
            var scrollPane = (JScrollPane) helper.findComponent("descriptionScrollPane", panel);
            var textArea = (JTextArea) Objects.requireNonNull(scrollPane).getViewport().getView();

            //then
            assertTrue(textArea.isEditable());
        }

        @Test
        void textAreaFromDescriptionPanelShouldBeScrollable() {
            //given+when
            var scrollPane = (JScrollPane) helper.findComponent("descriptionScrollPane", panel);
            var textArea = (JTextArea) Objects.requireNonNull(scrollPane).getViewport().getView();

            //then
            assertAll(
                    () -> assertThat(textArea.getName(), is("descriptionTextArea")),
                    () -> assertThat(scrollPane.getVerticalScrollBarPolicy(), is(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)),
                    () -> assertThat(scrollPane.getHorizontalScrollBarPolicy(), is(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER))
            );
        }
    }

    @Tag("priorityPanel")
    @Test
    void prioritySpinnerFromPriorityPanelShouldAcceptValuesFromOneToTen() {
        //given
        var prioritySpinner = (JSpinner) helper.findComponent("prioritySpinner", panel);

        //when
        var model = (SpinnerNumberModel) Objects.requireNonNull(prioritySpinner).getModel();

        //then
        assertAll(
                () -> assertThat((Integer) model.getValue(), is(5)),
                () -> assertThat((Integer) model.getMinimum(), is(1)),
                () -> assertThat((Integer) model.getMaximum(), is(10))
        );
    }

    @Tag("timePanel")
    @Test
    void timeSpinnerFromTimePanelShouldAccept24Hours() {
        //when+then
        var timeSpinner = (JSpinner) helper.findComponent("timeSpinner", panel);
        var model = (SpinnerDateModel) Objects.requireNonNull(timeSpinner).getModel();

        //then
        assertThat(model.getCalendarField(), is(Calendar.HOUR_OF_DAY));
    }

    @Tag("weekdaysPanel")
    @Test
    void weekdaysComboBoxFromWeekdaysPanelShouldHaveOnlyWeekdaysNamesToChoose() {
        //given
        @SuppressWarnings("unchecked")
        var weekdaysComboBox = (JComboBox<String>) helper.findComponent("weekdaysComboBox", panel);

        var numItems = Objects.requireNonNull(weekdaysComboBox).getItemCount();

        //then
        assertAll(
                () -> assertThat(numItems, is(WeekDays.values().length)),
                () -> IntStream.range(0, numItems)
                        .forEach(i -> assertThat(weekdaysComboBox.getItemAt(i), is(WeekDays.getWeekdayAt(i).getWeekdayPL())))
        );
    }

    @Tag("tasksPanel")
    @Test
    void tasksComboBoxFromTasksPanelShouldHaveTasksNamesToChoose() {
        //given+when
        @SuppressWarnings("unchecked")
        var tasksComboBox = (JComboBox<String>) helper.findComponent("tasksComboBox", panel);
        var taskNamesFromTasksComboBox = IntStream.range(0, Objects.requireNonNull(tasksComboBox).getItemCount())
                .mapToObj(tasksComboBox::getItemAt)
                .toList();

        var taskNamesFromFile = TaskNamesService.getService().getTaskNames().stream().toList();

        //then
        assertTrue(taskNamesFromFile.containsAll(taskNamesFromTasksComboBox));
    }

    @Tag("statusPanel")
    @Test
    void statusComboBoxFromStatusPanelShouldHaveOnlyYesNoOptions() {
        //given
        @SuppressWarnings("unchecked")
        var statusComboBox = (JComboBox<String>) helper.findComponent("statusComboBox", panel);
        var optionsFromStatusComboBox = IntStream.range(0, Objects.requireNonNull(statusComboBox).getItemCount())
                .mapToObj(statusComboBox::getItemAt)
                .toList();

        var options = new ArrayList<>(Arrays.asList("Tak", "Nie"));

        //then
        assertTrue(options.containsAll(optionsFromStatusComboBox));
    }
}