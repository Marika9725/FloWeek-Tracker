package com.floweektracker;

import com.floweektracker.controller.*;
import com.floweektracker.model.WeekDays;
import com.floweektracker.service.*;
import com.floweektracker.util.DialogUtils;
import com.floweektracker.view.*;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

/**
 * Represents the main window of the application. It contains all needed panels and controller of the main panel. It is
 * a singleton.
 * <br><br>
 * Fields: {@link #MAIN_FRAME}, {@link #mainPanelController}, {@link #cardPanel}
 * <br><br>
 * Methods: {@link #initFrame}, {@link #createCardPanel()}, {@link #addIconImage()},
 * {@link #addWindowListenerToFrame()}, {@link #switchCard(String)}, {@link #isWeekdayPanelVisible(String)}
 */
@Getter
public class MainFrame extends JFrame {
    /**
     * Singleton instance of {@link MainFrame}.
     *
     * @see #initFrame()
     */
    @Getter
    private static final MainFrame MAIN_FRAME = new MainFrame();
    private final MainPanelController mainPanelController = new MainPanelController(MainPanelView.getView(), new TaskAddingDialogController(new TaskAddingDialog()));
    /**
     * A {@link JPanel} configured with a {@link CardLayout} containing all panels of the application.
     *
     * @see #createCardPanel()
     */
    private final JPanel cardPanel = createCardPanel();

    /**
     * Constructor of the {@link MainFrame} which configures the frame by calling {@link #initFrame()}.
     */
    private MainFrame() {
        initFrame();
    }

    /**
     * Configures frame by setting title, icon, resizability, closing operation, content pane as {@link #cardPanel},
     * location and visibility. It also adds a {@link WindowAdapter} that defines the behaviour when the window is
     * closed.
     *
     * @see #MainFrame()
     * @see #addIconImage()
     * @see #addWindowListenerToFrame()
     */
    private void initFrame() {
        setTitle("FloWeek Tracker");
        addIconImage();
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListenerToFrame();
        setContentPane(cardPanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Create an {@link ImageIcon} from the specified image file and sets it as the icon image of the frame. If it
     * throws an exception, it shows a message to the user.
     *
     * @see #initFrame()
     * @see DialogUtils#showMessageDialog(String, String)
     */
    private void addIconImage() {
        try {
            var imageURL = Objects.requireNonNull(getClass().getResource("/appIcon.jpg"));
            var bufferedImage = ImageIO.read(imageURL);
            var imageIcon = new ImageIcon(bufferedImage).getImage();
            setIconImage(imageIcon);
        } catch (IOException e) {
            DialogUtils.showMessageDialog(
                    "Błąd dodawania ikony aplikacji",
                    "Nie udało się dodać ikony do aplikacji"
            );
        }
    }

    /**
     * Creates and configures {@link #cardPanel} which contains all panels of the application. It contains the
     * {@link MainPanelView}, {@link WeekdayPlannerView} for all {@link WeekDays} and the {@link InfoView} for the
     * database and planner.
     *
     * @return a configured {@link JPanel} as a card panel with all application panels
     * @see #initFrame()
     */
    private JPanel createCardPanel() {
        var cardPanel = new JPanel(new CardLayout());
        cardPanel.setName("cardPanel");
        cardPanel.setDoubleBuffered(true);

        cardPanel.add(mainPanelController.getView(), "mainPanel");
        WeekDays.getListedWeekdays().forEach(weekday -> cardPanel.add(new WeekdayPlannerView(weekday), "%sPanel".formatted(weekday.name().toLowerCase())));
        cardPanel.add(new InfoView("Baza"), "bazaPanel");
        cardPanel.add(new InfoView("Planer"), "planerPanel");

        return cardPanel;
    }

    /**
     * Creates and adds to the frame a {@link WindowAdapter} that defines the behaviour when the window is closed. If
     * user closes window, data is saved by {@link TasksService#savePlanner()} and
     * {@link TaskNamesService#saveTaskNames()} to the json files and the application is terminated.
     *
     * @see #initFrame()
     */
    private void addWindowListenerToFrame() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                TasksService.getService().savePlanner();
                TaskNamesService.getService().saveTaskNames();
                MAIN_FRAME.dispose();
                System.exit(0);
            }
        });
    }

    //region switchCard() method
    /**
     * Displays the panel from the {@link #cardPanel} which name corresponds to the given {@code panelName}. Then checks
     * if the panel is visible. When given panel name is null or doesn't end with "Panel", the method returns false.
     *
     * @param panelName a {@link @String} representing the name of the panel to be shown
     * @return true if the given panel is visible, otherwise false
     * @see #isWeekdayPanelVisible(String)
     * @see MainPanelController#createInfoButtonListener()
     * @see PlannerController#openFullWeekdaySchedule()
     * @see InfoView#createReturnButton()
     * @see WeekdayPlannerView#createReturnButton()
     */
    public boolean switchCard(String panelName) {
        if (panelName == null || !panelName.endsWith("Panel")) return false;

        ((CardLayout) cardPanel.getLayout()).show(cardPanel, panelName);

        return isWeekdayPanelVisible(panelName);
    }

    /**
     * Checks if the panel with the given {@code panelName} is visible.
     *
     * @param panelName a given panel name which corresponding panel should be visible
     * @return true if the panel is visible, otherwise false
     */
    private boolean isWeekdayPanelVisible(String panelName) {
        return Arrays.stream(cardPanel.getComponents())
                .anyMatch(comp -> comp.getName().equalsIgnoreCase(panelName) && comp.isVisible());
    }
    //endregion
}
