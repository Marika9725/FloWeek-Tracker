package com.floweektracker.model;

import lombok.*;

/**
 * Represents the names of the panels used in the application.
 * <br><br>
 * ENUMS: {@link #BUTTONS_PANEL}, {@link #DESCRIPTION_PANEL}, {@link #PRIORITY_PANEL}, {@link #TIME_PANEL},
 * {@link #PLAIN}, {@link #WEEKDAYS_PANEL}, {@link #TASKS_PANEL}, {@link #STATUS_PANEL}
 * <br><br>
 * FIELDS: {@link #panelName}
 */
@Getter
@AllArgsConstructor
public enum PanelNames {
    BUTTONS_PANEL("buttonsPanel"),
    DESCRIPTION_PANEL("descriptionPanel"),
    PRIORITY_PANEL("priorityPanel"),
    TIME_PANEL("timePanel"),
    PLAIN("plain"),
    WEEKDAYS_PANEL("weekdaysPanel"),
    TASKS_PANEL("tasksPanel"),
    STATUS_PANEL("statusPanel");

    /**
     * Contains a {@link String} representing the name of a panel.
     */
    private final String panelName;
}



