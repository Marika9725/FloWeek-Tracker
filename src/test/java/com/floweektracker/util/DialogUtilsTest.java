package com.floweektracker.util;

import com.floweektracker.TestHelper;
import com.floweektracker.model.SingleTask;
import com.floweektracker.service.TasksService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DialogUtilsTest {
    @AfterEach
    void cleanUpAfterEach() throws InterruptedException {
        var windows = Window.getWindows();
        for (Window window : windows) {
            if (window.isDisplayable()) window.dispose();
        }
    }

    @Nested
    class ShowMessageDialogTests {
        @Test
        void shouldShowInfoDialogToUser() {
            //given
            try (var optionPane = mockStatic(JOptionPane.class)) {
                //when
                DialogUtils.showMessageDialog("title", "message");

                //then
                optionPane.verify(() -> JOptionPane.showMessageDialog(null, "message", "title", JOptionPane.ERROR_MESSAGE), times(1));
            }
        }

        @ParameterizedTest
        @MethodSource("createValidParamsToShowMessageDialog")
        void shouldNotShowInfoDialogIfParamsAreValid(String title, String message) {
            //given
            try (var optionPane = mockStatic(JOptionPane.class)) {

                //when
                DialogUtils.showMessageDialog(title, message);

                //then
                optionPane.verify(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE), never());
            }
        }

        private static Stream<Arguments> createValidParamsToShowMessageDialog() {
            return Stream.of(
                    Arguments.of(null, "message"),
                    Arguments.of("title", null),
                    Arguments.of("", "message"),
                    Arguments.of("title", ""),
                    Arguments.of(" ", "message"),
                    Arguments.of("title", " ")
            );
        }
    }

    @Nested
    class RollbackTests {
        private final TestHelper helper = new TestHelper();
        private final SingleTask baseTask = helper.createBaseTask();
        private final TasksService tasksService = TasksService.getService();

        @Test
        void shouldRollbackAddingTaskToTheSchedule() {
            //given
            tasksService.addTask(baseTask);
            var rollbackAction = (Consumer<SingleTask>) tasksService::deleteTask;
            var checkerAction = (Function<SingleTask, Boolean>) tasksService::isTaskInSchedule;

            //when
            var isRollbackSuccessful = !DialogUtils.rollback(baseTask, rollbackAction, checkerAction);

            //then
            assertTrue(isRollbackSuccessful);
        }

        @Test
        void shouldShowMessageDialogWhenRollbackAddingTaskIsNotSuccessful() {
            //given
            var rollbackAction = (Consumer<SingleTask>)_ -> {throw new RuntimeException("test");};
            var checkerAction = (Function<SingleTask, Boolean>) _ -> false;

            try(var messageDialog = mockStatic(JOptionPane.class)) {
                //when
                var isRollbackSuccessful = DialogUtils.rollback(baseTask, rollbackAction, checkerAction);

                //then
                messageDialog.verify(() -> JOptionPane.showMessageDialog(any(), anyString(), anyString(), eq(JOptionPane.ERROR_MESSAGE)), times(1));
                assertFalse(isRollbackSuccessful);
            }
        }
    }
}
