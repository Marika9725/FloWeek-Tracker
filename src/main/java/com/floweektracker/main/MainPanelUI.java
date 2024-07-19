package com.floweektracker.main;

import com.floweektracker.planner.PlannerTableManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;

/**
 * Represents the UI of the main panel displayed to the user after launching the application. It contains the main
 * components of the application: title, planner and buttons.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #plannerTableManager} - a reference to a new {@link PlannerTableManager} instance which contains the
 *     {@link javax.swing.table.DefaultTableModel} for the {@link #planner} and manages its data and updates</li>
 *     <li>{@link #mainPanel} - a reference to a new {@link JPanel} instance containing the main panel's components</li>
 *     <li>{@link #infoButton} - a {@link JButton} reference to the button that displays information about the app to
 *     the user</li>
 *     <li>{@link #planner} - a {@link JTable} reference containing the UI of the planner</li>
 *     <li>{@link #buttons} - an array of {@link JButton} references for buttons that allow interaction with the
 *     {@link #planner}</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #configureMainPanel()} - adds panels and components to the {@link #mainPanel} and configures it</li>
 *     <li>{@link #createInfoButton()} - creates the {@link #infoButton} to display information about the app</li>
 *     <li>{@link #createTitlePanel()} - creates a {@link JPanel} with the app's name and the {@link #infoButton}</li>
 *     <li>{@link #createButtonsPanel()} - creates a {@link JPanel} with the {@link #buttons} for interacting with the
 *     application</li>
 *     <li>{@link #createBottomInfo()} - creates a {@link JLabel} with information displayed at the bottom of the app
 *     </li>
 * </ul>
 * <p>Getters: {@link #getMainPanel()}, {@link #getInfoButton()}, {@link #getButtons()}, {@link #getPlanner()},
 * {@link #getPlannerTableManager()}</p>
 */
class MainPanelUI {
    //region Fields
    /**
     * Contains a new instance of the {@link PlannerTableManager} which contains the
     * {@link javax.swing.table.DefaultTableModel} for the {@link #planner} and manages its data and updates.
     *
     * @see #MainPanelUI()
     * @see #getPlannerTableManager()
     */
    private final PlannerTableManager plannerTableManager;
    /**
     * Contains an instance of the {@link JPanel} representing the main panel shown to the user initially after
     * launching the application. It displays the app's title, week planner, buttons, and information at the bottom of
     * the window.
     *
     * @see #MainPanelUI()
     * @see #configureMainPanel()
     * @see #getMainPanel()
     */
    private final JPanel mainPanel;
    /**
     * Contains an instance of {@link JButton} representing an info button that allows displaying information about the
     * planner and the database to the user.
     *
     * @see #MainPanelUI()
     * @see #createInfoButton()
     * @see #getInfoButton()
     * @see MainListeners
     */
    private final JButton infoButton;
    /**
     * Contains an instance of the {@link JTable} representing the UI of the user's planner.
     *
     * @see #MainPanelUI()
     * @see #configureMainPanel()
     * @see #getPlanner()
     * @see MainListeners
     */
    private final JTable planner;
    /**
     * Contains an array of {@link JButton} instances representing buttons: "Add task to planner", "Add task to
     * database", "Delete task", "Clean planner", and "Reset points". These buttons are displayed to the user in
     * {@link #mainPanel}.
     *
     * @see #MainPanelUI()
     * @see #createButtonsPanel()
     * @see #getButtons()
     * @see ButtonsText
     * @see MainListeners
     */
    private final JButton[] buttons;
    //endregion

    /**
     * Constructs a new {@link MainPanelUI} instance. This constructor also initializes references to the
     * {@link #plannerTableManager}, {@link #mainPanel}, {@link #infoButton}, {@link #planner}, and {@link #buttons}. At
     * the end, the constructor invokes the {@link #configureMainPanel()} method to configure the {@link #mainPanel}.
     *
     * @see #createInfoButton()
     * @see PlannerTableManager#getPlanner()
     * @see ButtonsText
     * @see Main
     */
    MainPanelUI() {
        this.plannerTableManager = new PlannerTableManager();
        this.mainPanel = new JPanel();
        this.infoButton = createInfoButton();
        this.planner = plannerTableManager.getPlanner();
        this.buttons = Arrays.stream(ButtonsText.values())
                .filter(buttonText -> buttonText != ButtonsText.RETURN)
                .map(buttonText -> new JButton(buttonText.getActionPL()))
                .toArray(JButton[]::new);

        configureMainPanel();
    }

    /**
     * Configures a {@link #mainPanel} by setting layout and name. It also adds components to it.
     * <br><br>
     * The main panel's layout is set to a vertical {@link BoxLayout}. The panel is then named "MainPanel" for
     * identification purposes. This method adds the following components to the main panel in order:
     * <ul>
     *     <li>A title panel, created by {@link #createTitlePanel()}</li>
     *     <li>The planner component, referenced by {@link #planner}</li>
     *     <li>A buttons panel, created by {@link #createButtonsPanel()}</li>
     *     <li>Bottom information, created by {@link #createBottomInfo()}</li>
     * </ul>
     *
     * @see #MainPanelUI()
     */
    private void configureMainPanel() {
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setName("MainPanel");

        mainPanel.add(createTitlePanel());
        mainPanel.add(planner);
        mainPanel.add(createButtonsPanel());
        mainPanel.add(createBottomInfo());
    }

    //region Auxiliary methods

    /**
     * Creates a {@link JButton} with an {@link ImageIcon} retrieved from resources. If the image is successfully
     * loaded, it will be scaled and set as the icon for the new {@link JButton} instance, which will then be configured
     * and returned. If loading the image fails, the user will see an {@link JOptionPane#ERROR_MESSAGE} and null will be
     * returned. If this method produces a {@link NullPointerException}, make sure that {@code info.png} is in the
     * resources.
     *
     * @return a {@link JButton} with the {@link ImageIcon}, or null if loading the {@link ImageIcon} fails.
     * @throws NullPointerException if the resource {@code /info.png} is not found.
     * @see #infoButton
     * @see #MainPanelUI()
     * @see #createTitlePanel()
     * @see MainListeners
     */
    private JButton createInfoButton() {
        JButton infoButton = null;
        try {
            var iconButton = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/info.png"))));
            infoButton = new JButton(new ImageIcon(iconButton.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))) {
                @Override
                protected void paintComponent(Graphics g) {
                    setContentAreaFilled(false);
                    setBorderPainted(false);
                    setFocusPainted(false);

                    g.drawImage(iconButton.getImage(), 0, 0, getWidth(), getHeight(), this);
                    super.paintComponent(g);
                }
            };

            infoButton.setPreferredSize(new Dimension(50, 50));
            infoButton.setActionCommand("Info");
        } catch (NullPointerException e) {
            Main.showErrorDialog("Błąd zasobów", "Plik /info.png nie został znaleziony w zasobach.");
        } catch (IOException e) {
            Main.showErrorDialog("Błąd ładowania ikony", "Nie udało się załadować ikony z informacjami o aplikacji.");
        }

        return infoButton;
    }

    /**
     * Creates and returns a {@link JPanel} containing the app's title represented by a {@link JLabel}, and the
     * {@link #infoButton}. This panel is used as a part of the user interface setup in the
     * {@link #configureMainPanel()} method.
     *
     * @return a {@link JPanel} displaying the app's title and the {@link #infoButton}
     * @see #createInfoButton()
     */
    private JPanel createTitlePanel() {
        var title = new JLabel("TYGODNIOWY PLANER");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        var titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
        titlePanel.add(infoButton);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(title);
        titlePanel.add(Box.createHorizontalGlue());

        return titlePanel;
    }

    /**
     * Creates and returns a {@link JPanel} containing {@link #buttons}, which are used for interaction with the
     * application. This panel is used as a part of the user interface setup in the {@link #configureMainPanel()}
     * method.
     *
     * @return a {@link JPanel} displaying interactive buttons.
     */
    private JPanel createButtonsPanel() {
        var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        Arrays.stream(buttons).forEach(buttonsPanel::add);

        return buttonsPanel;
    }

    /**
     * Creates and returns a {@link JLabel} containing instructions about how to access more details about application.
     * This panel is used as a part of the user interface setup in the {@link #configureMainPanel()} method.
     *
     * @return a {@link JPanel} displaying instructors for accessing more information about the app
     */
    private JLabel createBottomInfo() {
        var bottomInfo = new JLabel("Aby uzyskać więcej informacji, kliknij w ikonę \"i\" znajdującą się w lewym górnym rogu.");
        bottomInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        return bottomInfo;
    }
    //endregion
    //region Getters

    /**
     * @return a {@link #mainPanel} displayed to the user after launching the application
     * @see #configureMainPanel()
     * @see Main
     */
    JPanel getMainPanel() {return mainPanel;}

    /**
     * @return a {@link #infoButton} allowing to get information about the app.
     * @see #createInfoButton()
     * @see #createTitlePanel()
     * @see MainListeners
     */
    JButton getInfoButton() {return infoButton;}

    /**
     * @return {@link #buttons} representing operations that the user can perform with the application.
     * @see #configureMainPanel()
     * @see #createButtonsPanel()
     * @see ButtonsText
     * @see MainListeners
     */
    JButton[] getButtons() {return buttons;}

    /**
     * @return a {@link #planner} representing UI of the week planner
     * @see #configureMainPanel()
     * @see PlannerTableManager#getPlanner()
     * @see MainListeners
     */
    public JTable getPlanner() {return planner;}

    /**
     * @return a {@link PlannerTableManager} instance which contains the {@link javax.swing.table.DefaultTableModel} for
     * the {@link #planner}
     * @see MainListeners
     */
    public PlannerTableManager getPlannerTableManager() {return plannerTableManager;}
    //endregion
}