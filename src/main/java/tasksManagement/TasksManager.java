package tasksManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.Main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TasksManager {
    private TreeSet<String> tasksList;
    private JDialog addTaskDialog;
    private final Gson gson;
    private final File tasksListFile;

    public TasksManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        tasksListFile = new File(Main.calendarFolder, "/TasksList.json");

        if(tasksListFile.exists()){
            try(var reader = new FileReader(tasksListFile)){
                tasksList = gson.fromJson(reader, new TreeSet<>(new TasksListComparator()){}.getClass().getGenericSuperclass());
            }catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Wystąpił błąd w trakcie ładowania zadań z pliku: <html><u>" +
                                tasksListFile.getPath() + "</u></html>.",
                        "Błąd wczytywania pliku",
                        JOptionPane.ERROR_MESSAGE
                );

                tasksList = new TreeSet<>(new TasksListComparator());
            }
        }else {tasksList = new TreeSet<>(new TasksListComparator());}
    }

    public void showTaskDialog() {
        var creator = new TaskAddingDialogCreator(getTasksList());

        addTaskDialog = new JDialog();
        addTaskDialog.setLayout(new FlowLayout());
        addTaskDialog.setSize(400, 400);
        addTaskDialog.setTitle("Dodaj zadanie do bazy");
        addTaskDialog.setModal(true);

        addTaskDialog.add(Box.createRigidArea(new Dimension(20,10)));
        addTaskDialog.add(creator.getLeftPanel());
        addTaskDialog.add(Box.createRigidArea(new Dimension(20,10)));
        addTaskDialog.add(creator.getRightPanel());
        addTaskDialog.add(Box.createRigidArea(new Dimension(20,20)));

        addListeners(creator);

        addTaskDialog.pack();
        addTaskDialog.setLocationRelativeTo(null);
        addTaskDialog.setVisible(true);
    }

    public void deleteTask(String task){
        if(!tasksList.isEmpty() && tasksList.remove(task)){
            try(var writer = new FileWriter(tasksListFile)){
                gson.toJson(tasksList, writer);
            }catch (IOException e){
                JOptionPane.showMessageDialog(
                        null,
                        "Usuwanie zadania z pliku zakończyło się niepowodzeniem.",
                        "Błąd usuwania zadania",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public String[] getTasksList(){return this.tasksList.toArray(new String[0]);}

    private void addTaskToList(String task){
        try(var writer = new FileWriter(tasksListFile)){
            if(this.tasksList.add(task)) gson.toJson(tasksList, writer);
        }catch (IOException e){
            JOptionPane.showMessageDialog(
                    null,
                    "Dodawanie zadania do pliku zakończyło się niepowodzeniem.",
                    "Błąd zapisywania zadania",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addListeners(TaskAddingDialogCreator creator){
        final String[] task = new String[1];

        creator.getTasksList().addListSelectionListener(listEvent -> {
            if(listEvent.getValueIsAdjusting()) return;

            task[0] = creator.getTasksList().getSelectedValue();
            creator.getTaskInputField().setText(task[0]);
        });

        Arrays.stream(creator.getButtons()).forEach(button -> button.addActionListener(_ -> {
            switch (button.getText()){
                case "Dodaj":
                    if(task[0] != null && !task[0].isBlank()){
                        addTaskToList(task[0]);
                        creator.getTasksList().setListData(getTasksList());
                        creator.getTaskInputField().setText("");
                    }
                    break;
                case "Usuń":
                    if(task[0] != null && !task[0].isBlank()) {
                        deleteTask(task[0]);
                        creator.getTasksList().setListData(getTasksList());
                        creator.getTaskInputField().setText("");
                    }
                    break;
                case "Anuluj":
                    creator.getTaskInputField().setText("");
                    break;
            }
        }));

        creator.getTaskInputField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                task[0] = creator.getTaskInputField().getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                task[0] = creator.getTaskInputField().getText();}

            @Override
            public void changedUpdate(DocumentEvent e) {
                task[0] = creator.getTaskInputField().getText();}
        });
    }
}