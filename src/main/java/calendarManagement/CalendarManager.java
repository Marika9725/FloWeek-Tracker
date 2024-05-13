package calendarManagement;

import com.google.gson.*;
import tasksManagement.SingleTaskManager;
import main.Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static javax.swing.BoxLayout.Y_AXIS;

public class CalendarManager{
    private HashMap<String, HashMap<String, SingleTaskManager>> calendars;
    private final WeekDays[] weekDays = WeekDays.values();
    private final File calendarFile;
    private final Gson gson;

    public CalendarManager() {
        calendarFile = new File(Main.calendarFolder.getPath(), "/Kalendarz.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if(calendarFile.exists()) loadSavedCalendar();
        else{
            this.calendars = new HashMap<>();
            Arrays.stream(weekDays).forEach(weekDay -> this.calendars.put(weekDay.name(), new HashMap<>()));
            saveCalendar();
        }
    }

    public DefaultTableModel getCalendarModel(){
        final var columnNamesPL = Stream.concat(Stream.of(""), Stream.of(getWeekDaysPL())).toArray(String[]::new);
        final var calendarModel = new DefaultTableModel(columnNamesPL, 0){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        final var times = showCalendarLines();
        final var data = new String[columnNamesPL.length];

        calendarModel.addRow(columnNamesPL);

        Arrays.stream(times).forEach(time -> {
            data[0] = time;
            byte i = 1;
            SingleTaskManager task;

            for(WeekDays weekDay : weekDays){
                if((task = calendars.get(weekDay.name()).get(time)) != null){
                    byte points = task.getIsDone() ? task.getPriority() : 0;
                    var cellValue = new StringBuilder()
                            .append(task.getTaskName())
                            .append("(").append(points).append("/").append(task.getPriority()).append(")");

                    data[i] = task.getIsDone() ? ("<html><strike>" + cellValue + "</strike></html>") : cellValue.toString();
                } else data[i] = "-";
                i++;
            }

            calendarModel.addRow(data);
        });

        calendarModel.addRow(countTotalPoints());

        return calendarModel;
    }

    public boolean taskAddingDialog(){
        final var dialogUI = new TaskAddingDialogUI();
        final var taskAddingDialog = new JDialog();
        var buttonsDialogPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        var dialogButtons = new JButton[]{new JButton("Zapisz"), new JButton("Anuluj")};
        boolean[] isSaveButtonClicked = {false};

        ActionListener actionListener = buttonEvent -> {
            if(buttonEvent.getActionCommand().equals("Zapisz")){
                var task = dialogUI.getTask();
                var time = dialogUI.getTime();

                calendars.get(task.getWeekDay().name()).put(time, task);

                isSaveButtonClicked[0] = true;
            }

            taskAddingDialog.dispose();
        };

        taskAddingDialog.setLayout(new BoxLayout(taskAddingDialog.getContentPane(), Y_AXIS));
        taskAddingDialog.setSize(400, 400);
        taskAddingDialog.setTitle("Dodaj zadanie do kalendarza");
        taskAddingDialog.setModal(true);

        Arrays.stream(dialogUI.getAllPanels()).forEach(taskAddingDialog::add);
        taskAddingDialog.add(buttonsDialogPanel);
        Arrays.stream(dialogButtons).forEach(button -> {
            button.addActionListener(actionListener);
            buttonsDialogPanel.add(button);
        });
        taskAddingDialog.pack();
        taskAddingDialog.setLocationRelativeTo(null);
        taskAddingDialog.setVisible(true);

        return isSaveButtonClicked[0];
    }

    public String[] getWeekDaysPL(){
        return Arrays.stream(weekDays)
                .map(WeekDays::getWeekDayPL)
                .toArray(String[]::new);
    }

    public JPanel getWeekDayPanel(int weekDayNum){
        var dayPanelUI = new WeekDayPanelUI();
        var weekDayPanel = new JPanel(new BorderLayout());
        var contentPanel = dayPanelUI.getContentPanel();

        dayPanelUI.setDay(weekDays[weekDayNum]);
        weekDayPanel.add(dayPanelUI.getHeadlinePanel(), BorderLayout.NORTH);
        calendars.get(weekDays[weekDayNum].name()).forEach((time, task) -> {
            if(task != null){
                dayPanelUI.setTime(time);
                dayPanelUI.setTask(task);
                contentPanel.add(dayPanelUI.getTaskContentPanel());
            }
        });

        weekDayPanel.add(contentPanel, BorderLayout.CENTER);

        return weekDayPanel;
    }

    public void saveCalendar(){
        try (var writer = new FileWriter(calendarFile)) {
            gson.toJson(calendars, writer);
        } catch (IOException e) {showWarningIOExceptionDialog();}
    }

    public boolean deleteTask(final String weekDayPL, final String time){
        calendars.get(WeekDays.getWeekDayEN(weekDayPL)).remove(time);
        return calendars.get(WeekDays.getWeekDayEN(weekDayPL)).containsKey(time);
    }

    public void cleanCalendar(final HashSet<String> checkedWeekDays, final String buttonName){
        Arrays.stream(weekDays)
                .filter(weekDay -> checkedWeekDays.contains(weekDay.getWeekDayPL()))
                .forEach(weekDay -> {
                   var times = calendars.get(weekDay.name()).keySet().toArray(String[]::new);

                    if(buttonName.equals("Wyczyść kalendarz"))
                        Arrays.stream(times).forEach(time -> calendars.get(weekDay.name()).remove(time));
                    else if(buttonName.equals("Wyzeruj punkty"))
                        Arrays.stream(times).forEach(time -> calendars.get(weekDay.name()).get(time).setDone(false));
                });
    }

    public void editTaskDialog(final String oldWeekDayPL, final String oldTime){
        var oldWeekDayEN = WeekDays.getWeekDayEN(oldWeekDayPL);
        SingleTaskManager[] task = {calendars.get(oldWeekDayEN).get(oldTime)};
        var dialogUI = new TaskEditingDialogUI(task[0], oldTime);
        var editTaskDialog = new JDialog();
        var buttonsDialogPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton[] dialogButtons = {new JButton("Zapisz"), new JButton("Anuluj")};
        ActionListener buttonsListener = buttonEvent -> {
            if(buttonEvent.getSource().equals(dialogButtons[0])){
                var newTime = dialogUI.getTime();
                task[0] = dialogUI.getTask();
                task[0].setDone(dialogUI.getIsDone());

                calendars.get(oldWeekDayEN).remove(oldTime);
                calendars.get(task[0].getWeekDay().name()).put(newTime, task[0]);
            }
            editTaskDialog.dispose();
        };

        Arrays.stream(dialogButtons).forEach(button -> {
            button.addActionListener(buttonsListener);
            buttonsDialogPanel.add(button);
        });

        editTaskDialog.setTitle("Edytuj zadanie");
        editTaskDialog.setLayout(new BoxLayout(editTaskDialog.getContentPane(), Y_AXIS));
        editTaskDialog.setSize(400, 400);
        Arrays.stream(dialogUI.getAllPanels()).forEach(editTaskDialog::add);
        editTaskDialog.add(buttonsDialogPanel);
        editTaskDialog.pack();
        editTaskDialog.setModal(true);
        editTaskDialog.setLocationRelativeTo(null);
        editTaskDialog.setVisible(true);
    }

    private void loadSavedCalendar(){
        try(var reader = new FileReader(calendarFile)){
            calendars = gson.fromJson(reader, new HashMap<String, HashMap<String, SingleTaskManager>>(){}.getClass().getGenericSuperclass());
        } catch (IOException e) {showWarningIOExceptionDialog();}
    }

    private String[] showCalendarLines(){
        var times = new TreeSet<String>();

        calendars.forEach((_, tasks) -> tasks.forEach((time, _) -> times.add(time)));

        return times.toArray(new String[0]);
    }

    private String[] countTotalPoints(){
        var pointsSummaryForEachDay = new ArrayList<String>();
        pointsSummaryForEachDay.add("PUNKTY");

        Arrays.stream(weekDays).forEach(weekDay -> {
            int total = calendars.get(weekDay.name()).values().stream()
                    .mapToInt(SingleTaskManager::getPriority)
                    .sum();
            int reached = calendars.get(weekDay.name()).values().stream()
                    .filter(SingleTaskManager::getIsDone)
                    .mapToInt(SingleTaskManager::getPriority)
                    .sum();

            pointsSummaryForEachDay.add(reached + "/" + total);
        });

        return pointsSummaryForEachDay.toArray(new String[0]);
    }

    private void showWarningIOExceptionDialog(){
        JOptionPane.showMessageDialog(
                null,
                "Błąd wejścia-wyjścia. Nie można zapisać kalendarza.",
                "IOException",
                JOptionPane.ERROR_MESSAGE
        );
    }
}