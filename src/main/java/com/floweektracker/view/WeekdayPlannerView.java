package com.floweektracker.view;

import com.floweektracker.MainFrame;
import com.floweektracker.model.*;
import com.floweektracker.service.TasksService;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Represents UI of the schedule for a given weekday, displaying the name of the weekday, its schedule and a return
 * button to the user. It also contains return button to go back to the {@link MainPanelView}.
 * <br><br>
 * Fields: {@link #weekday}, {@link #returnButton}, {@link #contentPanel}
 * <br><br>
 * Methods: {@link #initWeekdayPlannerView()}, {@link #createWeekdayPanel()}
 * <br><br>
 * Content Panel methods: {@link #createHeadlinePanel()}, {@link #createContentPanel()},
 * {@link #createTaskPanel(SingleTask)}, {@link #createDescriptionScrollPane(SingleTask)},
 * {@link #createGrid(int, int, String, int, Insets)}
 * <br><br>
 * Bottom Panel methods: {@link #createBottomPanel()}, {@link #createReturnButton()}
 * <br><br>
 * Helpful methods: {@link #createPanel(String, LayoutManager, int)}, {@link #createLabel(String, String, Font)}
 */
@Getter
public class WeekdayPlannerView extends JScrollPane {
    /**
     * Contains a {@link WeekDays} enum representing given weekday.
     *
     * @see #WeekdayPlannerView(WeekDays)
     * @see #initWeekdayPlannerView()
     * @see #createWeekdayPanel()
     * @see #createHeadlinePanel()
     * @see #createContentPanel()
     */
    private final WeekDays weekday;
    /**
     * Represents a return button to go back to the {@link MainPanelView}.
     *
     * @see #WeekdayPlannerView(WeekDays)
     * @see #createReturnButton()
     * @see #createBottomPanel()
     */
    private final JButton returnButton;
    /**
     * Represents a panel containing tasks for given weekday.
     *
     * @see #WeekdayPlannerView(WeekDays)
     * @see #createWeekdayPanel()
     * @see #createContentPanel()
     */
    private final JPanel contentPanel;

    /**
     * Constructs a new {@link WeekdayPlannerView} instance for a specific weekday.Initializes references to the
     * {@link #weekday}, creates {@link #returnButton} and {@link #contentPanel}. Then, it calls
     * {@link #initWeekdayPlannerView()} method.
     *
     * @param weekday the WeekDays enum representing the specific weekday
     * @see com.floweektracker.Main
     */
    public WeekdayPlannerView(WeekDays weekday) {
        if (weekday == null) throw new IllegalArgumentException("Weekday cannot be null");
        this.weekday = weekday;
        this.returnButton = createReturnButton();
        this.contentPanel = createContentPanel();

        initWeekdayPlannerView();
    }

    //region initWeekdayPlannerView methods

    /**
     * When {@link #weekday} isn't null, it sets the name of the {@link WeekdayPlannerView}, Vertical Scroll Bar Policy
     * and Viewport View. In other case, it doesn't do anything.
     *
     * @see WeekdayPlannerView(WeekDays)
     */
    private void initWeekdayPlannerView() {
        this.setName("%sPanel".formatted(weekday.name()));
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setViewportView(createWeekdayPanel());
    }

    /**
     * Creates a {@link JPanel} representing the UI of the schedule for a given weekday, displaying the name of the
     * specified weekday, the schedule, and a return button to the user.
     *
     * @return a {@link JPanel} representing the UI of the schedule for a given weekday
     * @see #initWeekdayPlannerView()
     */
    @NotNull
    private JPanel createWeekdayPanel() {
        var weekdayPanel = createPanel(String.format("weekdayPanel%s", weekday.name()), new BorderLayout(), -1);

        weekdayPanel.add(createHeadlinePanel(), BorderLayout.NORTH);
        weekdayPanel.add(contentPanel, BorderLayout.CENTER);
        weekdayPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        return weekdayPanel;
    }
    //endregion

    //region createContentPanel methods

    /**
     * Creates a {@link JPanel} representing the headline panel for a given weekday, which displays the name of the
     * weekday in a large font.
     *
     * @return a {@link JPanel} representing the headline panel for a given weekday
     * @see #createWeekdayPanel()
     * @see #createPanel(String, LayoutManager, int)
     */
    @NotNull
    private JPanel createHeadlinePanel() {
        var panel = createPanel("headlinePanel", new FlowLayout(FlowLayout.CENTER), -1);
        panel.add(createLabel("titleLabel", weekday.getWeekdayPL(), new Font("Arial", Font.BOLD, 40)));

        return panel;
    }

    /**
     * Creates a {@link JPanel} representing the content panel for a given weekday, which displays its tasks. Each task
     * has its own panel.
     *
     * @return a {@link JPanel} representing the content panel for a given weekday
     * @see #createWeekdayPanel()
     * @see #createPanel(String, LayoutManager, int)
     * @see #createTaskPanel(SingleTask)
     */
    @NotNull
    private JPanel createContentPanel() {
        var weekdayTasks = TasksService.getService().getSchedule().get(weekday);

        var panel = createPanel("contentPanel", null, BoxLayout.Y_AXIS);

        if ((weekdayTasks != null) && (!weekdayTasks.isEmpty())) {
            weekdayTasks.values().forEach(task -> panel.add(createTaskPanel(task)));
        }

        return panel;
    }

    /**
     * Creates a {@link JPanel} representing a task panel for a given task. This method configures UI components which
     * display details of the task's name, time, name, points, priority, and description.
     *
     * @param task a {@link SingleTask} object containing details to be displayed in the panel
     * @return a {@link JPanel} containing UI components displaying details of the specified task
     * @see #createContentPanel()
     * @see #createGrid(int, int, String, int, Insets)
     * @see com.floweektracker.service.WeekdayPlannerService#addTask(SingleTask)
     */
    @NotNull
    public JPanel createTaskPanel(@NotNull SingleTask task) {
        var nameForPanel = String.format("%s_%s", task.getTime().toString(), task.getTaskName());
        var textForTaskNameLabel = String.format("%s(%d/%d)", task.getTaskName(), task.calculatePoints(), task.getPriority());

        var taskNameLabel = createLabel("taskNameLabel", textForTaskNameLabel, null);
        var timeLabel = createLabel("timeLabel", task.getTime().toString(), null);

        var taskPanel = createPanel(nameForPanel, new GridBagLayout(), -1);
        taskPanel.add(timeLabel, createGrid(0, 0, null, GridBagConstraints.LINE_END, new Insets(0, 0, 0, 10)));
        taskPanel.add(taskNameLabel, createGrid(1, 0, null, GridBagConstraints.LINE_START, new Insets(0, 0, 0, 10)));
        taskPanel.add(createDescriptionScrollPane(task), createGrid(1, 1, "2", GridBagConstraints.LINE_START, null));

        return taskPanel;
    }


    /**
     * Creates a {@link JScrollPane} containing a {@link JTextArea} which displays the description of the given task.
     * The {@link JTextArea} is configured to wrap text at word boundaries, has a preferred height of 35px and width of
     * 400px, and is not editable.
     *
     * @param task the {@link SingleTask} object containing the description to be displayed
     * @return a {@link JScrollPane} containing the description of the task
     * @see #createTaskPanel(SingleTask)
     */
    @NotNull
    private JScrollPane createDescriptionScrollPane(@NotNull SingleTask task) {
        var descriptionTextArea = new JTextArea(task.getDescription());
        descriptionTextArea.setName("descriptionTextArea");
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setPreferredSize(new Dimension(400, 35));
        descriptionTextArea.setEditable(false);

        var descriptionPane = new JScrollPane(descriptionTextArea);
        descriptionPane.setName("descriptionScrollPane");
        descriptionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return descriptionPane;
    }

    /**
     * Creates and configures (@link GridBagConstraints} used to customize the layout of components within its
     * containing {@link JPanel}.
     *
     * @param gridx     the column position in the grid where component should be placed
     * @param gridy     the row position in the grid where the component should be placed
     * @param gridWidth the number of columns that the component should span; can be {@code null} if not specified
     * @param anchor    determines where, within the display area, to place the component if ti doesn't fill the space
     * @param insets    the external padding of the component; can be {@code null} if not specified
     * @return a {@link GridBagConstraints} object configured for customizing the layout within the
     * {@link #createTaskPanel(SingleTask)} method
     */
    @NotNull
    private GridBagConstraints createGrid(int gridx, int gridy, String gridWidth, int anchor, Insets insets) {
        var grid = new GridBagConstraints();
        grid.gridx = gridx;
        grid.gridy = gridy;
        grid.anchor = anchor;

        if (gridWidth != null) grid.gridwidth = Integer.parseInt(gridWidth);
        if (insets != null) grid.insets = insets;

        return grid;
    }
    //endregion

    //region createBottomPanel methods

    /**
     * Creates a {@link JPanel} containing a {@link JButton} allowing the user to return to the main panel.
     *
     * @return a {@link JPanel} containing the return button
     * @see #createWeekdayPanel()
     * @see #createReturnButton()
     * @see #createPanel(String, LayoutManager, int)
     */
    @NotNull
    private JPanel createBottomPanel() {
        var bottomPanel = createPanel("bottomPanel", new FlowLayout(), -1);
        bottomPanel.add(returnButton);

        return bottomPanel;
    }

    /**
     * Creates a {@link JButton} that allows the user to return to the main panel. The button is labeled "Powrót"
     * and when clicked, it triggers an action to switch the card to "mainPanel" in the {@link MainFrame}.
     *
     * @return a {@link JButton} configured as a return button for navigating back to the main panel
     * @see WeekdayPlannerView(WeekDays)
     * @see #createBottomPanel()
     */
    @NotNull
    private JButton createReturnButton() {
        var returnButton = new JButton("Powrót");
        returnButton.setName("returnButton");
        returnButton.addActionListener(_ -> MainFrame.getMAIN_FRAME().switchCard("mainPanel"));

        return returnButton;
    }
    //endregion

    //region helpful methods

    /**
     * Creates a {@link JPanel} with the given name and layout manager. If the given layout manager is {@code null}, the
     * panel is configured with a {@link BoxLayout} using the given axis.
     *
     * @param name   the name of the panel
     * @param layout the layout manager of the panel; can be {@code null}
     * @param axis   the axis of the {@link BoxLayout}; can be {@code -1} if a layout manager is provided
     * @return a {@link JPanel} with the given name and layout manager
     * @see #createWeekdayPanel()
     * @see #createHeadlinePanel()
     * @see #createContentPanel()
     * @see #createTaskPanel(SingleTask)
     * @see #createBottomPanel()
     */
    @NotNull
    private JPanel createPanel(String name, LayoutManager layout, int axis) {
        var panel = new JPanel();
        panel.setName(name);

        if ((layout == null) && (axis != -1)) panel.setLayout(new BoxLayout(panel, axis));
        else panel.setLayout(layout);

        return panel;
    }

    /**
     * Creates a {@link JLabel} with the given name, text and font. If the font is {@code null} the font isn't set.
     *
     * @param name a name for the label
     * @param text a text for the label
     * @param font a font for the label
     * @return a {@link JLabel} with the given name, text and font
     */
    @NotNull
    private JLabel createLabel(String name, String text, Font font) {
        var label = new JLabel(text);
        label.setName(name);
        if (font != null) label.setFont(font);

        return label;
    }
    //endregion
}