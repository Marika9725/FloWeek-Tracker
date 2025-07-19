package com.floweektracker.view;

import com.floweektracker.TestHelper;
import com.floweektracker.controller.TaskNamesController;
import com.floweektracker.service.TaskNamesService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskNamesDialogTest {
    private final TestHelper helper = new TestHelper();
    private final TaskNamesDialog taskNamesDialogView = TaskNamesDialog.getView();
    private final JDialog taskNamesDialog = taskNamesDialogView.getDialog();
    private JPanel panel;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        testInfo.getTags().stream().findFirst().ifPresent(this::initPanel);
    }

    private void initPanel(String tag) {
        switch (tag) {
            case "leftPanel" -> panel = (JPanel) helper.findComponent("leftPanel", taskNamesDialog.getContentPane());
            case "rightPanel" -> panel = (JPanel) helper.findComponent("rightPanel", taskNamesDialog.getContentPane());
        }
    }

    @AfterEach
    void tearDown() {
        this.panel = null;
    }

    @Nested
    class ConstructorTests {
        @Test
        void shouldReturnSameInstances() {
            assertThat(TaskNamesDialog.getView().getDialog(), sameInstance(TaskNamesDialog.getView().getDialog()));
        }

        @Test
        void constructorShouldInitTaskNamesDialog() {
            assertAll(
                    () -> assertThat(taskNamesDialog.getName(), is("taskNamesDialog")),
                    () -> assertThat(taskNamesDialog.getTitle(), is("Dodaj zadanie do bazy")),
                    () -> assertTrue(taskNamesDialog.isModal())
            );
        }
    }

    @Tag("leftPanel")
    @Nested
    class LeftPanelTests {
        @Test
        void dialogShouldContainsLeftPanel() {
            assertThat(panel, notNullValue());
            checkReturnedValue(panel, JPanel.class, "leftPanel");
            assertThat(panel.getLayout().getClass(), is(sameInstance(BoxLayout.class)));
        }

        @Test
        void leftPanelShouldContainsTitleLabel() {
            //given+when
            var titleLabel = (JLabel) helper.findComponent("titleLabel", panel);

            //then
            assertThat(titleLabel, notNullValue());
            checkReturnedValue(titleLabel, JLabel.class, "titleLabel");
            assertThat(titleLabel.getText(), is("Dostępne zadania:"));
        }

        @Test
        void leftPanelShouldContainsScrollPaneWithTaskNames() {
            //given+when
            var scrollPane = (JScrollPane) helper.findComponent("taskNamesScrollPane", panel);

            //then
            assertThat(scrollPane, notNullValue());
            checkReturnedValue(scrollPane, JScrollPane.class, "taskNamesScrollPane");
        }
    }

    @Tag("rightPanel")
    @Nested
    class RightPanelTests {
        @Test
        void shouldContainsRightPanel() {
            assertThat(panel, notNullValue());
            checkReturnedValue(panel, JPanel.class, "rightPanel");
            assertThat(panel.getLayout().getClass(), is(sameInstance(BoxLayout.class)));
        }

        @ParameterizedTest
        @CsvSource({"taskAddingPanel", "buttonsPanel"})
        void rightPanelShouldContainsInnerPanels(String panelName) {
            //given+when
            var innerPanel = (JPanel) helper.findComponent(panelName, panel);

            //then
            assertThat(innerPanel, notNullValue());
            checkReturnedValue(innerPanel, JPanel.class, panelName);
            assertThat(innerPanel.getLayout().getClass(), is(sameInstance(FlowLayout.class)));
        }

        @Test
        void taskAddingPanelShouldContainsInformLabel() {
            //given
            var taskAddingPanel = (JPanel) helper.findComponent("taskAddingPanel", panel);

            //when
            var informLabel = (JLabel) helper.findComponent("informLabel", Objects.requireNonNull(taskAddingPanel));

            //then
            assertThat(informLabel, notNullValue());
            checkReturnedValue(informLabel, JLabel.class, "informLabel");
            assertThat(informLabel.getText(), is("Zadanie:"));
        }

        @Test
        void taskAddingPanelShouldContainsTextField() {
            //given
            var taskAddingPanel = (JPanel) helper.findComponent("taskAddingPanel", panel);

            //when
            var taskNameInputField = (JTextField) helper.findComponent("taskNameInputField", Objects.requireNonNull(taskAddingPanel));

            //then
            assertThat(taskNameInputField, notNullValue());
            checkReturnedValue(taskNameInputField, JTextField.class, "taskNameInputField");
            assertThat(taskNameInputField.getColumns(), is(32));
        }

        @ParameterizedTest
        @CsvSource({"confirmButton, OK", "deleteButton, Usuń", "cancelButton, Wyczyść"})
        void buttonsPanelShouldContainsButtons(String buttonName, String buttonText) {
            //given
            var buttonsPanel = (JPanel) helper.findComponent("buttonsPanel", panel);

            //when
            var button = (JButton) helper.findComponent(buttonName, Objects.requireNonNull(buttonsPanel));

            //then
            assertThat(button, notNullValue());
            checkReturnedValue(button, JButton.class, buttonName);
            assertThat(button.getText(), is(buttonText));
        }
    }

    @Nested
    class GetTaskNameFromInputFieldTests {
        @Tag("rightPanel")
        @Test
        void shouldReturnCorrectTaskNameFromInputField() {
            //given
            var taskAddingPanel = (JPanel) helper.findComponent("taskAddingPanel", panel);
            var taskNameInputField = (JTextField) helper.findComponent("taskNameInputField", Objects.requireNonNull(taskAddingPanel));
            Objects.requireNonNull(taskNameInputField).setText("Test task");

            //when
            var actualTaskName = taskNamesDialogView.getTaskNameFromInputField();

            //then
            assertThat(actualTaskName, is("Test task"));
        }

        @Tag("rightPanel")
        @Test
        void shouldReturnNullWhenTaskNameInInputFieldIsEmpty() {
            //given
            var taskAddingPanel = (JPanel) helper.findComponent("taskAddingPanel", panel);
            var taskNameInputField = (JTextField) helper.findComponent("taskNameInputField", Objects.requireNonNull(taskAddingPanel));
            Objects.requireNonNull(taskNameInputField).setText("");

            //when
            var actualTaskName = taskNamesDialogView.getTaskNameFromInputField();

            //then
            assertThat(actualTaskName, is(nullValue()));

        }
    }

    @Tag("rightPanel")
    @Test
    void shouldSetTaskNameInInputField() {
        //given
        var taskAddingPanel = (JPanel) helper.findComponent("taskAddingPanel", panel);
        var taskNameInputField = (JTextField) helper.findComponent("taskNameInputField", Objects.requireNonNull(taskAddingPanel));
        var expectedTaskName = "test";

        //when
        taskNamesDialogView.setTaskNameInInputField(expectedTaskName);
        var actualTaskName = Objects.requireNonNull(taskNameInputField).getText();

        //then
        assertThat(actualTaskName, is(expectedTaskName));
    }

    @Tag("leftPanel")
    @Test
    void shouldSetTaskNamesInList() {
        //given
        var taskNamesList = taskNamesDialogView.getTaskNamesList();
        var expectedTaskNames = new String[]{"Task1", "Task2", "Task3"};

        //when
        taskNamesDialogView.setTaskNamesList(expectedTaskNames);

        var actualTaskNames = IntStream.range(0, taskNamesList.getModel().getSize())
                .mapToObj(i -> taskNamesList.getModel().getElementAt(i))
                .toArray(String[]::new);

        //then
        assertThat(actualTaskNames, equalTo(expectedTaskNames));
    }

    @Nested
    class GetSelectedTaskNameFromTaskNamesListTests {
        @Tag("rightPanel")
        @Test
        void shouldReturnValueWhenTaskNameIsSelected() {
            //given
            var taskNamesList = taskNamesDialogView.getTaskNamesList();
            var expectedTaskName = "Task2";

            taskNamesDialogView.setTaskNamesList(new String[]{"Task1", "Task2", "Task3"});
            taskNamesList.setSelectedIndex(1);

            //when
            var actualTaskName = taskNamesDialogView.getSelectedTaskNameFromTaskNamesList();

            //then
            assertThat(actualTaskName, is(expectedTaskName));
        }

        @Tag("rightPanel")
        @Test
        void shouldReturnNullWhenTaskNameIsNotSelected() {
            //given
            taskNamesDialogView.setTaskNamesList(new String[]{"Task1", "Task2", "Task3"});

            //when
            var actualTaskName = taskNamesDialogView.getSelectedTaskNameFromTaskNamesList();

            //then
            assertThat(actualTaskName, is(nullValue()));
        }
    }

    private void checkReturnedValue(Component component, Class<?> expectedType, String expectedName) {
        assertAll(
                () -> assertThat(component.getName(), is(expectedName)),
                () -> assertThat(component.getClass(), is(sameInstance(expectedType)))
        );
    }
}