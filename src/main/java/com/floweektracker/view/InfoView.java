package com.floweektracker.view;

import com.floweektracker.MainFrame;
import com.floweektracker.util.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;

/**
 * Represents UI of the information panel which shows to the user details about database or planner using.
 * <br><br>
 * Fields: {@link #infoType}, {@link #returnButton}
 * <br><br>
 * Methods: {@link #configureInfoView(String)}, {@link #createTitlePanel()}, {@link #createContentPanel()},
 * {@link #addTextToContentPanel(JPanel)}, {@link #createLine(String)}, {@link #createButtonPanel()},
 * {@link #createReturnButton()}
 */
public class InfoView extends JPanel {
    /**
     * {@link String} represents one of the two types of information: database or planner.
     *
     * @see InfoView(String)
     * @see #addTextToContentPanel(JPanel)
     */
    private final String infoType;
    /**
     * {@link JButton} which allows the user to return to the main panel.
     *
     * @see InfoView(String)
     * @see #createButtonPanel()
     * @see #createReturnButton()
     */
    private final JButton returnButton;

    /**
     * Public constructor of the class. Initializes the fields and calls the {@link #configureInfoView(String)} method.
     *
     * @param infoType a type of the information: database or planner
     * @see #configureInfoView(String)
     * @see #createReturnButton()
     * @see MainFrame#createCardPanel()
     */
    public InfoView(String infoType) {
        this.infoType = "/info%s.txt".formatted(infoType);
        this.returnButton = createReturnButton();

        configureInfoView(infoType);
    }

    /**
     * Configures {@link InfoView} by setting BorderLayout, and adding title panel, content panel and button panel.
     *
     * @param infoType a type of the information: database or planner
     * @see #createTitlePanel()
     * @see #createContentPanel()
     * @see #createButtonPanel()
     * @see #configureInfoView(String)
     */
    private void configureInfoView(String infoType) {
        this.setName(infoType);
        this.setLayout(new BorderLayout());
        this.add(createTitlePanel(), BorderLayout.NORTH);
        this.add(createContentPanel(), BorderLayout.CENTER);
        this.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    //region titlePanel

    /**
     * Creates title panel by calling {@link #createPanel(String)} and {@link #createLabel(String, String, int)}
     * methods. This panel contains title headline.
     *
     * @return a {@link JPanel} with title headline
     * @see #configureInfoView(String)
     */
    private JPanel createTitlePanel() {
        var titlePanel = createPanel("titlePanel");
        titlePanel.add(createLabel("titleLabel", "INFORMACJE", 40));

        return titlePanel;
    }
    //endregion

    //region contentPanel

    /**
     * Creates content panel by calling {@link #createPanel(String)} and {@link #addTextToContentPanel(JPanel)} methods
     * with information about database or planner.
     *
     * @return a {@link JPanel} containing information about database or planner
     * @see #configureInfoView(String)
     */
    private JPanel createContentPanel() {
        var panel = createPanel("contentPanel");
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return addTextToContentPanel(panel);
    }

    /**
     * Reads information from the file and adds it to the given panel by calling {@link #createLine(String)} method.
     * Reads information until it finds the "#END" line. Then it returns the panel. When there is an error, it shows a
     * message dialog to the user.
     *
     * @param panel a given panel to which information is added
     * @return a panel with information about database or planner
     * @see #createContentPanel()
     * @see DialogUtils#showMessageDialog(String, String)
     */
    private JPanel addTextToContentPanel(JPanel panel) {
        try (var br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(infoType))))) {
            String textLine;

            while (!(textLine = br.readLine()).equals("#END")) {
                panel.add(createLine(textLine));
            }
        } catch (NullPointerException | IOException e) {
            DialogUtils.showMessageDialog(
                    "Błąd odczytu pliku",
                    "Nie można odczytać pliku %s: %s".formatted(infoType, e.getMessage())
            );
        }

        return panel;
    }

    /**
     * Creates {@link JLabel} with the given text. If the text is empty, it adds a space. If the text starts with "#",
     * it adds a label with the given text without "#". Otherwise, it adds a label with the given text.
     *
     * @param textLine a given text returned as a {@link JLabel}
     * @return a {@link JLabel} with the given text
     * @see #addTextToContentPanel(JPanel)
     * @see #createContentPanel()
     */
    private JLabel createLine(String textLine) {
        var label = new JLabel();

        if (textLine.isEmpty()) label.setText(" ");
        else if (textLine.startsWith("#")) label = createLabel(null, textLine.substring(1), 20);
        else label.setText(textLine);

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }
    //endregion

    //region buttonPanel

    /**
     * Creates a {@link JPanel} with the {@link #returnButton} by calling {@link #createPanel(String)} method.
     *
     * @return a {@link JPanel} containing the {@link #returnButton}.
     * @see #configureInfoView(String)
     * @see #createPanel(String)
     * @see #createReturnButton()
     */
    private JPanel createButtonPanel() {
        var buttonPanel = createPanel("buttonPanel");
        buttonPanel.add(returnButton);

        return buttonPanel;
    }

    /**
     * Create a {@link JButton} that allows the user return to the main panel, and add action listener to it. .
     *
     * @return a {@link JButton} configured as a return button
     * @see #createButtonPanel()
     * @see MainFrame#switchCard(String)
     * @see InfoView(String)
     */
    private JButton createReturnButton() {
        var returnButton = new JButton("Powrót");
        returnButton.setName("returnButton");
        returnButton.addActionListener(_ -> MainFrame.getMAIN_FRAME().switchCard("mainPanel"));

        return returnButton;
    }
    //endregion

    /**
     * Creates a {@link JLabel} with the given name, text and font size. The font is set as "Arial" and "Bold".
     *
     * @param name     a name of the label
     * @param text     a text of the label
     * @param fontSize a size of the font
     * @return a {@link JLabel} with the given name, text and font.
     * @see #createTitlePanel() 
     * @see #createLine(String) 
     */
    private JLabel createLabel(String name, String text, int fontSize) {
        var label = new JLabel(text);
        if (name != null) label.setName(name);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));

        return label;
    }

    /**
     * Creates a {@link JPanel} with the given name. If the name is "contentPanel" it set the layout as
     * BoxLayout.Y_AXIS. Otherwise, it set the layout as FlowLayout.CENTER. It is template method used to create all
     * panels in the view.
     *
     * @param panelName a given name of the panel
     * @return a {@link JPanel} with the given name and configured layout.
     * @see #createTitlePanel()
     * @see #createContentPanel()
     * @see #createButtonPanel()
     */
    private JPanel createPanel(String panelName) {
        var panel = new JPanel();
        panel.setName(panelName);

        if (panelName.equals("contentPanel")) panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        else panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        return panel;
    }
}
