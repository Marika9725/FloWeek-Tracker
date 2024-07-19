package com.floweektracker.main;

/**
 * Represents the text displayed on {@link javax.swing.JButton}s in the main panel.
 * <br><br>
 * ENUMS: {@link #ADD_TASK_PLANNER}, {@link #ADD_TASK_DATABASE}, {@link #DELETE_TASK}, {@link #CLEAN_PLANNER},
 * {@link #RESET_POINTS}, {@link #RETURN}
 * <br><br>
 * FIELDS:
 * <ul><li>{@link #actionPL} - a {@link String} representing the Polish name of the button text</li></ul>
 * GETTERS:
 * <ul><li>{@link #getActionPL()} ()} - returns a {@link String} representing the Polish button text</li></ul>
 */

enum ButtonsText {
    //region ENUMS
    ADD_TASK_PLANNER("Dodaj zadanie do planera"),
    ADD_TASK_DATABASE("Dodaj zadanie do bazy"),
    DELETE_TASK("Usuń zadanie"),
    CLEAN_PLANNER("Wyczyść planer"),
    RESET_POINTS("Wyzeruj punkty"),
    RETURN("Powrót");
    //endregion

    /**
     * Contains a {@link String} representing the Polish name of the button text displayed on the
     * {@link javax.swing.JButton}s in the main panel.
     *
     * @see #ButtonsText(String)
     * @see #getActionPL()
     */
    private final String actionPL;

    /**
     * Constructs a {@link ButtonsText} enum with the specified Polish name of the button text.
     *
     * @param actionPL a Polish name of the enum
     * @see #ADD_TASK_PLANNER
     * @see #ADD_TASK_DATABASE
     * @see #DELETE_TASK
     * @see #CLEAN_PLANNER
     * @see #RESET_POINTS
     * @see #RETURN
     */
    ButtonsText(String actionPL) {this.actionPL = actionPL;}

    //region Getters

    /**
     * @return a {@link String} representing the polish name of the button text
     * @see MainPanelUI#MainPanelUI()
     */
    String getActionPL() {return actionPL;}
    //endregion
}
