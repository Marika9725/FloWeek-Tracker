package com.floweektracker.main;

import com.floweektracker.planner.*;
import com.floweektracker.tasksDatabase.TasksDatabaseUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents the UI handling for instances of {@link Main}, {@link MainPanelUI} and {@link InfoPanelUI}, managing user
 * interactions and events within the panels.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #main} - a {@link Main} reference to the main application entry point</li>
 *     <li>{@link #mainPanelUI} - a {@link MainPanelUI} reference to the UI of the main panel</li>
 *     <li>{@link #mainCardsLayout} - a {@link CardLayout} that contains all panels added to {@code mainCards}
 *     from {@link #main}</li>
 *     <li>{@link #plannerTableManager} - a reference to a new {@link PlannerTableManager} instance which contains the
 *     {@link javax.swing.table.DefaultTableModel} for the week planner and manages its data and updates</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #setupListeners()} - adds listeners to the components from {@link #mainPanelUI} and
 *     {@link #infoPanelsUI}</li>
 *     <li>{@link #createButtonsListener()} - creates an {@link ActionListener} for {@link JButton}s from
 *     {@link #mainPanelUI} and {@link #infoPanelsUI}</li>
 *     <li>{@link #createPlannerListener()} - creates a {@link MouseAdapter} for the {@link JTable} representing week
 *     planner</li>
 *     <li>{@link #configureWindowsAdapter()} - sets the app's behaviour after closing it</li>
 *     <li>{@link #deleteTask()} - deletes a task from the week planner</li>
 *     <li>{@link #cleanPlanner(String)} - deletes all tasks or resets all points from the selected week day(s)</li>
 *     <li>{@link #showInfo()} - allows users to select kind of information shown in the app interface</li>
 *     <li>{@link #showPanel(String)} - shows a panel from {@link #mainCardsLayout} which name is compatible with a
 *     passed {@link String}</li>
 *     <li>{@link #refreshApp()} - reloads and updates the application after changing the week planner in the
 *     {@link JTable}
 * </ul>
 */
class MainListeners {
    //region Fields
    /**
     * Contains a {@link Main} reference provided via the {@code main} parameter. This reference is utilized for various
     * interactions and operations between this class and the Main module, such as accessing methods, properties, or
     * data related to the Main module.
     *
     * @see #MainListeners(Main, MainPanelUI, InfoPanelUI[])
     * @see #setupListeners()
     * @see #createPlannerListener()
     * @see #configureWindowsAdapter()
     * @see #showInfo()
     * @see #showPanel(String)
     * @see #refreshApp
     */
    private final Main main;
    /**
     * Contains a {@link MainPanelUI} reference provided via the {@code mainPanelUI} parameter. This reference is
     * utilized for various interactions and operations between this class and the MainPanelUI module, such as accessing
     * methods, properties, or data related to the MainPanelUI module.
     *
     * @see #MainListeners(Main, MainPanelUI, InfoPanelUI[])
     * @see #setupListeners()
     * @see #deleteTask()
     */
    private final MainPanelUI mainPanelUI;
    /**
     * Contains an array of {@link InfoPanelUI} references via the {@code infoPanelsUI} parameter. This reference is
     * utilized for various interactions and operations between this class and the InfoPanelUI module, such as accessing
     * methods, properties, or data related to the InfoPanelUI module.
     *
     * @see #MainListeners(Main, MainPanelUI, InfoPanelUI[])
     * @see #setupListeners()
     */
    private final InfoPanelUI[] infoPanelsUI;
    /**
     * Contains a {@link CardLayout} reference from {@link #main} field which allows switching between panels, which are
     * shown to the user.
     *
     * @see #MainListeners(Main, MainPanelUI, InfoPanelUI[])
     * @see #showPanel(String)
     */
    private final CardLayout mainCardsLayout;
    /**
     * Contains a {@link PlannerTableManager} reference via the {@link #mainPanelUI}. This reference is utilized for
     * various interactions and operations between this class and the planner from {@link #mainPanelUI}, such as
     * accessing methods, properties, or data.
     *
     * @see #MainListeners(Main, MainPanelUI, InfoPanelUI[])
     * @see #createButtonsListener()
     * @see #createPlannerListener()
     * @see #configureWindowsAdapter()
     * @see #deleteTask()
     * @see #cleanPlanner(String)
     * @see #refreshApp()
     */
    private final PlannerTableManager plannerTableManager;
    //endregion

    /**
     * Constructs a new {@link MainListeners} instance with the specified parameters. Assigns passed instances to
     * references: {@link #main}, {@link #mainPanelUI} and {@link #infoPanelsUI}. Retrieves the {@link CardLayout}
     * instance from the {@link com.floweektracker.main} object and assigns it to the {@link #mainCardsLayout}
     * reference. Also retrieves the {@link PlannerTableManager} instance from {@link #mainPanelUI} and assigns it to
     * the {@link #plannerTableManager} reference.
     * <br><br>
     * At the end, calls {@link #setupListeners()} to add listeners to the components.
     *
     * @param main         an instance of {@link Main}
     * @param mainPanelUI  an instance of {@link MainPanelUI}
     * @param infoPanelsUI an array of {@link InfoPanelUI} instances
     */
    MainListeners(Main main, MainPanelUI mainPanelUI, InfoPanelUI[] infoPanelsUI) {
        this.main = main;
        this.mainPanelUI = mainPanelUI;
        this.infoPanelsUI = infoPanelsUI;
        this.mainCardsLayout = (CardLayout) main.getMainCards().getLayout();
        this.plannerTableManager = mainPanelUI.getPlannerTableManager();

        setupListeners();
    }

    //region Methods

    /**
     * Adds listeners to the components from {@link MainPanelUI} and {@link InfoPanelUI} instances. It also adds
     * listener to the app's window.
     *
     * @see #MainListeners(Main, MainPanelUI, InfoPanelUI[])
     * @see MainListeners#createButtonsListener()
     * @see MainListeners#createPlannerListener
     * @see MainListeners#configureWindowsAdapter()
     */
    private void setupListeners() {
        mainPanelUI.getInfoButton().addActionListener(createButtonsListener());
        Arrays.stream(mainPanelUI.getButtons()).forEach(button -> button.addActionListener(createButtonsListener()));
        Arrays.stream(infoPanelsUI).forEach(infoPanelUI -> infoPanelUI.getReturnButton().addActionListener(createButtonsListener()));
        mainPanelUI.getPlanner().addMouseListener(createPlannerListener());
        main.addWindowListener(configureWindowsAdapter());
    }

    /**
     * <p>Creates and returns an {@link ActionListener} for buttons. Depending on the clicked button, the listener:</p>
     * <ul>
     *     <li>adds a task to the {@code planner} by invoking the {@link PlannerTableManager#addTask()} and the {@link #refreshApp()}
     *     method,</li>
     *     <li>creates a new {@link TasksDatabaseUI} instance for adding a task to the database,</li>
     *     <li>deletes a task from the {@code planner} by invoking the {@link #deleteTask()} method,</li>
     *     <li>deletes all tasks or resets all points from selected week day(s) by invoking
     *     {@link #cleanPlanner(String)} method,</li>
     *     <li>shows to the user one of the two kinds of information about application by invoking {@link #showInfo()}
     *     method,</li>
     *     <li>shows a main panel from {@link #mainCardsLayout} instance to the user by invoking
     *     {@link #showPanel(String)}method.</li>
     * </ul>
     *
     * @return an {@link ActionListener} for buttons from {@link MainPanelUI} and {@link InfoPanelUI} instances.
     * @see MainListeners#setupListeners()
     */
    private ActionListener createButtonsListener() {
        return buttonEvent -> {
            switch (buttonEvent.getActionCommand()) {
                case "Dodaj zadanie do planera" -> {if (plannerTableManager.addTask()) refreshApp();}
                case "Dodaj zadanie do bazy" -> new TasksDatabaseUI();
                case "Usuń zadanie" -> deleteTask();
                case "Wyczyść planer", "Wyzeruj punkty" -> cleanPlanner(buttonEvent.getActionCommand());
                case "Info" -> showInfo();
                case "Powrót" -> showPanel("MainPanel");
            }
        };
    }

    /**
     * Creates and returns a {@link MouseListener} for the {@code planner}. The listener handles user clicks on the week
     * planner.
     * <ul>
     *     <li>If user double-clicks on a weekday name, a new {@link WeekDayPanelUI} instance is created for the
     *     selected weekday, and the weekday panel from this instance is added to {@code MainCards} in {@link #main}.
     *     At the end, created panel of a week day is shown to the user.</li>
     *     <li>If user double-clicks on a task name, the {@link PlannerTableManager#editTask(String, String)} method is
     *     invoked, allowing the user to edit the selected task.</li>
     * </ul>
     *
     * @return a {@link MouseListener} for the {@link JTable} representing the week planner
     * @see #setupListeners()
     */
    private MouseListener createPlannerListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent click) {
                var planner = plannerTableManager.getPlanner();
                var clickedRow = planner.rowAtPoint(click.getPoint());

                var weekDayNum = planner.columnAtPoint(click.getPoint()) - 1;
                var weekDayPL = planner.getColumnName(weekDayNum + 1);
                var hour = (String) planner.getValueAt(clickedRow, 0);
                var cellValue = planner.getValueAt(clickedRow, weekDayNum + 1);

                if (click.getClickCount() == 2 && click.getButton() == MouseEvent.BUTTON1 && !weekDayPL.isBlank()) {
                    if (hour.isBlank()) {
                        var weekDayPanelUI = new WeekDayPanelUI(
                                weekDayNum,
                                plannerTableManager.getPlanners().get(WeekDays.getWeekDayEN(weekDayPL)),
                                main.getMainCards(),
                                main
                        );

                        main.getMainCards().add(weekDayPanelUI.getWeekDayScrollPane(), weekDayPL);
                        showPanel(weekDayPL);
                    } else if (!hour.equals("PUNKTY") && !cellValue.equals("<html><strike>-</strike></html>")) {
                        if (plannerTableManager.editTask(weekDayPL, hour)) refreshApp();
                    }
                }
            }
        };
    }

    /**
     * Creates and returns a {@link WindowAdapter} that defines the behaviour when the window is closed by the user. If
     * user closes window, the {@link PlannerTableManager#savePlanner()} method is invoked to save the state of the
     * {@code planner}. After saving, the {@link Main#dispose()} method is called to dispose the window and
     * {@link System#exit(int)} is called to terminate the application.
     *
     * @return a {@link WindowAdapter} that handles the window closing event by saving the planner state, disposing the
     * window, and exiting the application.
     * @see MainListeners#setupListeners()
     */
    private WindowAdapter configureWindowsAdapter() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                plannerTableManager.savePlanner();
                main.dispose();
                System.exit(0);
            }
        };
    }
    //endregion

    //region Auxiliary methods

    /**
     * Deletes task from the {@link JTable} representing a weekday planner. Checks which cell was clicked by the user.
     * If it is the task's name, the {@link PlannerTableManager#deleteTask(String, String)} method is invoked, which
     * deletes task from the planner. If the task is deleted, the {@link #refreshApp()} is invoked.
     *
     * @see #createButtonsListener()
     */
    private void deleteTask() {
        var planner = mainPanelUI.getPlanner();
        var column = planner.getSelectedColumn();
        var row = planner.getSelectedRow();
        var hour = (String) planner.getValueAt(row, 0);

        if (row > 0 && row < planner.getRowCount() && column > 0 && column < planner.getColumnCount() &&
                !plannerTableManager.deleteTask(planner.getColumnName(column), hour)) {
            refreshApp();
        }
    }

    /**
     * Deletes all tasks or reset all achieved points from the selected weekdays chosen by the user. This method creates
     * and shows a {@link JOptionPane#showOptionDialog(Component, Object, String, int, int, Icon, Object[], Object)}
     * dialog allowing user to select weekdays. If user confirms their selection, the chosen weekdays are passed to the
     * {@link PlannerTableManager#cleanPlanner(HashSet, String)} method along with the given {@code actionCommand}.
     *
     * @param actionCommand a {@link String} containing action command's name which is passed from
     *                      {@link #createButtonsListener()} method.
     * @see #createButtonsListener()
     */
    private void cleanPlanner(String actionCommand) {
        var checkedWeekDays = new HashSet<String>();
        var checkBox = Stream.concat(
                Arrays.stream(WeekDays.values()).map(weekDay -> new JCheckBox(weekDay.getWeekDayPL())),
                Stream.of(new JCheckBox("Zaznacz wszystkie"))
        ).toArray(JCheckBox[]::new);

        Arrays.stream(checkBox).forEach(weekDay -> weekDay.addItemListener(event -> {
            switch (event.getStateChange()) {
                case ItemEvent.SELECTED -> {
                    if (weekDay.getText().equals("Zaznacz wszystkie"))
                        Arrays.stream(checkBox).forEach(op -> op.setSelected(true));
                    else weekDay.setSelected(true);
                }
                case ItemEvent.DESELECTED -> {
                    if (weekDay.getText().equals("Zaznacz wszystkie"))
                        Arrays.stream(checkBox).forEach(op -> op.setSelected(false));
                    else weekDay.setSelected(false);
                }
            }
        }));

        var selectedOption = JOptionPane.showOptionDialog(
                null,
                checkBox,
                "Wybierz dni tygodnia",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{"OK", "Anuluj"},
                "OK");

        if (selectedOption == 0) {
            Arrays.stream(checkBox).forEach(weekDay -> {
                if (weekDay.isSelected() && !weekDay.getText().equals("Zaznacz wszystkie"))
                    checkedWeekDays.add(weekDay.getText());
            });
            plannerTableManager.cleanPlanner(checkedWeekDays, actionCommand);
            refreshApp();
        }
    }

    /**
     * Creates and shows a
     * {@link JOptionPane#showOptionDialog(Component, Object, String, int, int, Icon, Object[], Object)} dialog to the
     * user, allowing them to choose a type of information to display. Depending on the user's choice, this method shows
     * the corresponding {@link JPanel} from the {@link #mainCardsLayout}.
     * <br><br>
     * The options presented to the user are "Baza", "Planer" and "Anuluj". If the user selects "Baza" or "Planer", the
     * corresponding panel is displayed, and the application's window is resized and centered.
     *
     * @see #createButtonsListener()
     */
    private void showInfo() {
        var options = new String[]{"Baza", "Planer", "Anuluj"};
        int result = JOptionPane.showOptionDialog(
                null,
                "Chcę uzyskać informacje o:",
                "Uzyskaj informacje",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (!options[result].equals("Anuluj")) {showPanel(options[result]);}
    }

    /**
     * Displays the panel from the {@link #mainCardsLayout} which name corresponds to the given {@code panelName}. Then
     * sets the size of the {@link JFrame} by invoking the {@link Main#setPanelSize(String)} with {@code panelName} as
     * the parameter, and centers the app's window on the screen.
     *
     * @param panelName a {@link String} representing the name of the panel to be shown to the user
     * @see #createButtonsListener()
     * @see #createPlannerListener()
     * @see #showInfo()
     */
    private void showPanel(String panelName) {
        mainCardsLayout.show(main.getMainCards(), panelName);
        main.setPanelSize(panelName);
        main.setLocationRelativeTo(null);
    }

    /**
     * Reloads the app after making changes in the {@link JTable} representing the user's planner. Invokes the
     * {@link PlannerTableManager#refreshPlanner()} method to reload the user's planner, sets the size of the
     * {@link JFrame} using the {@link Main#setPanelSize(String)} method with "MainPanel" as the parameter and centers
     * the window on the screen.
     *
     * @see #createButtonsListener()
     * @see #createPlannerListener()
     * @see #deleteTask()
     * @see #cleanPlanner(String)
     */
    private void refreshApp() {
        plannerTableManager.refreshPlanner();
        main.setPanelSize("MainPanel");
        main.setLocationRelativeTo(null);
    }
    //endregion
}