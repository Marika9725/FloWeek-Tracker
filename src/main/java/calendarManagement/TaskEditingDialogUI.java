package calendarManagement;

import tasksManagement.SingleTaskManager;

import javax.swing.*;
import java.awt.*;

class TaskEditingDialogUI extends TaskAddingDialogUI {
    private String isDone;

    public TaskEditingDialogUI(SingleTaskManager task, String hour){
        super();
        setTime(hour);
        setTask(task);
        this.isDone = task.getIsDone() ? "tak" : "nie";
    }

    private JPanel getIsDonePanel(){
        var isDonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var isDoneComboBox = new JComboBox<>(new String[]{"tak", "nie"});
        isDoneComboBox.setSelectedItem(isDone);

        isDonePanel.add(new JLabel("Wykonano:"));
        isDonePanel.add(isDoneComboBox);
        isDoneComboBox.addActionListener(_ -> isDone = (String) isDoneComboBox.getSelectedItem());

        return isDonePanel;
    }

    public boolean getIsDone(){return isDone.equals("tak");}

    public JPanel[] getAllPanels(){
        return new JPanel[]{
                getTaskSelectionPanel(),
                getWeekDaySelectionPanel(),
                getTimeSelectionPanel(),
                getPrioritySelectionPanel(),
                getIsDonePanel(),
                getDescriptionPanel()
        };
    }
}
