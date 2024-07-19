package com.floweektracker.planner;

import com.floweektracker.planner.plannerManagerDialogs.TaskAddingDialogUI;

import java.util.*;

/**
 * Represents the days of the week, along with utility methods for day name retrieval.
 * <br><br>
 * ENUMS: {@link #MONDAY}, {@link #TUESDAY}, {@link #WEDNESDAY}, {@link #THURSDAY}, {@link #FRIDAY}, {@link #SATURDAY},
 * {@link #SUNDAY}
 * <br><br>
 * FIELDS:
 * <ul><li>{@link #weekDayPL} - a {@link String} representing the Polish name of the specified weekday</li></ul>
 * GETTERS:
 * <ul>
 *     <li>{@link #getWeekDayEN(String)} - returns the English name of the given Polish name of the weekday</li>
 *     <li>{@link #getWeekDayPL()} - returns the Polish name of the weekday</li>
 *     <li>{@link #getWeekDaysPL()} - returns an array of {@link String}s with Polish names of the weekdays</li>
 * </ul>
 */
public enum WeekDays {
    //region ENUMS
    MONDAY("PONIEDZIAŁEK"), TUESDAY("WTOREK"),
    WEDNESDAY("ŚRODA"), THURSDAY("CZWARTEK"),
    FRIDAY("PIĄTEK"), SATURDAY("SOBOTA"),
    SUNDAY("NIEDZIELA");
    //endregion

    /**
     * Contains a {@link String} representing the Polish name of the weekday.
     *
     * @see #WeekDays(String)
     * @see #getWeekDayPL()
     * @see #getWeekDaysPL()
     */
    private final String weekDayPL;

    /**
     * Constructs a {@link WeekDays} enum with the specified Polish name.
     *
     * @param weekDayPL a {@link String} representing the Polish name of the specified weekday
     * @see #MONDAY
     * @see #TUESDAY
     * @see #WEDNESDAY
     * @see #THURSDAY
     * @see #FRIDAY
     * @see #SATURDAY
     * @see #SUNDAY
     */
    WeekDays(String weekDayPL) {this.weekDayPL = weekDayPL;}

    //region Getters

    /**
     * @return a {@link String} representing the Polish name of the specified weekday.
     * @see com.floweektracker.main.MainListeners#cleanPlanner(String)
     * @see PlannerDataManager#cleanPlanner(HashSet, String)
     * @see WeekDayPanelUI
     * @see WeekDayPanelUI#createHeadlinePanel()
     * @see TaskAddingDialogUI#createWeekDaySelectionPanel()
     */
    public String getWeekDayPL() {return weekDayPL;}

    /**
     * @return a {@link String} array containing the Polish names of the weekdays
     * @see PlannerTableManager#createPlannerModel()
     * @see TaskAddingDialogUI
     */
    public static String[] getWeekDaysPL() {
        return Arrays.stream(WeekDays.values())
                .map(weekDay -> weekDay.weekDayPL)
                .toArray(String[]::new);
    }

    /**
     * @param weekDayPL a {@link String} representing the Polish name of the weekday.
     * @return a {@link String} representing the English name corresponding to the {@code weekDayPL} parameter
     */
    public static String getWeekDayEN(String weekDayPL) {
        return switch (weekDayPL) {
            case "WTOREK" -> TUESDAY.name();
            case "ŚRODA" -> WEDNESDAY.name();
            case "CZWARTEK" -> THURSDAY.name();
            case "PIĄTEK" -> FRIDAY.name();
            case "SOBOTA" -> SATURDAY.name();
            case "NIEDZIELA" -> SUNDAY.name();
            default -> MONDAY.name();
        };
    }
    //endregion
}