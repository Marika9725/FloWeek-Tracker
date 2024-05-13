package calendarManagement;

import tasksManagement.SingleTaskManager;

import javax.swing.*;
import java.awt.*;

class WeekDayPanelUI {
    private WeekDays weekDay;
    private String time;
    private SingleTaskManager task;

    public WeekDayPanelUI() {}

    public JPanel getHeadlinePanel(){
        var headlinePanel = new JPanel();
        var titleLabel = new JLabel(weekDay.getWeekDayPL());

        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        headlinePanel.add(titleLabel);

        return headlinePanel;
    }

    public JPanel getContentPanel(){
        var contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        return contentPanel;
    }

    public JPanel getTaskContentPanel(){
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
        taskPanel.add(new Label(time), getGrid(0, 0, null, GridBagConstraints.LINE_END, new Insets(0, 0, 0, 10)));
        taskPanel.add(new Label(taskNameLabel), getGrid(1, 0, null, GridBagConstraints.LINE_START, new Insets(0, 0, 0, 10)));
        taskPanel.add(descriptionScrollPane, getGrid(1, 1, "2", GridBagConstraints.LINE_START, null));

        return taskPanel;
    }

    public void setDay(WeekDays weekDay){this.weekDay = weekDay;}
    public void setTime(String time){this.time = time;}
    public void setTask(SingleTaskManager task){this.task = task;}

    private GridBagConstraints getGrid(int gridx, int gridy, String gridwidth, int anchor, Insets instets){
        var grid = new GridBagConstraints();
        grid.gridx = gridx;
        grid.gridy = gridy;
        grid.anchor = anchor;

        if(gridwidth != null) grid.gridwidth = Integer.parseInt(gridwidth);
        if(instets != null) grid.insets = instets;

        return grid;
    }
}
