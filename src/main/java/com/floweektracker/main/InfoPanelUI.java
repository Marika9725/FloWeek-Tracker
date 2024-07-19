package com.floweektracker.main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;

/**
 * Represents UI of the {@link #infoPanel}, which shows to the user information about database or planner using.
 * <br><br>
 * Fields:
 * <ul>
 *     <li>{@link #infoPanel} - contains all components for user interface panel information</li>
 *     <li>{@link #infoType} - contains path to the one of the .txt files with information about database or planner
 *     using</li>
 *     <li>{@link #returnButton} - allows to return to the {@link JPanel} from the {@link MainPanelUI} instance.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link #configureInfoPanel()} - sets layout and adds components to the {@link #infoPanel}</li>
 *     <li>{@link #createTitlePanel()} - creates panel with the panel's title</li>
 *     <li>{@link #createContentPanel()} - creates panel with content</li>
 *     <li>{@link #createButtonPanel()} - creates panel with return button</li>
 * </ul>
 *
 * <p>Getters: {@link #getInfoType()}, {@link #getInfoPanel()}, {@link #getReturnButton()}</p>
 */
class InfoPanelUI {
    //region Fields
    /**
     * Contains all components for user interface panel information: title, information and return button.
     *
     * @see #configureInfoPanel()
     * @see #getInfoPanel()
     */
    private final JPanel infoPanel;
    /**
     * Contains path to the one of the .txt files with information about database or planner using.
     *
     * @see #InfoPanelUI(String)
     * @see #createContentPanel()
     * @see #getInfoType()
     */
    private final String infoType;
    /**
     * Contains button allowing to return to the {@code mainPanel} from a {@link MainPanelUI} instance.
     *
     * @see #InfoPanelUI(String)
     * @see #createButtonPanel()
     * @see #getReturnButton()
     */
    private final JButton returnButton;
//endregion

    /**
     * Constructs a new {@link InfoPanelUI} instance with the specified {@code infoType}. This constructor also
     * initializes references to the {@link #infoPanel}, {@link #infoType} and {@link #returnButton}. At the end,
     * constructor uses a {@link #configureInfoPanel()} method to sets layout and adds components to the
     * {@link #infoPanel}.
     *
     * @param infoType stores type of information, which are shown to the user in created new {@link #infoPanel}
     *                 instance.
     */
    InfoPanelUI(String infoType) {
        this.infoPanel = new JPanel();
        this.infoType = ("/info" + infoType + ".txt");
        this.returnButton = new JButton("Powrót");

        configureInfoPanel();
    }

    /**
     * Configures an {@link #infoPanel} instance setting layout and adding {@link JPanel}'s from auxiliary methods.
     *
     * @see #createTitlePanel()
     * @see #createContentPanel()
     * @see #createButtonPanel()
     */
    private void configureInfoPanel() {
        infoPanel.setLayout(new BorderLayout());
        infoPanel.add(createTitlePanel(), BorderLayout.NORTH);
        infoPanel.add(createContentPanel(), BorderLayout.CENTER);
        infoPanel.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    //region Auxiliary methods

    /**
     * Creates and returns {@link JPanel} container with {@link JLabel} titled "INFORMACJE". It is the first of three
     * panels added to the {@link #infoPanel}
     *
     * @return a {@link JPanel} containing the {@code titleLabel}.
     * @see #configureInfoPanel()
     * @see #createContentPanel()
     * @see #createButtonPanel()
     */
    private JPanel createTitlePanel() {
        var titleLabel = new JLabel("INFORMACJE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));

        var titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    /**
     * Creates and returns {@link JPanel} with {@link JLabel} containing information loaded from {@link #infoType} path.
     * This panel is the second of three panels which are added to the {@link #infoPanel}.
     *
     * @return a {@link JPanel} containing the {@code textLabel}'s.
     * @throws NullPointerException if the resource stream for {@link #infoType} is null
     * @see #configureInfoPanel()
     * @see #createTitlePanel()
     * @see #createButtonPanel()
     */
    private JPanel createContentPanel() {
        var contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        try (var br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(infoType))))) {
            String textLine;
            JLabel textLabel;

            while (!(textLine = br.readLine()).equals("#END")) {
                if (textLine.contains("#")) {
                    textLabel = new JLabel(textLine.substring(1));
                    textLabel.setFont(new Font("Arial", Font.BOLD, 20));
                } else if (textLine.isEmpty()) textLabel = new JLabel(" ");
                else textLabel = new JLabel(textLine);

                textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(textLabel);
            }
        } catch (NullPointerException | IOException e) {
            Main.showErrorDialog("Błąd odczytu pliku", "Nie można odczytać pliku " + infoType + ": " + e.getMessage());
        }

        return contentPanel;
    }

    /**
     * Creates a {@link JPanel} with the {@link #returnButton} allowing to return to the {@code mainPanel} from the
     * {@link MainPanelUI}. This panel is the third of three panels which are added to the
     * {@link InfoPanelUI#infoPanel}.
     *
     * @return a {@link JPanel} containing the {@link #returnButton}.
     * @see #configureInfoPanel()
     * @see #createTitlePanel()
     * @see #createContentPanel()
     */
    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(returnButton);

        return buttonPanel;
    }
//endregion

    //region Getters

    /**
     * @return a {@link JScrollPane} containing the {@link #infoPanel}
     * @see #configureInfoPanel()
     */
    JScrollPane getInfoPanel() {
        var infoScrollPane = new JScrollPane(infoPanel);
        infoScrollPane.setName(getInfoType());
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return infoScrollPane;
    }

    /**
     * @return the {@link #infoType} as a {@link String} without prefix "/info" ant the file extension .txt.
     */
    String getInfoType() {return infoType.substring(("/info").length(), infoType.indexOf('.'));}

    /**
     * @return a {@link #returnButton} used to return to the {@code MainPanel} in {@link CardLayout}
     * @see #createButtonPanel()
     * @see MainListeners
     */
    JButton getReturnButton() {return returnButton;}
    //endregion
}