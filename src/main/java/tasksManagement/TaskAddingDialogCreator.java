package tasksManagement;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

class TaskAddingDialogCreator {
    final private JPanel leftPanel, rightPanel;
    private final JButton[] buttons;
    private final JTextField taskInputField;
    private final JList<String> tasksList;

    public TaskAddingDialogCreator(String[] tasksList) {
        this.tasksList = new JList<>(tasksList);
        this.taskInputField = new JTextField(32);
        this.buttons = new JButton[]{new JButton("Dodaj"), new JButton("Usuń"), new JButton("Anuluj")};

        this.leftPanel = new JPanel();
        this.leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        this.leftPanel.add(getCurrentTasksPanel());

        this.rightPanel = new JPanel();
        this.rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        this.rightPanel.add(Box.createVerticalStrut(100));
        this.rightPanel.add(getTaskAddingPanel());
        this.rightPanel.add(getButtonsPanel());
        this.rightPanel.add(Box.createVerticalStrut(100));
    }

    public JPanel getLeftPanel() {return leftPanel;}
    public JPanel getRightPanel() {return rightPanel;}
    public JButton[] getButtons(){return buttons;}
    public JTextField getTaskInputField(){return taskInputField;}
    public JList<String> getTasksList(){return tasksList;}

    private JPanel getCurrentTasksPanel(){
        var currentTasksPanel = new JPanel();

        currentTasksPanel.setLayout(new BoxLayout(currentTasksPanel, BoxLayout.Y_AXIS));
        currentTasksPanel.add(new JLabel("Dostępne zadania:"));
        currentTasksPanel.add(new JScrollPane(tasksList));

        return currentTasksPanel;
    }
    private JPanel getTaskAddingPanel(){
        var taskAddingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        taskAddingPanel.add(new JLabel("Zadanie"));
        taskAddingPanel.add(taskInputField);

        return taskAddingPanel;
    }
    private JPanel getButtonsPanel(){
        var addTaskButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        Arrays.stream(buttons).forEach(addTaskButtonsPanel::add);

        return addTaskButtonsPanel;
    }
}