package com.floweektracker.view;

import com.floweektracker.MainFrame;
import com.floweektracker.model.WeekDays;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents the UI component responsible for displaying a dialog that allows the user to select the days of the week
 * for performing a cleaning operation. This class is a singleton.
 * <br><br>
 * Fields: {@link #cleanerView}, {@link #weekdaysCheckbox}, {2link #checkboxPanel}
 * <br><br>
 * Methods: {@link #showOptionDialog()}, {@link #createCheckboxPanel()}, {@link #createValues()}
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CleanerView {
    @Getter
    private static final CleanerView cleanerView = new CleanerView();
    private final List<JCheckBox> weekdaysCheckbox = createValues();
    private final JPanel checkboxPanel = createCheckboxPanel();

    /**
     * Shows a dialog allowing the user to select the days of the week for a cleaning operation. The dialog contains a
     * weekdays' checkboxes, confirm button and cancel button. The method returns the option selected by the user.
     * Before the dialog is shown, all checkboxes are unselected.
     *
     * @return an integer value representing the selected option
     * @see #createCheckboxPanel()
     * @see com.floweektracker.controller.MainPanelController#createCleanerListener(int)
     */
    public int showOptionDialog() {
        weekdaysCheckbox.forEach(box -> box.setSelected(false));

        return JOptionPane.showOptionDialog(
                MainFrame.getMAIN_FRAME(),
                checkboxPanel,
                "Wybierz dni tygodnia",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{"OK", "Anuluj"},
                "OK"
        );
    }

    /**
     * Creates a panel containing {@link #weekdaysCheckbox}. This panel is shown in the option dialog and allows the
     * user to select the days of the week for a cleaning operation.
     *
     * @return a {@link JPanel} containing {@link #weekdaysCheckbox}
     * @see #createValues()
     * @see #showOptionDialog()
     */
    @NotNull
    private JPanel createCheckboxPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        weekdaysCheckbox.forEach(panel::add);

        return panel;
    }

    /**
     * @return a list of {@link JCheckBox} objects representing the weekdays and checkbox allowing to select all
     * weekdays.
     */
    private List<JCheckBox> createValues() {
        return Stream.concat(
                Arrays.stream(WeekDays.values()).map(weekday -> new JCheckBox(weekday.getWeekdayPL())),
                Stream.of(new JCheckBox("Zaznacz wszystkie"))
        ).toList();
    }
}
