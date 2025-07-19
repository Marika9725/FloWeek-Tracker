package com.floweektracker.model;

import com.floweektracker.view.TaskDialogView;
import lombok.*;

import java.util.*;

/**
 * Represents the days of the week, along with utility methods for day name retrieval.
 * <br><br>
 * ENUMS: {@link #MONDAY}, {@link #TUESDAY}, {@link #WEDNESDAY}, {@link #THURSDAY}, {@link #FRIDAY}, {@link #SATURDAY},
 * {@link #SUNDAY}
 * <br><br>
 * FIELDS: {@link #weekdayPL}, {@link #position}
 * <br><br>
 * GETTERS: {@link #getWeekdaysPL()}, {@link #getWeekdayAt(int)}, {@link #valueOfPL(String)},
 * {@link #getListedWeekdays()}
 */
@Getter
@AllArgsConstructor
public enum WeekDays {
    MONDAY("Poniedziałek", 0),
    TUESDAY("Wtorek", 1),
    WEDNESDAY("Środa", 2),
    THURSDAY("Czwartek", 3),
    FRIDAY("Piątek", 4),
    SATURDAY("Sobota", 5),
    SUNDAY("Niedziela", 6);

    /**
     * Contains a {@link String} representing the Polish name of the specified weekday.
     *
     * @see #WeekDays(String, int)
     * @see #getWeekdayPL()
     * @see #valueOfPL(String)
     * @see #getListedWeekdays()
     */
    final String weekdayPL;

    /**
     * Represents the position of the specified weekday in the week.
     *
     * @see #WeekDays(String, int)
     * @see #getWeekdayAt(int)
     */
    final int position;

    /**
     * @return an array of {@link String}s with Polish names of the weekdays
     * @see com.floweektracker.view.PlannerView
     * @see TaskDialogView
     */
    public static String[] getWeekdaysPL() {
        return Arrays.stream(WeekDays.values())
                .map(weekDay -> weekDay.weekdayPL)
                .toArray(String[]::new);
    }

    /**
     * @param position represents the specified weekday in the week
     * @return a {@link WeekDays} object by the specified position or null when position is out of range
     */
    public static WeekDays getWeekdayAt(int position) {
        return Arrays.stream(WeekDays.values())
                .filter(weekday -> weekday.getPosition() == position)
                .findFirst()
                .orElse(null);
    }

    /**
     * @param weekdayPL represents the polish name of the specified weekday
     * @return a {@link WeekDays} object by the specified polish name or null when weekdayPL is not found
     * @see com.floweektracker.service.CleanerService
     * @see com.floweektracker.service.PlannerService
     */
    public static WeekDays valueOfPL(String weekdayPL) {
        return Arrays.stream(WeekDays.values())
                .filter(weekday -> weekday.getWeekdayPL().equals(weekdayPL))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return a {@link List} of {@link WeekDays} objects
     * @see com.floweektracker.MainFrame
     * @see com.floweektracker.service.CleanerService
     * @see com.floweektracker.view.PlannerView
     * @see com.floweektracker.view.TaskAddingDialog
     * @see com.floweektracker.view.TaskEditingDialog
     */
    public static List<WeekDays> getListedWeekdays() {
        return Arrays.stream(WeekDays.values()).toList();
    }
}