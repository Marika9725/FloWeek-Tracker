package com.floweektracker.view;

import com.floweektracker.MainFrame;
import com.floweektracker.model.WeekDays;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CleanerViewTest {
    private final CleanerView cleanerView = CleanerView.getCleanerView();
    private final List<JCheckBox> checkboxes = cleanerView.getWeekdaysCheckbox();

    @AfterEach
    void cleanUp() {
        for (JCheckBox checkbox : checkboxes) {
            checkbox.setSelected(false);
        }
    }

    @Test
    void shouldShowOptionDialog() {
        //given
        try (var mockedStatic = mockStatic(JOptionPane.class)) {
            mockedStatic
                    .when(() -> JOptionPane.showOptionDialog(any(), any(), anyString(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(JOptionPane.OK_OPTION);

            //when
            cleanerView.showOptionDialog();

            //then
            mockedStatic.verify(() -> JOptionPane.showOptionDialog(
                    eq(MainFrame.getMAIN_FRAME()),
                    any(JPanel.class),
                    eq("Wybierz dni tygodnia"),
                    eq(JOptionPane.DEFAULT_OPTION),
                    eq(JOptionPane.PLAIN_MESSAGE),
                    eq(null),
                    eq(new Object[]{"OK", "Anuluj"}),
                    eq("OK")));
        }
    }

    @Test
    void checkBoxesShouldNotBeSelectedAfterCallShowOptionDialog() {
        var checkboxes = cleanerView.getWeekdaysCheckbox();
        checkboxes.getFirst().setSelected(true);
        checkboxes.get(2).setSelected(true);

        try (var mockedStatic = mockStatic(JOptionPane.class)) {
            mockedStatic
                    .when(() -> JOptionPane.showOptionDialog(any(), any(), anyString(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(JOptionPane.OK_OPTION);

            cleanerView.showOptionDialog();

            checkboxes.forEach(box -> assertFalse(box.isSelected()));
        }
    }

    @Test
    void optionDialogShouldContainsWeekdays() {
        //given
        var checkbox = cleanerView.getWeekdaysCheckbox();

        //when
        var expectedTexts = Stream.concat(Stream.of(WeekDays.getWeekdaysPL()), Stream.of("Zaznacz wszystkie")).toList();

        var actualTexts = checkbox.stream().map(JCheckBox::getText).collect(toList());

        //then
        assertAll(
                () -> assertThat(actualTexts.size(), is(8)),
                () -> assertThat(actualTexts, contains(expectedTexts.toArray()))
        );
    }
}