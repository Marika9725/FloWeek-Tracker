package calendarManagement;

import tasksManagement.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

class TaskAddingDialogUI {
    private SingleTaskManager task;
    private String time;

    public TaskAddingDialogUI() {
        this.task = new SingleTaskManager();
        this.time = "00:00";
    }

    JPanel getTaskSelectionPanel(){
        var taskSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        var tasksComboBox = new JComboBox<>(new TasksManager().getTasksList());
        tasksComboBox.setSelectedItem(task.getTaskName());
        tasksComboBox.addActionListener(_ -> task.setTaskName((String) tasksComboBox.getSelectedItem()));

        taskSelectionPanel.add(new JLabel("Zadanie:"));
        taskSelectionPanel.add(tasksComboBox);

        return taskSelectionPanel;
    }

    JPanel getWeekDaySelectionPanel(){
        var weekDaySelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        var weekDaysComboBox = new JComboBox<>(new CalendarManager().getWeekDaysPL());
        weekDaysComboBox.setSelectedItem(task.getWeekDay().getWeekDayPL());
        weekDaysComboBox.addActionListener(_ -> {
            var selectedWeekDay = WeekDays.values()[weekDaysComboBox.getSelectedIndex()];
            if(selectedWeekDay != null) task.setWeekDay(selectedWeekDay);
        });

        weekDaySelectionPanel.add(new JLabel("Dzień tygodnia"));
        weekDaySelectionPanel.add(weekDaysComboBox);

        return weekDaySelectionPanel;
    }

    JPanel getTimeSelectionPanel(){
        var timeSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        var hour = Byte.parseByte(time.substring(0, time.indexOf(":")));
        var minute = Byte.parseByte(time.substring(time.indexOf(":")+1));
        var calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        var defaultTime = calendar.getTime();
        var timeSpinner = new JSpinner(new SpinnerDateModel(defaultTime, null, null, Calendar.HOUR_OF_DAY));
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, new SimpleDateFormat("HH:mm").toPattern()));
        timeSpinner.addChangeListener(_ -> time = timeSpinner.getValue().toString().substring(11, 16));

        timeSelectionPanel.add(new JLabel("Godzina"));
        timeSelectionPanel.add(timeSpinner);

        return timeSelectionPanel;
    }

    JPanel getPrioritySelectionPanel(){
        var prioritySelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        var prioritySpinner = new JSpinner(new SpinnerNumberModel(task.getPriority(), 1, 10, 1));
        prioritySpinner.addChangeListener(_ -> task.setPriority(Byte.parseByte(prioritySpinner.getValue().toString())));

        prioritySelectionPanel.add(new JLabel("Prioritet:"));
        prioritySelectionPanel.add(prioritySpinner);

        return prioritySelectionPanel;
    }

    JPanel getDescriptionPanel(){
        var descriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        var descriptionTextArea = new JTextArea(3, 32);
        descriptionTextArea.setLineWrap(true);
        if(!task.getDescription().equals("unknown")) descriptionTextArea.append(task.getDescription());
        descriptionTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {task.setDescription(descriptionTextArea.getText());}

            @Override
            public void removeUpdate(DocumentEvent e) {task.setDescription(descriptionTextArea.getText());}

            @Override
            public void changedUpdate(DocumentEvent e) {task.setDescription(descriptionTextArea.getText());}
        });

        var descriptionScrollPane = new JScrollPane(descriptionTextArea);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        descriptionPanel.add(new JLabel("Opis:"));
        descriptionPanel.add(descriptionScrollPane);

        return descriptionPanel;
    }

    public String getTime(){return this.time;}
    void setTime(String time){this.time = time;}
    public SingleTaskManager getTask(){return this.task;}
    public void setTask(SingleTaskManager task){this.task = task;}

    public JPanel[] getAllPanels(){
        return new JPanel[]{
                getTaskSelectionPanel(),
                getWeekDaySelectionPanel(),
                getTimeSelectionPanel(),
                getPrioritySelectionPanel(),
                getDescriptionPanel()
        };
    }
}