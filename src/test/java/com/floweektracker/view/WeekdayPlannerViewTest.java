package com.floweektracker.view;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.service.TasksService;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class WeekdayPlannerViewTest {
    private final TestHelper helper = new TestHelper();
    private final SingleTask task = helper.createBaseTask();
    private WeekdayPlannerView weekdayPlannerView;
    private JPanel weekdayPanel;

    @BeforeEach
    void setUp() {
        weekdayPlannerView = new WeekdayPlannerView(WeekDays.MONDAY);
        weekdayPanel = (JPanel) helper.findComponent("weekdayPanelMONDAY", weekdayPlannerView.getViewport());
    }

    @AfterEach
    void cleanUp() {
        weekdayPlannerView = null;
        weekdayPanel = null;
    }

    @Nested
    class ConstructorTests {
        @Test
        void constructorShouldNotCreateSameInstances() {
            assertThat(new WeekdayPlannerView(WeekDays.MONDAY), not(sameInstance(new WeekdayPlannerView(WeekDays.MONDAY))));
        }

        @Test
        void constructorShouldInitializeWeekdayPlannerView() {
            checkReturnedValue(weekdayPlannerView, JScrollPane.class, "MONDAYPanel");
            assertThat(weekdayPlannerView.getVerticalScrollBarPolicy(), is(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED));
        }

        @Test
        void shouldThrowExceptionWhenWeekdayIsNull(){
            assertThrows(IllegalArgumentException.class, () -> new WeekdayPlannerView(null));
        }
    }

    @Nested
    class WeekdayPanelTests {
        @Test
        void weekdayPlannerViewShouldContainsWeekdayPanel() {
            assertThat(weekdayPanel, notNullValue());
            checkReturnedValue(weekdayPanel, JPanel.class, "weekdayPanelMONDAY");
            assertThat(weekdayPanel.getLayout().getClass(), is(sameInstance(BorderLayout.class)));
        }

        @Test
        void weekdayPanelShouldContainsHeadlinePanel() {
            //given+when
            var headlinePanel = helper.findComponent("headlinePanel", weekdayPanel);

            //then
            assertThat(headlinePanel, notNullValue());
            checkReturnedValue(headlinePanel, JPanel.class, "headlinePanel");
        }

        @Test
        void headlinePanelShouldContainsTitle() {
            //given+when
            var headlinePanel = (JPanel) helper.findComponent("headlinePanel", weekdayPanel);

            //when
            var titleLabel = (JLabel) helper.findComponent("titleLabel", Objects.requireNonNull(headlinePanel));

            //then
            assertThat(titleLabel, notNullValue());
            checkReturnedValue(titleLabel, JLabel.class, "titleLabel");
            assertAll(
                    () -> assertThat(titleLabel.getText(), is("Poniedziałek")),
                    () -> assertThat(titleLabel.getFont(), is(new Font("Arial", Font.BOLD, 40)))
            );
        }

        @Test
        void weekdayPanelShouldContainsContentPanel() {
            //given+when
            var contentPanel = (JPanel) helper.findComponent("contentPanel", weekdayPanel);

            //then
            assertThat(contentPanel, notNullValue());
            checkReturnedValue(contentPanel, JPanel.class, "contentPanel");
            assertThat(contentPanel.getLayout(), is(instanceOf(BoxLayout.class)));
        }

        @Test
        void weekdayPanelShouldContainsBottomPanel() {
            //given+when
            var bottomPanel = (JPanel) helper.findComponent("bottomPanel", weekdayPanel);

            //then
            assertThat(bottomPanel, notNullValue());
            checkReturnedValue(bottomPanel, JPanel.class, "bottomPanel");
            assertThat(bottomPanel.getLayout(), is(instanceOf(FlowLayout.class)));
        }
    }

    @Nested
    class ContentPanelTests {
        @Test
        void whenPlannerHasNoTasksContentPanelShouldHaveNoTaskPanels() {
            //given
            var contentPanel = (JPanel) helper.findComponent("contentPanel", weekdayPanel);

            //when
            var components = Objects.requireNonNull(contentPanel).getComponents();

            //then
            assertAll(
                    () -> assertThat(components, notNullValue()),
                    () -> assertThat(components.length, is(0))
            );
        }

        @Test
        void tasksPanelsNumShouldBeSameAsTasksNumInPlanner() {
            //given
            var service = TasksService.getService();
            for (SingleTask singleTask : helper.prepareSingleTasks()) service.addTask(singleTask);

            var contentPanel = new WeekdayPlannerView(WeekDays.MONDAY).getContentPanel();

            //when
            var taskPanels = Arrays.stream(contentPanel.getComponents()).toList();

            //then
            assertAll(
                    () -> assertThat(taskPanels, notNullValue()),
                    () -> assertThat(taskPanels.size(), is(3)),
                    () -> taskPanels.forEach(taskPanel -> assertThat(taskPanel, instanceOf(JPanel.class)))
            );

            for (var weekday : WeekDays.values()) {service.getSchedule().get(weekday).clear();}
        }

        @Test
        void taskPanelShouldContainsTaskNameLabel() {
            //given
            var taskNameLabelText = String.format("%s(%d/%d)", task.getTaskName(), task.calculatePoints(), task.getPriority());
            var taskPanel = weekdayPlannerView.createTaskPanel(task);

            //when
            var taskNameLabel = (JLabel) helper.findComponent("taskNameLabel", taskPanel);

            //then
            assertThat(taskNameLabel, notNullValue());
            checkReturnedValue(taskNameLabel, JLabel.class, "taskNameLabel");
            assertThat(taskNameLabel.getText(), is(taskNameLabelText));
        }

        @Test
        void taskPanelShouldContainsTimeLabel() {
            //given
            var taskPanel = weekdayPlannerView.createTaskPanel(task);

            //when
            var timeLabel = (JLabel) helper.findComponent("timeLabel", taskPanel);

            //then
            assertThat(timeLabel, notNullValue());
            assertAll(
                    () -> assertThat(timeLabel, is(instanceOf(JLabel.class))),
                    () -> assertThat(timeLabel.getName(), is("timeLabel")),
                    () -> assertThat(timeLabel.getText(), is(task.getTime().toString()))
            );
        }

        @Test
        void taskPanelShouldContainsDescriptionScrollPane() {
            //given
            var taskPanel = weekdayPlannerView.createTaskPanel(task);

            //when
            var descriptionScrollPane = (JScrollPane) helper.findComponent("descriptionScrollPane", taskPanel);
            var descriptionTextArea = (JTextArea) Objects.requireNonNull(descriptionScrollPane).getViewport().getView();

            //then
            assertThat(descriptionScrollPane, notNullValue());
            checkReturnedValue(descriptionScrollPane, JScrollPane.class, "descriptionScrollPane");
            checkReturnedValue(descriptionTextArea, JTextArea.class, "descriptionTextArea");
            assertThat(descriptionTextArea.getText(), is(task.getDescription()));
        }
    }

    @Test
    void bottomPanelShouldContainsReturnButton() {
        //given
        var bottomPanel = (JPanel) helper.findComponent("bottomPanel", weekdayPanel);

        //when
        var returnButton = (JButton) helper.findComponent("returnButton", Objects.requireNonNull(bottomPanel));

        //then
        assertThat(returnButton, notNullValue());
        checkReturnedValue(returnButton, JButton.class, "returnButton");
        assertThat(returnButton.getText(), is("Powrót"));
    }

    private void checkReturnedValue(Component component, Class<?> expectedType, String expectedName) {
        assertAll(
                () -> assertThat(component.getName(), is(expectedName)),
                () -> assertThat(component, is(instanceOf(expectedType)))
        );
    }
}