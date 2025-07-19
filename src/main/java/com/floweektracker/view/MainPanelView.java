package com.floweektracker.view;

import com.floweektracker.service.PlannerService;
import com.floweektracker.util.DialogUtils;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * Represents the UI of the main panel displayed to the user after launching the application. It contains the main
 * components of the application: title, planner and buttons.
 * <br><br>
 * Fields: {@link #view}, {@link #buttons}, {@link #infoButton}, {@link #planner}
 * <br><br>
 * Methods: {@link #configureMainPanel()}, {@link #createTitlePanel()}, {@link #createButtonsPanel()},
 * {@link #createButtons()}, {@link #createBottomInfo()}, {@link #createInfoButton()}, {@link #createTitleLabel()},
 * {@link #createButton(String, String)}, {@link #createPanel(String, LayoutManager, int)},
 * {@link #createLabel(String, String)}
 */
@Getter
public class MainPanelView extends JPanel {
    /**
     * Represents the singleton instance of the {@link MainPanelView} class.
     *
     * @see com.floweektracker.MainFrame
     * @see #MainPanelView()
     */
    @Getter
    private static final MainPanelView view = new MainPanelView();
    /**
     * Contains a {@link JList} with adding, removing, cleaning and resetting buttons {@link JButton}s.
     *
     * @see #createButtons()
     * @see com.floweektracker.controller.MainPanelController
     * @see #createButtonsPanel()
     */
    private final List<JButton> buttons = createButtons();
    /**
     * Contains a {@link JButton} representing an info button that allows to display information about the planner and
     * the database to the user.
     *
     * @see #createInfoButton()
     * @see com.floweektracker.controller.MainPanelController
     * @see #createTitlePanel()
     */
    private final JButton infoButton = createInfoButton();
    /**
     * Contains a {@link JTable} representing the UI of the user's planner.
     *
     * @see PlannerView
     * @see #configureMainPanel()
     */
    private final JTable planner = PlannerService.getService().getPlanner();

    /**
     * Private constructor which calling {@link #configureMainPanel()} method.
     *
     * @see MainPanelView
     */
    private MainPanelView() {
        configureMainPanel();
    }

    /**
     * Configures the {@link #view} by setting layout and name, and then adding components to it: title panel,
     * {@link #planner}, buttons panel and bottom information.
     *
     * @see #MainPanelView()
     * @see #createTitlePanel()
     * @see #createButtonsPanel()
     * @see #createBottomInfo()
     */
    private void configureMainPanel() {
        this.setName("MainPanel");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(createTitlePanel());
        this.add(planner);
        this.add(createButtonsPanel());
        this.add(createBottomInfo());
    }

    //region titlePanel creation
    /**
     * Creates a {@link JPanel} with the application name, and the {@link #infoButton}, by using
     * {@link #createPanel(String, LayoutManager, int)} and {@link #createTitleLabel()} methods.
     *
     * @return a {@link JPanel} with the application name and the {@link #infoButton}
     * @see #configureMainPanel()
     */
    private JPanel createTitlePanel() {
        var titlePanel = createPanel("TitlePanel", null, BoxLayout.LINE_AXIS);
        titlePanel.add(infoButton);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(createTitleLabel());
        titlePanel.add(Box.createHorizontalGlue());

        return titlePanel;
    }

    /**
     * @return a {@link JLabel} with the application name.
     * @see #createTitlePanel()
     * @see #createLabel(String, String)
     */
    private JLabel createTitleLabel() {
        var title = createLabel("titleLabel", "TYGODNIOWY PLANER");
        title.setFont(new Font("Arial", Font.BOLD, 40));

        return title;
    }

    /**
     * Creates {@link JButton} representing an info button allowing to display information about using planner and
     * database. It reads icon from info.png file in resources and then scales it to 50x50 pixels. When it throws an
     * exception, it creates simple button and shows a message to the user.
     *
     * @return an info button with scaled icon
     * @see MainPanelView
     * @see #configureMainPanel()
     */
    private JButton createInfoButton() {
        var infoButton = new JButton();

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
        } catch (NullPointerException | IOException e) {
            infoButton = new JButton("Informacje");
            DialogUtils.showMessageDialog(
                    "Błąd wyczytywania grafiki",
                    "Nie udało się wczytać grafiki dla przycisku: src/main/resources/info.png"
            );
        } finally {
            infoButton.setPreferredSize(new Dimension(50, 50));
            infoButton.setName("infoButton");
        }

        return infoButton;
    }
    //endregion

    //region buttonsPanel creation
    /**
     * Creates a {@link JPanel} with {@link #buttons} using {@link #createPanel(String, LayoutManager, int)} method.
     *
     * @return a {@link JPanel} with {@link #buttons}
     * @see #configureMainPanel()
     * @see #createButtons()
     */
    private JPanel createButtonsPanel() {
        var buttonsPanel = createPanel("ButtonsPanel", new FlowLayout(FlowLayout.CENTER, 20, 20), -1);
        buttons.forEach(buttonsPanel::add);

        return buttonsPanel;
    }

    /**
     * @return a created {@link JList} with the adding, removing, cleaning, adn resetting {@link JButton}s.
     * @see #buttons
     * @see com.floweektracker.controller.MainPanelController
     */
    private List<JButton> createButtons() {
        var buttons = new ArrayList<JButton>();
        buttons.add(createButton("addButton", "Dodaj zadanie"));
        buttons.add(createButton("addTaskNameButton", "Dodaj zadanie do bazy"));
        buttons.add(createButton("deleteButton", "Usuń zadanie"));
        buttons.add(createButton("cleanScheduleButton", "Wyczyść planer"));
        buttons.add(createButton("resetPointsButton", "Wyzeruj punkty"));

        return buttons;
    }

    /**
     * Creates a pattern of the {@link JButton} with the given name and text.
     *
     * @param name a given name of the button
     * @param text a given text of the button
     * @return a {@link JButton} with the given name and text
     * @see #createButtons()
     */
    private JButton createButton(String name, String text) {
        var button = new JButton(text);
        button.setName(name);

        return button;
    }
    //endregion

    /**
     * Creates a {@link JLabel} with information displaying at the bottom of the {@link #view}, which explain how to get
     * more details about using application.
     *
     * @return a created {@link JLabel} with information displaying to the user
     * @see #createLabel(String, String)
     * @see #configureMainPanel()
     */
    private JLabel createBottomInfo() {
        var bottomInfo = createLabel(
                "bottomInfo",
                "Aby uzyskać więcej informacji, kliknij w ikonę %s znajdującą się w lewym górnym rogu.".formatted("\"i\"")
        );
        bottomInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        return bottomInfo;
    }

    //region helper methods
    /**
     * Creates pattern of the {@link JPanel} with the given name, layout manager and axis. If the layout is not null, it
     * sets the given layout. Otherwise, it creates a {@link BoxLayout} with the given axis.
     *
     * @param name   a given name of the panel
     * @param layout a given layout manager
     * @param axis   a given axis of the {@link BoxLayout}
     * @return a pattern of the {@link JPanel} with the given and layout
     * @see #createTitlePanel()
     * @see #createButtonsPanel()
     */
    private JPanel createPanel(String name, LayoutManager layout, int axis) {
        var panel = new JPanel();
        panel.setName(name);

        if (layout != null) panel.setLayout(layout);
        else panel.setLayout(new BoxLayout(panel, axis));

        return panel;
    }

    /**
     * Creates a {@link JLabel} with a given name and text. It sets alignment to the center. Created label is a pattern
     * for creating labels in this class.
     *
     * @param name a name of the label
     * @param text a text of the label
     * @return a {@link JLabel} with the given name and text
     */
    private JLabel createLabel(String name, String text) {
        var label = new JLabel(text);
        label.setName(name);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        return label;
    }
    //endregion
}