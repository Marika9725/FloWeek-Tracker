package calendarManagement;

public enum WeekDays {
    MONDAY("PONIEDZIAŁEK"), TUESDAY("WTOREK"),
    WEDNESDAY("ŚRODA"), THURSDAY ("CZWARTEK"),
    FRIDAY("PIĄTEK"), SATURDAY("SOBOTA"),
    SUNDAY("NIEDZIELA");

    private final String weekDayPL;

    WeekDays(String weekDayInPolish) {
        this.weekDayPL = weekDayInPolish;
    }

    public String getWeekDayPL() {
        return weekDayPL;
    }

    public static String getWeekDayEN(String weekDayPL){
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
}
