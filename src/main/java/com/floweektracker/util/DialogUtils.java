package com.floweektracker.util;

import com.floweektracker.model.SingleTask;
import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.util.function.*;

/**
 * Represents class that provides utility methods for all classes.
 * <br><br>
 * Methods: {@link #showMessageDialog(String, String)}, {@link #rollback(SingleTask, Consumer, Function)}
 */
@UtilityClass
public class DialogUtils {
    /**
     * Displays message dialog to the user with given title and message. It breaks if title or message are null or
     * empty.
     *
     * @param title   a given title of the dialog
     * @param message a given message of the dialog
     */
    public static void showMessageDialog(String title, String message) {
        if (title == null || title.isBlank() || message == null || message.isBlank()) return;

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Rollbacks action to the previous state of the planner, schedule or weekday planner.
     *
     * @param task          a task to rollback
     * @param action        an action to make rollback
     * @param checkerAction a checker if rollback was successful
     * @return true if rollback was successful, otherwise false
     */
    public static boolean rollback(SingleTask task, Consumer<SingleTask> action, Function<SingleTask, Boolean> checkerAction) {
        try {
            action.accept(task);
        } catch (Exception e) {
            DialogUtils.showMessageDialog("Niepowodzenie", "Nie udało się cofnąć operacji dla: " + task);
            return false;
        }

        return checkerAction.apply(task);
    }
}
