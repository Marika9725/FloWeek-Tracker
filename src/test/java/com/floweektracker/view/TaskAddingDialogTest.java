package com.floweektracker.view;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskAddingDialogTest {
    private final TestHelper helper = new TestHelper();
    private final SingleTask originalTask = helper.createBaseTask();
    private TaskAddingDialog taskAddingDialog;

    @BeforeEach
    void setUp() {
        taskAddingDialog = new TaskAddingDialog();
    }

    @Test
    void shouldCreateTaskDialogViewWithoutDoneSelecting() {
        //given + when
        var panels = taskAddingDialog.getDialog().getContentPane().getComponents();

        //then
        assertAll(
                () -> assertThat(taskAddingDialog.getDialog().getName(), is("taskAddingDialog")),
                () -> assertThat(taskAddingDialog.getDialog().getTitle(), is("Dodaj nowe zadanie")),
                () -> assertThat(panels.length, is(6)),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("buttonsPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("tasksPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("weekdaysPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("timePanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("priorityPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("descriptionPanel"))),
                () -> assertFalse(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("statusPanel")))
        );
    }

    @Nested
    class TasksComboBoxTests {
        @Test
        void shouldReturnTaskName() {
            //given
            taskAddingDialog.getTasksComboBox().addItem(originalTask.getTaskName());
            taskAddingDialog.getTasksComboBox().setSelectedItem(originalTask.getTaskName());

            //when+then
            helper.checkReturnedValue(taskAddingDialog.getTaskName(), String.class, originalTask.getTaskName());
        }

        @Test
        void shouldReturnNullIfTaskNameIsNotSelected() {
            assertThat(taskAddingDialog.getTaskName(), nullValue());
        }
    }

    @Nested
    class DescriptionScrollPaneTests {
        @Test
        void shouldReturnDescription() {
            //given
            var textArea = (JTextArea) taskAddingDialog.getDescriptionScrollPane().getViewport().getView();
            textArea.setText(originalTask.getDescription());

            //when+then
            helper.checkReturnedValue(taskAddingDialog.getDescription(), String.class, originalTask.getDescription());
        }

        @Test
        void shouldReturnNullIfDescriptionIsValid() {
            assertThat(taskAddingDialog.getDescription(), nullValue());
        }
    }

    @Nested
    class WeekdayComboBoxTests {
        @Test
        void shouldReturnWeekday() {
            //given
            taskAddingDialog.getWeekdaysComboBox().setSelectedIndex(originalTask.getWeekday().getPosition());

            //when+then
            helper.checkReturnedValue(taskAddingDialog.getWeekday(), WeekDays.class, originalTask.getWeekday());
        }

        @Test
        void shouldReturnMondayIfWeekdayIsNotSelected() {
            assertThat(taskAddingDialog.getWeekday(), is(WeekDays.MONDAY));
        }
    }

    @Nested
    class TimeSpinnerTests {
        @Test
        void shouldReturnTime() {
            //given
            var calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, originalTask.getTime().getHour());
            calendar.set(Calendar.MINUTE, originalTask.getTime().getMinute());
            taskAddingDialog.getTimeSpinner().setValue(calendar.getTime());

            //when+then
            helper.checkReturnedValue(taskAddingDialog.getTime(), LocalTime.class, originalTask.getTime());
        }

        @Test
        void shouldReturnMidnightIfTimeIsNotSelected() {
            assertThat(taskAddingDialog.getTime(), is(LocalTime.of(0, 0)));
        }
    }

    @Nested
    class PrioritySpinnerTests {
        @Test
        void shouldReturnPriority() {
            //given
            taskAddingDialog.getPrioritySpinner().setValue(originalTask.getPriority());

            //when+then
            helper.checkReturnedValue(taskAddingDialog.getPriority(), Byte.class, originalTask.getPriority());
        }

        @Test
        void shouldReturnMiddleValueIfPriorityIsNotSelected() {
            assertThat(taskAddingDialog.getPriority(), is((byte) 5));
        }
    }

    @Test
    void componentsShouldHaveDefaultValuesWhenDialogIsVisible() {
        //given
        var taskAddingDialog = spy(new TaskAddingDialog());

        //when
        taskAddingDialog.makeDialogVisible();

        //then
        assertAll(
                () -> verify(taskAddingDialog).makeDialogVisible(),
                () -> assertThat(taskAddingDialog.getDescription(), is(nullValue())),
                () -> assertThat(taskAddingDialog.getTime(), is(LocalTime.of(0, 0))),
                () -> assertThat(taskAddingDialog.getPriority(), is((byte) 5)),
                () -> assertThat(taskAddingDialog.getTaskName(), is(nullValue())),
                () -> assertThat(taskAddingDialog.getWeekday(), is(WeekDays.MONDAY))
        );
    }

    @Nested
    class GetAllTaskNames {
        @Test
        void shouldReturnEmptyListWhenThereIsNoTaskNameInTaskNamesComboBox() {
            //given+when
            var actualTaskNames = taskAddingDialog.getAllTaskNames();

            //then
            assertThat(actualTaskNames, is(notNullValue()));
            assertTrue(actualTaskNames.isEmpty());
        }

        @Test
        void shouldReturnCompleteListWhenThereAreTaskNamesInTaskNamesComboBox() {
            //given
            var expectedTaskNames = new ArrayList<>(List.of("taskName1", "taskName2", "taskName3"));
            var taskNamesComboBox = taskAddingDialog.getTasksComboBox();
            expectedTaskNames.forEach(taskNamesComboBox::addItem);

            //when
            var actualTaskNames = taskAddingDialog.getAllTaskNames();

            //then
            assertThat(actualTaskNames, is(expectedTaskNames));
        }
    }
}