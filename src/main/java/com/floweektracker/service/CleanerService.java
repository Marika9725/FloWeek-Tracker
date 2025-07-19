package com.floweektracker.service;

import com.floweektracker.model.WeekDays;
import com.floweektracker.view.CleanerView;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

/**
 * Service for the {@link CleanerView}. It creates and adds listeners to the checkboxes and also allows to get a list of
 * selected weekdays.
 * <br><br>
 * Fields: {@link #service}, {@link #cleaner}
 * <br><br>
 * Methods: {@link #createCheckboxListener()},
 * {@link #selectCheckboxes(ItemEvent, boolean)}, {@link #getSelectedWeekdays()}
 */
@Getter
public class CleanerService {
    @Getter
    private static final CleanerService service = new CleanerService();
    private final CleanerView cleaner = CleanerView.getCleanerView();

    /**
     * Private constructor for a singleton cleaner service, which adds listeners to the checkboxes in the
     * {@link #cleaner} if they don't have any.
     *
     * @see #createCheckboxListener()
     */
    private CleanerService() {
        var checkbox = cleaner.getWeekdaysCheckbox();

        checkbox.stream()
                .filter(box -> box.getItemListeners().length == 0)
                .forEach(box -> box.addItemListener(createCheckboxListener()));
    }

    //region checkBoxListener
    /**
     * Creates an {@link ItemListener} for the checkboxes in the {@link #cleaner}. It allows to select or deselect the
     * checkboxes.
     *
     * @return an {@link ItemListener} for the checkboxes
     * @see #selectCheckboxes(ItemEvent, boolean)
     * @see CleanerService()
     */
    private ItemListener createCheckboxListener() {
        return event -> {
            switch (event.getStateChange()) {
                case ItemEvent.SELECTED -> selectCheckboxes(event, true);
                case ItemEvent.DESELECTED -> selectCheckboxes(event, false);
            }
        };
    }

    /**
     * Selects or deselects checkboxes depending on the event and the {@code setSelected} parameter. It breaks, if the
     * given event is null. If the event contains the string "Zaznacz wszystkie", it changes selectable status in all
     * checkboxes as {@code setSelected} value. Otherwise, it changes status only in provided checkboxes by
     * {@code event}.
     *
     * @param event       an event, which indicates that a checkbox was selected or deselected
     * @param setSelected a status which should be set in provided checkboxes
     * @see #createCheckboxListener()
     */
    private void selectCheckboxes(@NotNull ItemEvent event, boolean setSelected) {
        var areAllOptionsSelected = event.getItem().toString().contains("Zaznacz wszystkie");

        if (areAllOptionsSelected) {
            cleaner.getWeekdaysCheckbox().forEach(box -> {
                box.setSelected(setSelected);
                box.revalidate();
                box.repaint();
            });
        } else ((JCheckBox) event.getItemSelectable()).setSelected(setSelected);
    }
    //endregion

    /**
     * @return a list of selected {@link WeekDays} or null when no checkboxes are selected.
     * @see com.floweektracker.controller.MainPanelController#createCleanerListener(int)
     */
    public List<WeekDays> getSelectedWeekdays() {
        List<WeekDays> selectedWeekdays;

        var weekdaysFromCheckbox = cleaner.getWeekdaysCheckbox().stream()
                .filter(AbstractButton::isSelected)
                .map(AbstractButton::getText)
                .toList();

        if (weekdaysFromCheckbox.isEmpty()) return null;

        selectedWeekdays = weekdaysFromCheckbox.contains("Zaznacz wszystkie")
                ? WeekDays.getListedWeekdays()
                : weekdaysFromCheckbox.stream().map(WeekDays::valueOfPL).toList();

        return selectedWeekdays;
    }
}
