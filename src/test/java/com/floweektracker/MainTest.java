package com.floweektracker;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MainTest {
    @AfterEach
    void cleanUpAfterEach() {
        var windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog dialog && dialog.isVisible()) {
                if (dialog.getContentPane().getComponentCount() > 0 && dialog.getContentPane().getComponent(0) instanceof JOptionPane) {
                    dialog.dispose(); // zamyka okno
                }
            }
        }
    }

    public boolean checkVisibility(String dialogName) {
        var windowStream = Arrays.stream(Window.getWindows())
                .filter(Window::isVisible);

        if (dialogName != null) {
            windowStream = windowStream.filter(window -> window.getName().equalsIgnoreCase(dialogName));
        }

        return windowStream
                .filter(JDialog.class::isInstance)
                .map(JDialog.class::cast)
                .flatMap(dialog -> Arrays.stream(dialog.getContentPane().getComponents()))
                .anyMatch(JOptionPane.class::isInstance);
    }

    @Test
    void shouldOpenConfirmDialogWhenArgumentIsTrue() {
        //given + when
        SwingUtilities.invokeLater(() -> Main.main(new String[]{"true"}));

        //then
        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> checkVisibility(null));
    }

    @Test
    void shouldNotOpenConfirmDialogWhenArgumentIsFalse() {
        //given + when
        SwingUtilities.invokeLater(() -> Main.main(new String[]{"false"}));

        //then
        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> !checkVisibility(null));
    }

    @Test
    void shouldNotOpenConfirmDialogWhenNoArgumentsAreProvided() {
        //given + when
        SwingUtilities.invokeLater(() -> Main.main(new String[]{}));

        //then
        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> !checkVisibility(null));
    }

    @Test
    void shouldNotOpenConfirmDialogWhenArgumentIsNotBoolean() {
        //given + when
        SwingUtilities.invokeLater(() -> Main.main(new String[]{"test"}));

        //then
        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> !checkVisibility(null));
    }

    @Test
    void shouldNotOpenConfirmDialogWhenArgumentIsNull() {
        //given + when
        Main.main(null);

        //then
        assertFalse(checkVisibility(null));
    }
}