package com.floweektracker.tasksDatabase;

import java.util.Comparator;

/**
 * Representing a {@link Comparator<String>} implementation used by the {@link TasksDatabaseManager#tasksList}. This
 * class implements the {@link #compare(String, String)} method.
 */
class TasksListComparator implements Comparator<String> {
    /**
     * Compares {@code task1} and {@code task2} ignoring case considerations.
     *
     * @param task1 a {@link String} representing the first task name to be compared.
     * @param task2 a {@link String} representing the second task name to be compared.
     * @return a negative integer, zero, or a positive integer as the specified String is greater than, equal to, or
     * less than this String, ignoring case consideration.
     */
    @Override
    public int compare(String task1, String task2) {
        return task1.compareToIgnoreCase(task2);
    }
}