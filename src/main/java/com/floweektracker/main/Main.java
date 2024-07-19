package com.floweektracker.main;

import com.floweektracker.planner.*;
import com.floweektracker.tasksDatabase.TasksDatabaseManager;
import com.floweektracker.planner.PlannerDataManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Represents the {@link Main} instance and serves as the entry point for the application. It initializes main user
 * interface components and manages the application's lifecycle. Additionally, ths class handles the creation of
 * necessary directories for storing application data.
 * <br><br>
 * Fields:
 *  <ul>
 *       <li>{@link #plannerFolder} - a static {@link File} reference to the directory where application files are
 *       stored</li>
 *       <li>{@link #mainCards} - a {@link JPanel} reference using {@link CardLayout} to manage different panels in the
 *       application</li>
 *       <li>{@link #mainPanelUI} - a {@link MainPanelUI} reference representing the main user interface panel</li>
 *       <li>{@link #infoPanelsUI} - an array of {@link InfoPanelUI} references representing different informational
 *       panels in the application</li>
 *  </ul>
 *  Methods:
 * <ul>
 *     <li>{@link #createDirectory()} - creates directory where working files are stored</li>
 *     <li>{@link #configureFrame()} - sets up JFrame elements such as title, size, and visibility </li>
 *     <li>{@link #configureMainCards()} - adds all panels to {@link #mainCards}</li>
 *     <li>{@link #initializeListeners()} - creates a new instance of {@link MainListeners}, which adds listeners to
 *     the elements contained in {@code MainPanel} and {@code InfoPanel}</li>
 *     <li>{@link #showErrorDialog(String, String)} - creates and shows error dialog based on provided information</li>
 *     <li>{@link #setPanelSize(String)} - changes JFrame's size based on visible {@link JPanel} in {@link #mainCards}</li>
 * </ul>
 * Getters: {@link #getMainCards()}
 */
public class Main extends JFrame {
    //region Fields
    /**
     * Contains a {@link File} instance representing the FloWeekTracker directory, where application files are stored.
     * This variable is used to save or load data from .json files located in the FloWeekTracker directory.
     *
     * @see #createDirectory()
     * @see PlannerDataManager
     * @see TasksDatabaseManager
     */
    public static File plannerFolder;
    /**
     * Contains multiple {@link JPanel} instances managed by a {@link CardLayout} which allows displaying one panel at a
     * time. The user can switch between these {@link JPanel} instances to interact with application. Contains:
     * <ul>
     *     <li>one panel from {@link MainPanelUI} representing the main panel shown to the user at the beginning</li>
     *     <li>two panels from {@link InfoPanelUI} representing information about the base and planner usage</li>
     *     <li>seven panels from {@link WeekDayPanelUI} representing the schedule for each day of the week</li>
     * </ul>
     *
     * @see #configureMainCards()
     * @see #setPanelSize(String)
     */
    private final JPanel mainCards;
    /**
     * Contains an instance of {@link MainPanelUI} which is used to create and deliver the main panel to
     * {@link #mainCards}.
     *
     * @see #configureMainCards()
     * @see #initializeListeners()
     */
    private final MainPanelUI mainPanelUI;
    /**
     * Contains instances of {@link InfoPanelUI} used creating and delivering panels with information to
     * {@link #mainCards}
     *
     * @see #configureMainCards()
     * @see #initializeListeners()
     */
    private final InfoPanelUI[] infoPanelsUI;
//endregion

    /**
     * Constructs a new {@link Main} instance serving as the entry point for the application. Creates a directory for
     * application's working files using the {@link #createDirectory()} method and then initializes the
     * {@link #mainPanelUI}, {@link #infoPanelsUI} and {@link #mainCards} instances.
     * <br><br>
     * Invokes the {@link #configureMainCards()} method to add panels to {@link #mainCards}, and the
     * {@link #initializeListeners()} method to add listeners to the components in the information panels and the main
     * panel. Finally, the constructor uses {@link #configureFrame()} to configure the {@link JFrame}.
     *
     * @see Main
     * @see MainPanelUI
     * @see InfoPanelUI
     */
    public Main() {
        plannerFolder = createDirectory();
        if (plannerFolder == null) System.exit(0);
        this.mainPanelUI = new MainPanelUI();
        this.infoPanelsUI = new InfoPanelUI[]{
                new InfoPanelUI("Planer"),
                new InfoPanelUI("Baza")
        };
        this.mainCards = new JPanel(new CardLayout());

        configureMainCards();
        initializeListeners();
        configureFrame();
    }

    //#region Internal methods

    /**
     * Creates a FloWeekTracker directory, where necessary application's files are stored. It creates a
     * {@link JOptionPane} confirm dialog with {@link JOptionPane#YES_NO_CANCEL_OPTION}, where user is informed about
     * creating the directory in the default localization and is asked for permission.<br><br>
     * <p>If the user clicks {@link JOptionPane#YES_OPTION}, the directory will be created.</p>
     * <p>If the user clicks {@link JOptionPane#NO_OPTION}, the user can choose another localization for folder or
     * select one that already exists.</p>
     * <p>If the user clicks {@link JOptionPane#CANCEL_OPTION}, the program will exit without creating the
     * directory.</p>
     *
     * @return a {@link File} object representing the FloWeekTracker directory or {@code null} if the user cancels the
     * operation.
     * @see #plannerFolder
     * @see #Main()
     */
    private File createDirectory() {
        var plannerFolder = new File(System.getProperty("user.home"), "/FloWeekTracker");

        while (!plannerFolder.exists()) {
            var result = JOptionPane.showConfirmDialog(
                    null,
                    "<html><center>Program utworzy folder FloWeekTracker, w którym będą przechowywane pliki " +
                            "niezbędne do prawidłowego działania programu.<br>Czy zgadzasz się na utworzenie " +
                            "niezbędnego folderu w następującej lokalizacji: <u>" + plannerFolder.getParent() +
                            "</u>? </br><br>Wybierz tak, jeśli wyrażasz zgodę.</br><br>Wybierz nie, jeśli nie wyrażasz " +
                            "zgody, ale chcesz utworzyć folder w innej lokalizacji lub wczytać już istniejący.</br>" +
                            "<br>Wybierz anuluj, jeśli nie chcesz tworzyć folderu, ani wczytać istniejącego. Spowoduje" +
                            " to wyjście z programu.</br></center></html>",
                    "Tworzenie folderu FloWeekTracker",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );

            switch (result) {
                case JOptionPane.YES_OPTION -> {
                    try {
                        Files.createDirectory(plannerFolder.toPath());
                        return plannerFolder;
                    } catch (IOException e) {
                        showErrorDialog(
                                "Błąd tworzenia folderu",
                                "Błąd w trakcie tworzenia folderu we wskazanej lokalizacji.\n" +
                                        "Proszę o wskazanie nowej lokalizacji dla folderu FloWeekTracker lub wybranie już istniejącego."
                        );
                    }
                }
                case JOptionPane.NO_OPTION -> {
                    var folderChooser = new JFileChooser();
                    folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    folderChooser.setLocale(Locale.of("pl", "PL"));

                    if (JFileChooser.APPROVE_OPTION == folderChooser.showOpenDialog(null)) {
                        var selectedPath = folderChooser.getSelectedFile();
                        if (!(selectedPath.toString().endsWith("FloWeekTracker") || selectedPath.getParent().endsWith("FloWeekTracker"))) {
                            plannerFolder = new File(selectedPath + "/FloWeekTracker");
                        }
                    }
                }
                case JOptionPane.CANCEL_OPTION -> {return null;}
            }
        }

        return plannerFolder;
    }

    /**
     * Adds {@link JPanel} instances from {@link #mainPanelUI} and {@link #infoPanelsUI} to {@link #mainCards}.
     *
     * @see MainPanelUI#getMainPanel()
     * @see InfoPanelUI#getInfoPanel()
     * @see #Main()
     */
    private void configureMainCards() {
        mainCards.setDoubleBuffered(true);
        mainCards.add(mainPanelUI.getMainPanel(), "MainPanel");
        Arrays.stream(infoPanelsUI).forEach(infoPanelUI -> mainCards.add(infoPanelUI.getInfoPanel(), infoPanelUI.getInfoType()));
    }

    /**
     * Creates a new instance of {@link MainListeners}, which creates, delivers and adds listeners to the elements
     * contained in the {@link #mainPanelUI} and {@link #infoPanelsUI} instances.
     *
     * @see #Main()
     */
    private void initializeListeners() {new MainListeners(this, mainPanelUI, infoPanelsUI);}

    /**
     * Configures app's frame by adding {@link #mainCards} to {@link JFrame}. Also sets title, app's icon, size,
     * resizability, frame's location closing operation and frame's visibility.<br><br>
     * <p>If the app's icon cannot be set, an error dialog is displayed informing the user that the icon could not be
     * added.</p>
     *
     * @see #setPanelSize(String)
     * @see #Main()
     */
    private void configureFrame() {
        add(mainCards, BorderLayout.CENTER);
        setTitle("FloWeek Tracker");
        try {
            setIconImage(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/appIcon.jpg")))).getImage());
        } catch (IOException e) {
            showErrorDialog("Błąd dodawania ikony aplikacji", "Nie udało się dodać ikony do aplikacji");
        }
        setPanelSize("MainPanel");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    //endregion

    //#region External methods

    /**
     * Creates a {@link JOptionPane#ERROR_MESSAGE} based on provided information about {@code title} and
     * {@code message}. This method is used to show error messages to the user.
     *
     * @param title   a title for the {@link JOptionPane}
     * @param message a message which is shown to the user
     */
    public static void showErrorDialog(final String title, final String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Sets the size of the {@link JFrame} based on the {@code panelName}. Filters the components from
     * {@link Main#mainCards} to find a {@link JPanel} component with name matching the {@code panelName}. Gets the size
     * of the filtered {@link JPanel} and sets it for {@link Main#mainCards}.
     *
     * @param panelName a {@link String} containing the name of the panel from which the {@link JFrame} size is set
     */
    public void setPanelSize(final String panelName) {
        var cardPanels = mainCards.getComponents();

        Arrays.stream(cardPanels)
                .filter(cardPanel -> (cardPanel.getName()).equals(panelName))
                .forEach(cardPanel -> mainCards.setPreferredSize(cardPanel.getPreferredSize()));

        pack();
    }
    //endregion

    //region Getters

    /**
     * @return a {@link #mainCards} containing panels from the {@link #mainPanelUI} and {@link #infoPanelsUI} instances.
     *
     * @see #configureMainCards()
     */
    public JPanel getMainCards() {return mainCards;}
    //endregion

    /**
     * The main method which serves as the entry point to the application. It initializes the application by creating an
     * instance of the {@link Main} class.
     *
     * @param args command line arguments passed to the application (no used)
     */
    public static void main(String[] args) {SwingUtilities.invokeLater(Main::new);}
}