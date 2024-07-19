package com.floweektracker.planner;

import com.floweektracker.main.Main;
import com.floweektracker.tasksDatabase.SingleTaskManager;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Represents UI of the schedule for one of the weekdays, displaying the name of the weekday, its schedule and a return
 * button to the user.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #weekDay} - a {@link WeekDays} enum representing the name of a specified weekdays</li>
 *     <li>{@link #weekDayScrollPane} - a {@link JScrollPane} contains a weekday panel created by
 *     {@link #createWeekDayPanel()} providing a scrollable view for the user</li>
 *     <li>{@link #tasks} - a {@link HashMap} contains all tasks from one of the weekdays</li>
 *     <li>{@link #mainCards} - a {@link JPanel} with a {@link CardLayout} contains panels to be displayed to the user
 *     </li>
 *     <li>{@link #main} - a {@link Main} reference containing the passed instance of the main application class</li>
 *     <li>{@link #times} - a {@link TreeSet} with the {@link String}s representing sorted {@link #tasks} times.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #createWeekDayPanel()} - creates a {@link JPanel} representing the UI schedule for the specified
 *     weekday, which is displayed to the user</li>
 * </ul>
 * Auxiliary methods:
 * <ul>
 *     <li>{@link #createHeadlinePanel()} - creates a {@link JPanel} containing the headline for the panel created by
 *     the {@link #createWeekDayPanel()} method</li>
 *     <li>{@link #createContentPanel()} - creates a {@link JPanel} containing tasks panels for the panel created by the
 *     {@link #createWeekDayPanel()} method</li>
 *     <li>{@link #createTaskPanel(SingleTaskManager)} - creates a {@link JPanel} representing the UI for a
 *     {@link SingleTaskManager} task</li>
 *     <li>{@link #createBottomPanel()} - creates a {@link JPanel} containing a {@link JButton} representing a return
 *     button</li>
 *     <li>{@link #createGrid(int, int, String, int, Insets)} - creates a {@link GridBagConstraints} used in the
 *     {@link #createTaskPanel(SingleTaskManager)} method</li>
 * </ul>
 * <p>
 * Getters: {@link #weekDayScrollPane}
 */
public class WeekDayPanelUI {
    //region Fields
    /**
     * Contains a {@link WeekDays} enum representing one of the weekdays. The {@link #weekDayScrollPane} is created for
     * the value of this enum.
     *
     * @see #WeekDayPanelUI(int, HashMap, JPanel, Main)
     * @see #createHeadlinePanel()
     */
    private final WeekDays weekDay;
    /**
     * Contains a {@link JScrollPane} that provides a scrollable view of the {@link JPanel} created by the
     * {@link #createWeekDayPanel()} method.
     *
     * @see #WeekDayPanelUI(int, HashMap, JPanel, Main)
     * @see #getWeekDayScrollPane()
     */
    private final JScrollPane weekDayScrollPane;
    /**
     * Contains all tasks from the schedule of a specified weekday. The key is a {@link String} representing the time of
     * the task, and the value is a {@link SingleTaskManager} instance managing the task details.
     *
     * @see #WeekDayPanelUI(int, HashMap, JPanel, Main)
     * @see #createWeekDayPanel()
     */
    private final HashMap<String, SingleTaskManager> tasks;
    /**
     * Contains a {@link JPanel} which is provided as an argument to the constructor. This {@link JPanel} uses a
     * {@link CardLayout} and contains panels to be displayed to the user.
     *
     * @see #WeekDayPanelUI(int, HashMap, JPanel, Main)
     * @see #createBottomPanel()
     */
    private final JPanel mainCards;
    /**
     * Contains a {@link Main} instance provided as an argument to the constructor.
     *
     * @see #WeekDayPanelUI(int, HashMap, JPanel, Main)
     * @see #createBottomPanel()
     */
    private final Main main;
    /**
     * Contains a {@link TreeSet} with the {@link String}s representing the sorted {@link #tasks} times.
     *
     * @see #WeekDayPanelUI(int, HashMap, JPanel, Main)
     * @see #createContentPanel()
     */
    private final TreeSet<String> times;

    //endregion

    /**
     * Constructs a new {@link WeekDayPanelUI} instance with the specified {@code weekDayNum}, {@code tasks},
     * {@code mainCards}, and {@code main}. This constructor initializes references to the {@link #weekDay},
     * {@link #times} {@link #tasks}, {@link #mainCards}, and {@link #main} fields based on the provided parameters. It
     * also creates a {@link #weekDayScrollPane}, sets its name and configures its vertical scroll bar policy.
     *
     * @param weekDayNum a number representing the weekday to initialize the {@link WeekDays} enum
     * @param tasks      a {@link HashMap} used to initialize the {@link #tasks} mapping
     * @param mainCards  a {@link JPanel} used to initialize the {@link #mainCards} container
     * @param main       a {@link Main} instance used to initialize the {@link #main} application context
     * @see com.floweektracker.main.MainListeners#createPlannerListener()
     */
    public WeekDayPanelUI(int weekDayNum, HashMap<String, SingleTaskManager> tasks, JPanel mainCards, Main main) {
        this.weekDay = WeekDays.values()[weekDayNum];
        this.times = new TreeSet<>(String::compareToIgnoreCase);
        tasks.forEach((time, _) -> times.add(time));
        this.tasks = tasks;
        this.mainCards = mainCards;
        this.main = main;

        this.weekDayScrollPane = new JScrollPane(createWeekDayPanel());
        this.weekDayScrollPane.setName(weekDay.getWeekDayPL());
        this.weekDayScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Creates a {@link JPanel} representing the UI schedule for the specified weekday. This panel displays the name of
     * the specified weekday, the schedule, and a return button to the user.
     *
     * @return a {@link JPanel} representing the UI schedule for the specified weekday
     * @see #WeekDayPanelUI(int, HashMap, JPanel, Main)
     * @see #createHeadlinePanel()
     * @see #createContentPanel()
     * @see #createBottomPanel()
     */
    private JPanel createWeekDayPanel() {
        var weekDayPanel = new JPanel(new BorderLayout());
        weekDayPanel.add(createHeadlinePanel(), BorderLayout.NORTH);
        weekDayPanel.add(createContentPanel(), BorderLayout.CENTER);
        weekDayPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        return weekDayPanel;
    }

    //region Auxiliary methods

    /**
     * Creates a {@link JPanel} containing a customized {@link JLabel} with the Polish name of the {@link #weekDay}.
     * This method is used as an auxiliary method in the {@link #createWeekDayPanel()} method, serving as a headline.
     *
     * @return a {@link JPanel} containing the headline for the panel created by the {@link #createWeekDayPanel()}
     * method
     */
    private JPanel createHeadlinePanel() {
        var headlinePanel = new JPanel();
        var titleLabel = new JLabel(weekDay.getWeekDayPL());

        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        headlinePanel.add(titleLabel);

        return headlinePanel;
    }

    /**
     * Creates a {@link JPanel} containing individual {@link JPanel}s for each task from the {@link #tasks} field. This
     * method is used as an auxiliary method in the {@link #createWeekDayPanel()} method, serving as a schedule
     * display.
     *
     * @return a {@link JPanel} containing the schedule for the panel created by the {@link #createWeekDayPanel()}
     * method
     * @see #createTaskPanel(SingleTaskManager)
     */
    private JPanel createContentPanel() {
        var contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        if(tasks != null) times.forEach(time -> contentPanel.add(createTaskPanel(tasks.get(time))));

        return contentPanel;
    }

    /**
     * Creates a {@link JPanel} representing a task panel for the specified task. This method initializes and configures
     * UI components such as labels and scrollable text area to display details of the task, including its name,
     * priority, points scored, time, and description.
     *
     * @param task a {@link SingleTaskManager} object representing the task for which the panel is created
     * @return a {@link JPanel} containing UI components displaying details of the specified task
     * @see #createContentPanel()
     * @see #createGrid(int, int, String, int, Insets)
     */
    private JPanel createTaskPanel(SingleTaskManager task) {
        var taskPanel = new JPanel(new GridBagLayout());
        var pointsScored = task.getIsDone() ? String.valueOf(task.getPriority()) : "0";
        var taskNameLabel = task.getTaskName() + "(" + pointsScored + "/" + task.getPriority() + ")";
        var description = new JTextArea(task.getDescription());
        var descriptionScrollPane = new JScrollPane(description);

        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setPreferredSize(new Dimension(400, 35));
        description.setEditable(false);

        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        taskPanel.add(new Label(task.getTime()), createGrid(0, 0, null, GridBagConstraints.LINE_END, new Insets(0, 0, 0, 10)));
        taskPanel.add(new Label(taskNameLabel), createGrid(1, 0, null, GridBagConstraints.LINE_START, new Insets(0, 0, 0, 10)));
        taskPanel.add(descriptionScrollPane, createGrid(1, 1, "2", GridBagConstraints.LINE_START, null));

        return taskPanel;
    }

    /**
     * Creates a {@link JPanel} containing a {@link JButton} allowing the user to return to the main panel. This method
     * initializes the return button and adds a listener to handle the return action. It is used as an auxiliary method
     * in the {@link #createWeekDayPanel()} method, serving as the bottom panel.
     *
     * @return a {@link JPanel} containing the return button
     */
    private JPanel createBottomPanel() {
        var bottomPanel = new JPanel(new FlowLayout());
        var returnButton = new JButton("Powrót");
        returnButton.addActionListener(_ -> {
            ((CardLayout) mainCards.getLayout()).show(mainCards, "MainPanel");
            main.setPanelSize("MainPanel");
            main.setLocationRelativeTo(null);
        });
        bottomPanel.add(returnButton);

        return bottomPanel;
    }

    /**
     * Creates and configures {@link GridBagConstraints} used within the {@link #createTaskPanel(SingleTaskManager)}
     * method to customize the layout of components within its containing {@link JPanel}.
     *
     * @param gridx     the column position in the grid where the component should be placed
     * @param gridy     the row position in the grid where the component should be placed
     * @param gridwidth the number of columns the component should span; can be {@code null} if not specified
     * @param anchor    determines where, within the display area, to place the component if it doesn't fill the space
     * @param instets   the external padding of the component; can be {@code null} if not specified
     * @return a {@link GridBagConstraints} object configured for customizing the layout within the
     * {@link #createTaskPanel(SingleTaskManager)} method.
     */
    private GridBagConstraints createGrid(int gridx, int gridy, String gridwidth, int anchor, Insets instets) {
        var grid = new GridBagConstraints();
        grid.gridx = gridx;
        grid.gridy = gridy;
        grid.anchor = anchor;

        if (gridwidth != null) grid.gridwidth = Integer.parseInt(gridwidth);
        if (instets != null) grid.insets = instets;

        return grid;
    }

    //endregion
    //region Getters

    /**
     * @return a {@link #weekDayScrollPane} instance, which provides scrollable functionality for the weekday schedule
     * panel
     * @see com.floweektracker.main.MainListeners#createPlannerListener()
     */
    public JScrollPane getWeekDayScrollPane() {return weekDayScrollPane;}
    //endregion
}
