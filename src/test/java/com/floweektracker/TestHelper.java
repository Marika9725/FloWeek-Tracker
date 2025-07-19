package com.floweektracker;

import com.floweektracker.model.*;
import org.jetbrains.annotations.*;
import org.junit.jupiter.params.provider.Arguments;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestHelper {
    public SingleTask createBaseTask() {
        return new SingleTask(
                "baseTask",
                "description",
                LocalTime.of(12, 0),
                false,
                WeekDays.MONDAY,
                (byte) 5
        );
    }

    public SingleTask createEditedTask() {
        return new SingleTask(
                "baseTask",
                "description2",
                LocalTime.of(12, 0),
                true,
                WeekDays.TUESDAY,
                (byte) 5
        );
    }

    public void checkVisibility(JDialog dialog) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

                assertTrue(dialog.isVisible());
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    @Nullable
    public Component findComponent(String name, @NotNull Container container) {
        return Arrays.stream(container.getComponents())
                .filter(comp -> (comp.getName() != null) && (comp.getName().contains(name)))
                .findFirst()
                .orElse(null);
    }

    public <T> void checkReturnedValue(T actualValue, Class<T> expectedType, T expectedValue) {
        assertAll(
                () -> assertThat(actualValue, is(notNullValue())),
                () -> assertThat(actualValue, instanceOf(expectedType)),
                () -> assertThat(actualValue, is(expectedValue))
        );
    }

    public SingleTask[] prepareSingleTasks() {
        var tasks = new SingleTask[]{createBaseTask(), createBaseTask(), createBaseTask()};
        tasks[1].setTime(LocalTime.of(15, 45));
        tasks[2].setTime(LocalTime.of(9, 30));

        return tasks;
    }

    public boolean checkVisibility(String dialogName, Class<?> objectClass) {
        var windowStream = Arrays.stream(Window.getWindows())
                .filter(Window::isVisible);

        if (dialogName != null) {
            windowStream = windowStream.filter(window -> window.getName().equalsIgnoreCase(dialogName));
        }

        return switch (objectClass.getSimpleName()) {
            case "JDialog" -> windowStream.anyMatch(JDialog.class::isInstance);
            case "JOptionPane" -> windowStream
                    .filter(JDialog.class::isInstance)
                    .map(JDialog.class::cast)
                    .flatMap(dialog -> Arrays.stream(dialog.getContentPane().getComponents()))
                    .anyMatch(JOptionPane.class::isInstance);
            default -> false;
        };
    }

    public void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) return;
        var files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) deleteDirectory(file);
                else if (file.isFile()) file.delete();
            }
        }

        directory.delete();
    }

    /*
    public TaskAddingDialog createTaskAddingDialog() {
        return new TaskAddingDialog();
    }

    public TaskAddingDialogController createTaskAddingDialogController(TaskAddingDialog taskAddingDialog) {
        return new TaskAddingDialogController(taskAddingDialog);
    }

    public TaskEditingDialog createTaskEditingDialog() {
        return new TaskEditingDialog();
    }

    public TaskEditingDialogController createTaskEditingDialogController(TaskEditingDialog taskEditingDialog) {
        return new TaskEditingDialogController(taskEditingDialog);
    }*/

    private static Stream<Arguments> createValidCellPoint() {
        return Stream.concat(
                Stream.of(0, 2).flatMap(row -> Stream.of(0, 1, 2, 3, 4, 5, 6, 7).map(col -> Arguments.of(row, col))),
                Stream.of(
                        Arguments.of(1, 0),
                        Arguments.of(1, 2)
                )
        );
    }
}
