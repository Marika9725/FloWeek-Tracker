package com.floweektracker.controller;

import com.floweektracker.TestHelper;
import com.floweektracker.service.TaskNamesService;
import com.floweektracker.view.*;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskNamesControllerTest {
    private final TaskNamesController taskNamesController = new TaskNamesController(
            new TaskAddingDialogController(new TaskAddingDialog()),
            new TaskEditingDialogController(new TaskEditingDialog())
    );
    private final TaskNamesDialog taskNamesDialog = taskNamesController.getTaskNamesDialog();
    private final JList<String> taskNamesList = taskNamesDialog.getTaskNamesList();
    private final JTextField taskNameInputField = taskNamesDialog.getTaskNameInputField();
    private final JButton[] buttons = taskNamesDialog.getButtons();
    private final TaskNamesService taskNamesService = taskNamesController.getTaskNamesService();

    @BeforeEach
    void setUp(TestInfo testInfo) {
        if(testInfo.getTags().contains("setNameToTaskNameInputField")) {
            taskNameInputField.setText("MyTaskName");
        }

        if(testInfo.getTags().contains("emptyTaskNamesList")) {
            taskNamesList.setListData(new String[0]);
        } else {
            taskNamesList.setListData(new String[]{"task1", "task2", "task3"});

            for (int i = 0; i < 3; i++) {
                taskNamesService.addTaskName(String.format("task%d", i));
            }
        }
    }

    @AfterEach
    void cleanUp() {
        taskNameInputField.setText("");

        for (var listSelectionListener : taskNamesList.getListSelectionListeners()) {
            taskNamesList.removeListSelectionListener(listSelectionListener);
        }

        for (JButton button : buttons) {
            for (ActionListener actionListener : button.getActionListeners()) {
                button.removeActionListener(actionListener);
            }
        }

        taskNamesList.clearSelection();
        taskNamesService.getTaskNames().clear();
    }

    @Nested
    class InitListenersTests {
        @Test
        void shouldAddListenerToTaskNamesList() {
            //given+when
            var listeners = taskNamesList.getListSelectionListeners();

            //then
            assertThat(listeners.length, is(1));
        }

        @Test
        void shouldAddListenerToButtons() {
            for (JButton button : buttons) {
                //given+when
                var listeners = button.getActionListeners();

                //then
                assertThat(listeners.length, is(1));
            }
        }
    }

    @Test
    void chooseTaskNameInTaskNamesListShouldSetThisTaskNameInTaskNameInputField() {
        //given
        var selectedValueBefore = taskNamesList.getSelectedValue();
        var taskNameInTaskNameInputFieldBefore = taskNameInputField.getText();

        //when
        taskNamesList.setSelectedIndex(0);

        var selectedValueAfter = taskNamesList.getSelectedValue();
        var taskNameInTaskNameInputFiledAfter = taskNameInputField.getText();

        //then
        assertAll(
                () -> assertThat(selectedValueBefore, is(nullValue())),
                () -> assertThat(taskNameInTaskNameInputFieldBefore, is("")),
                () -> assertThat(selectedValueAfter, is("task1")),
                () -> assertThat(taskNameInTaskNameInputFiledAfter, is("task1"))
        );
    }

    @Test
    void shouldRemoveTaskNameFromTaskNameInputFieldWhenCancelButtonIsClicked() {
        //given
        taskNameInputField.setText("task1");
        var valueBefore = taskNameInputField.getText();

        //when
        taskNamesDialog.getButtons()[2].doClick();
        var valueAfter = taskNameInputField.getText();

        //then
        assertAll(
                () -> assertThat(valueBefore, is("task1")),
                () -> assertThat(valueAfter, is(""))
        );
    }

    @Nested
    class ConfirmButtonTests {
        @Tag("setNameToTaskNameInputField")
        @Test
        void shouldAddTaskNameToTaskNamesListWhenConfirmButtonIsClicked() {
            //given
            var taskNamesNumBefore = taskNamesList.getModel().getSize();

            //when
            buttons[0].doClick();
            var taskNamesNumAfter = taskNamesList.getModel().getSize();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(3)),
                    () -> assertThat(taskNamesNumAfter, is(4))
            );
        }

        @Tag("setNameToTaskNameInputField")
        @Test
        void shouldAddTaskNameToTaskNamesServiceWhenConfirmButtonIsClicked() {
            //given
            var taskNamesNumBefore = taskNamesService.getTaskNames().size();

            //when
            buttons[0].doClick();
            var taskNamesNumAfter = taskNamesService.getTaskNames().size();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(3)),
                    () -> assertThat(taskNamesNumAfter, is(4)),
                    () -> assertTrue(taskNamesService.getTaskNames().contains("MyTaskName"))
            );
        }

        @Tag("emptyTaskNamesList")
        @Test
        void whenTaskNameIsEmptyNoTaskNamesShouldBeAddedToTaskNamesList() {
            //given
            var taskNamesNumBefore = taskNamesList.getModel().getSize();

            //when
            buttons[0].doClick();
            var taskNamesNumAfter = taskNamesList.getModel().getSize();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(0)),
                    () -> assertThat(taskNamesNumAfter, is(0))
            );
        }

        @Test
        void whenTaskNameIsEmptyNoTaskNamesShouldBeAddedToTaskNamesService() {
            //given
            var taskNamesNumBefore = taskNamesService.getTaskNames().size();

            //when
            buttons[0].doClick();
            var taskNamesNumAfter = taskNamesService.getTaskNames().size();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(3)),
                    () -> assertThat(taskNamesNumAfter, is(3))
            );
        }

        @Tag("setNameToTaskNameInputField")
        @Test
        void afterAddTaskTaskNameInputFieldShouldBeEmpty() {
            //given
            var valueBefore = taskNameInputField.getText();

            //when
            buttons[0].doClick();
            var valueAfter = taskNameInputField.getText();

            //then
            assertAll(
                    () -> assertThat(valueBefore, is("MyTaskName")),
                    () -> assertThat(valueAfter, is(""))
            );
        }
    }

    @Nested
    class DeleteButtonTests {
        @Test
        void shouldDeleteTaskNameFromTaskNamesListWhenDeleteButtonIsClicked() {
            //given
            var taskNamesNumBefore = taskNamesList.getModel().getSize();

            //when
            taskNameInputField.setText("task1");
            buttons[1].doClick();

            var taskNamesNumAfter = taskNamesList.getModel().getSize();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(3)),
                    () -> assertThat(taskNamesNumAfter, is(2))
            );
        }

        @Test
        void shouldDeleteTaskNameFromTaskNamesServiceWhenDeleteButtonIsClicked() {
            //given
            var taskNamesNumBefore = taskNamesService.getTaskNames().size();

            //when
            taskNameInputField.setText("task1");
            buttons[1].doClick();

            var taskNamesNumAfter = taskNamesService.getTaskNames().size();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(3)),
                    () -> assertThat(taskNamesNumAfter, is(2)),
                    () -> assertFalse(taskNamesService.getTaskNames().contains("task1"))
            );
        }

        @Test
        void whenTaskNameIsNotSelectedNoTaskNameShouldBeDeletedFromTaskNamesList() {
            //given
            var taskNamesNumBefore = taskNamesList.getModel().getSize();

            //when
            buttons[1].doClick();

            var taskNamesNumAfter = taskNamesList.getModel().getSize();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(3)),
                    () -> assertThat(taskNamesNumAfter, is(3))
            );
        }

        @Test
        void whenTaskNameIsNotSelectedNoTaskNameShouldBeDeletedFromTaskNamesService() {
            //given
            var taskNamesNumBefore = taskNamesService.getTaskNames().size();

            //when
            buttons[1].doClick();

            var taskNamesNumAfter = taskNamesService.getTaskNames().size();

            //then
            assertAll(
                    () -> assertThat(taskNamesNumBefore, is(3)),
                    () -> assertThat(taskNamesNumAfter, is(3))
            );
        }

        @Test
        void afterDeleteTaskTaskNameInputFieldShouldBeEmpty() {
            //given
            taskNameInputField.setText("task1");
            var valueBefore = taskNameInputField.getText();

            //when
            buttons[1].doClick();
            var valueAfter = taskNameInputField.getText();

            //then
            assertAll(
                    () -> assertThat(valueBefore, is("task1")),
                    () -> assertThat(valueAfter, is(""))
            );
        }
    }

    @Tag("emptyTaskNamesList") @Test
    void tasksInScrollPaneShouldBeSameAsInTaskNamesService() {
        //given
        var taskNames = new String[]{"task1", "task2", "task3"};
        var confirmButton = taskNamesDialog.getButtons()[0];

        for (String taskName : taskNames) {
            taskNameInputField.setText(taskName);
            confirmButton.doClick();
        }

        var modelTaskNamesList = taskNamesList.getModel();
        var expectedTaskNames = IntStream.range(0, modelTaskNamesList.getSize())
                .mapToObj(modelTaskNamesList::getElementAt)
                .toArray(String[]::new);

        //when
        var actualTaskNames = taskNamesService.getTaskNames().toArray(String[]::new);

        //then
        assertThat(actualTaskNames, equalTo(expectedTaskNames));
    }
}