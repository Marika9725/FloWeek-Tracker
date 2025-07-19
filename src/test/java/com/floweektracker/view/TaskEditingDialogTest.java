package com.floweektracker.view;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.util.DialogUtils;
import org.junit.jupiter.api.*;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class TaskEditingDialogTest {
    private final TestHelper helper = new TestHelper();
    private final SingleTask originalTask = helper.createBaseTask();
    private TaskEditingDialog taskEditingDialog;

    @BeforeEach
    void setUp() {
        this.taskEditingDialog = new TaskEditingDialog();
    }

    @Test
    void shouldCreateTaskEditingDialogWithDoneSelecting() {
        //given + when
        var panels = taskEditingDialog.getDialog().getContentPane().getComponents();

        //then
        assertAll(
                () -> assertThat(taskEditingDialog.getDialog().getName(), is("taskEditingDialog")),
                () -> assertThat(taskEditingDialog.getDialog().getTitle(), is("Edytuj zadanie")),
                () -> assertThat(panels.length, is(7)),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("buttonsPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("tasksPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("weekdaysPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("timePanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("priorityPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("descriptionPanel"))),
                () -> assertTrue(Arrays.stream(panels).anyMatch(panel -> panel.getName().equals("statusPanel")))
        );
    }

    @Test
    void shouldReturnTaskName() {
        //given
        taskEditingDialog.getTasksComboBox().addItem(originalTask.getTaskName());

        //when+then
        helper.checkReturnedValue(taskEditingDialog.getTaskName(), String.class, originalTask.getTaskName());
    }

    @Test
    void shouldReturnDescription() {
        //given
        taskEditingDialog.getDescriptionTextArea().setText(originalTask.getDescription());

        //when+then
        helper.checkReturnedValue(taskEditingDialog.getDescription(), String.class, originalTask.getDescription());
    }

    @Test
    void shouldReturnWeekday() {
        helper.checkReturnedValue(taskEditingDialog.getWeekday(), WeekDays.class, originalTask.getWeekday());
    }

    @Test
    void shouldReturnTime() {
        //given
        var calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, originalTask.getTime().getHour());
        calendar.set(Calendar.MINUTE, originalTask.getTime().getMinute());
        taskEditingDialog.getTimeSpinner().setValue(calendar.getTime());

        //when+then
        helper.checkReturnedValue(taskEditingDialog.getTime(), LocalTime.class, originalTask.getTime());
    }

    @Test
    void shouldReturnPriority() {
        //given
        taskEditingDialog.getPrioritySpinner().setValue(originalTask.getPriority());

        //when+then
        helper.checkReturnedValue(taskEditingDialog.getPriority(), Byte.class, originalTask.getPriority());
    }

    @Test
    void shouldReturnStatus() {
        //given
        taskEditingDialog.getStatusComboBox().setSelectedItem(originalTask.isDone() ? "Tak" : "Nie");

        //when+then
        helper.checkReturnedValue(taskEditingDialog.getStatus(), Boolean.class, originalTask.isDone());
    }

    @Nested
    class MakeDialogVisibleTests {
        @Test
        void shouldInitializeComponentsWithSingleTaskValues() {
            //given
            taskEditingDialog.getTasksComboBox().addItem(originalTask.getTaskName());

            //when
            taskEditingDialog.makeDialogVisible(originalTask);

            //then
            assertAll(
                    () -> assertThat(taskEditingDialog.getTasksComboBox().getSelectedItem(), is(originalTask.getTaskName())),
                    () -> assertThat(taskEditingDialog.getDescriptionTextArea().getText(), is(originalTask.getDescription())),
                    () -> assertThat(taskEditingDialog.getWeekdaysComboBox().getSelectedItem(), is(originalTask.getWeekday().getWeekdayPL())),
                    () -> assertThat(new SimpleDateFormat("HH:mm").format((Date) taskEditingDialog.getTimeSpinner().getValue()), is(originalTask.getTime().toString())),
                    () -> assertThat((byte) taskEditingDialog.getPrioritySpinner().getValue(), is(originalTask.getPriority())),
                    () -> assertThat(taskEditingDialog.getStatusComboBox().getSelectedItem(), is(originalTask.isDone() ? "Tak" : "Nie"))
            );
        }

        @Test
        void shouldShowMessageDialogIfSingleTaskParamIsNull() {
            //given
            try (var dialogUtils = mockStatic(DialogUtils.class)) {
                //when
                taskEditingDialog.makeDialogVisible(null);

                //then
                dialogUtils.verify(() -> DialogUtils.showMessageDialog(anyString(), anyString()));
            }
        }
    }

    @Nested
    class GetAllTaskNames {
        @Test
        void shouldReturnEmptyListWhenThereIsNoTaskNameInTaskNamesComboBox() {
            //given+when
            var actualTaskNames = taskEditingDialog.getAllTaskNames();

            //then
            assertThat(actualTaskNames, is(notNullValue()));
            assertTrue(actualTaskNames.isEmpty());
        }

        @Test
        void shouldReturnCompleteListWhenThereAreTaskNamesInTaskNamesComboBox() {
            //given
            var expectedTaskNames = new ArrayList<>(List.of("taskName1", "taskName2", "taskName3"));
            var taskNamesComboBox = taskEditingDialog.getTasksComboBox();
            expectedTaskNames.forEach(taskNamesComboBox::addItem);

            //when
            var actualTaskNames = taskEditingDialog.getAllTaskNames();

            //then
            assertThat(actualTaskNames, is(expectedTaskNames));
        }
    }
}