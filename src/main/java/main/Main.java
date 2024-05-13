package main;

import calendarManagement.CalendarManager;
import calendarManagement.WeekDays;
import tasksManagement.TasksManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Stream;

public class Main extends JFrame {
    private final CalendarManager calendarManager;
    private final JTable calendar;
    private final JButton[] buttons;
    private final JPanel mainCards;
    public static File calendarFolder;

    public Main(){
        this.calendarManager = new CalendarManager();
        this.calendar = new JTable(calendarManager.getCalendarModel());
        this.calendar.setVisible(false);

        this.buttons = new JButton[]{
                new JButton("Dodaj zadanie do kalendarza"),
                new JButton("Dodaj zadanie do bazy"),
                new JButton("Usuń zadanie"),
                new JButton("Wyczyść kalendarz"),
                new JButton("Wyzeruj punkty")
        };

        this.mainCards = new JPanel(new CardLayout());

        setupUI();
        setupListeners();
    }

    private void setupUI(){
        var mainPanel = new JPanel();
        var mainButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        var title = new JLabel("KALENDARZ");

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(title);
        mainPanel.add(calendar);
        mainPanel.add(mainButtonsPanel, "MainButtonsPanel");

        Arrays.stream(buttons).forEach(mainButtonsPanel::add);

        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        calendar.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        calendar.setCellSelectionEnabled(false);
        refreshCalendar();

        mainCards.add(mainPanel, "MainPanel");

        add(mainCards, BorderLayout.CENTER);
        pack();
        setTitle("FloWeek Tracker");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupListeners(){
        ActionListener buttonsListener = buttonEvent -> {
            switch(buttonEvent.getActionCommand()){
                case "Dodaj zadanie do kalendarza" -> {if (calendarManager.taskAddingDialog()) refreshCalendar();}
                case "Dodaj zadanie do bazy" -> new TasksManager().showTaskDialog();
                case "Usuń zadanie" -> {
                    var column = calendar.getSelectedColumn();
                    var row = calendar.getSelectedRow();
                    var hour = (String) calendar.getValueAt(row, 0);

                    if(row > 0 && row < calendar.getRowCount() && column > 0 && column < calendar.getColumnCount() &&
                            !calendarManager.deleteTask(calendar.getColumnName(column), hour)) {
                        refreshCalendar();
                    }
                }
                case "Wyczyść kalendarz", "Wyzeruj punkty" -> {
                    var selectedWeekDays = selectWeekDaysOptionDialog();

                    if(selectedWeekDays.contains("0")){
                        selectedWeekDays.remove("0");
                        selectedWeekDays.remove("Zaznacz wszystkie");

                        calendarManager.cleanCalendar(selectedWeekDays, buttonEvent.getActionCommand());
                        refreshCalendar();
                    }
                }
            }
        };

        Arrays.stream(buttons).forEach(button -> button.addActionListener(buttonsListener));

        calendar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent click) {
                var weekDayNum = calendar.columnAtPoint(click.getPoint())-1;
                var weekDayPL = calendar.getColumnName(weekDayNum+1);
                var hour = (String) calendar.getValueAt(calendar.rowAtPoint(click.getPoint()), 0);
                var cellValue = calendar.getValueAt(calendar.rowAtPoint(click.getPoint()), weekDayNum+1);

                if(click.getClickCount() == 2 && click.getButton() == MouseEvent.BUTTON1 && !weekDayPL.isBlank()){
                    if(hour.isBlank()) {
                        var weekDayPanel = calendarManager.getWeekDayPanel(weekDayNum);
                        var weekDayPanelSouth = new JPanel(new FlowLayout());
                        var returnButton = new JButton("Powrót");

                        weekDayPanelSouth.add(returnButton);
                        returnButton.addActionListener(_ -> ((CardLayout) mainCards.getLayout()).show(mainCards, "MainPanel"));

                        weekDayPanel.setName(weekDayPL);
                        weekDayPanel.add(weekDayPanelSouth, BorderLayout.SOUTH);
                        mainCards.add(weekDayPanel, weekDayPL);
                        pack();

                        ((CardLayout) mainCards.getLayout()).show(mainCards, weekDayPL);
                    } else if (!hour.equals("PUNKTY") && !cellValue.equals("<html><strike>-</strike></html>")){
                        calendarManager.editTaskDialog(weekDayPL, hour);
                        refreshCalendar();
                    }
                }
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                calendarManager.saveCalendar();
                dispose();
                System.exit(0);
            }
        });
    }

    private HashSet<String> selectWeekDaysOptionDialog(){
        var checkedWeekDays = new HashSet<String>();
        Object[] dialogOptions = {"OK", "Anuluj"};
        JCheckBox[] checkBox = Stream.concat(
                Arrays.stream(WeekDays.values()).map(weekDay -> new JCheckBox(weekDay.getWeekDayPL())),
                Stream.of(new JCheckBox("Zaznacz wszystkie"))
        ).toArray(JCheckBox[]::new);

        Arrays.stream(checkBox).forEach(weekDay -> weekDay.addItemListener(event -> {
            switch (event.getStateChange()){
                case ItemEvent.SELECTED -> {
                    if(event.getItem().equals(checkBox[7])) Arrays.stream(checkBox).forEach(op -> op.setSelected(true));
                    checkedWeekDays.add(weekDay.getText().toUpperCase());
                }
                case ItemEvent.DESELECTED -> {
                    if(event.getItem().equals(checkBox[7])) Arrays.stream(checkBox).forEach(op -> op.setSelected(false));
                    checkedWeekDays.remove(weekDay.getText().toUpperCase());
                }
            }
        }));

        checkedWeekDays.add(String.valueOf(JOptionPane.showOptionDialog(
                null,
                checkBox,
                "Wybierz dni tygodnia:",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                dialogOptions,
                dialogOptions[0]
        )));

        return checkedWeekDays;
    }

    private void refreshCalendar(){
        calendar.setModel(calendarManager.getCalendarModel());

        for(int column = 0; column < calendar.getColumnCount(); column++){
            calendar.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer(){
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                   var component =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                   setHorizontalAlignment(JLabel.CENTER);

                    if(column == 0 && row > 0 && row < calendar.getRowCount()-1){
                        var localTime = LocalTime.now().format(new DateTimeFormatterBuilder().append(DateTimeFormatter.ofPattern("HH")).toFormatter());
                        var calendarHour = value.toString().substring(0, 2);

                        if(localTime.equals(calendarHour)) component.setForeground(Color.black);
                        else component.setForeground(Color.gray);
                    } else if (LocalDate.now().getDayOfWeek().getValue() == column) component.setForeground(Color.black);
                    else component.setForeground(Color.gray);

                   return component;
                }
            });
        }

        pack();

        for (int column = 1; column < calendar.getColumnCount(); column++) {
            for(int row = 1; row < calendar.getRowCount()-1; row++){
                var value = calendar.getValueAt(row, column).toString();
                if(!value.contains("(0/"))
                    calendar.setValueAt(("<html><strike>" + value + "</strike></html>"), row, column);
            }
        }

        calendar.setVisible(true);
    }

    public static void main(String[] args) {
        var calendarFolder = new File(System.getProperty("user.home"), "/Kalendarz");

        while(!calendarFolder.exists()){
            var result = JOptionPane.showConfirmDialog(
                    null,
                    "<html><center>Program utworzy folder Kalendarz, w którym będą przechowywane pliki " +
                            "niezbędne do prawidłowego działania programu.<br>Czy zgadzasz się na utworzenie " +
                            "niezbędnego folderu w następującej lokalizacji: <u>" + calendarFolder.getParent() +
                            "</u>? <br><br>Wybierz tak, jeśli wyrażasz zgodę. <br>Wybierz nie, jeśli nie wyrażasz " +
                            "zgody lub chcesz wczytać już istniejący folder aplikacji.</center></html>",
                    "Tworzenie folderu Kalendarz",
                    JOptionPane.YES_NO_OPTION
            );

            if(result == JOptionPane.OK_OPTION) {
                try {
                    Files.createDirectory(calendarFolder.toPath());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Błąd w trakcie tworzenia folderu we wskazanej lokalizacji.\n" +
                                    "Proszę o wskazanie nowej lokalizacji dla folderu Kalendarz lub wybranie już istniejącego.",
                            "Błąd tworzenia folderu",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else if (result == JOptionPane.NO_OPTION || !calendarFolder.exists()){
                var folderChooser = new JFileChooser();
                folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                folderChooser.setLocale(Locale.of("pl", "PL"));

                if(JFileChooser.APPROVE_OPTION == folderChooser.showOpenDialog(null)){
                    var selectedPath = folderChooser.getSelectedFile();
                    if(!(selectedPath.toString().endsWith("Kalendarz") || selectedPath.getParent().endsWith("Kalendarz"))){
                        calendarFolder = new File(selectedPath + "/Kalendarz");
                    }
                }
            }
        }

        final var finalCalendarFolder = calendarFolder;
        SwingUtilities.invokeLater(() -> {
            Main.calendarFolder = finalCalendarFolder;
            new Main();
        });
    }
}