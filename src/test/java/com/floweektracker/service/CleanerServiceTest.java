package com.floweektracker.service;

import com.floweektracker.model.WeekDays;
import com.floweektracker.view.CleanerView;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CleanerServiceTest {
    private final CleanerService cleanerService = CleanerService.getService();
    private final CleanerView cleanerView = CleanerView.getCleanerView();
    private final List<JCheckBox> checkboxes = cleanerView.getWeekdaysCheckbox();

    @AfterEach
    void cleanUp() {
        checkboxes.forEach(box -> box.setSelected(false));
    }

    @Nested
    class ConstructorTests {
        @Test
        void shouldReturnSameInstances() {
            assertThat(CleanerService.getService(), sameInstance(CleanerService.getService()));
        }

        @Test
        void listenersShouldBeAddedToCleanerView() {
            //given+when
            var checkboxNumListeners = checkboxes.stream()
                    .mapToInt(box -> box.getItemListeners().length)
                    .toArray();

            //then
            Arrays.stream(checkboxNumListeners).forEach(num -> assertThat(num, is(1)));
        }
    }

    @Nested
    class CleanerCheckBoxListenerTests {
        @Test
        void shouldSelectCheckboxesOnlyChosenByUser() {
            //given
            var areOptionsCheckedBefore = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);

            //when
            checkboxes.getFirst().setSelected(true);

            var areOptionsCheckedAfter = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);

            //then
            assertAll(
                    () -> Arrays.stream(areOptionsCheckedBefore).forEach(Assertions::assertFalse),
                    () -> assertTrue(areOptionsCheckedAfter[0]),
                    () -> Arrays.stream(areOptionsCheckedAfter).skip(1).forEach(Assertions::assertFalse)
            );
        }

        @Test
        void shouldDeselectCheckboxesOnlyChosenByUser() {
            //given
            checkboxes.forEach(box -> box.setSelected(true));

            var areOptionsCheckedBefore = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);

            //when
            checkboxes.getFirst().setSelected(false);
            checkboxes.get(3).setSelected(false);

            var areOptionsCheckedAfter = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);

            //then
            assertAll(
                    () -> Arrays.stream(areOptionsCheckedBefore).forEach(Assertions::assertTrue),
                    () -> IntStream.range(0, checkboxes.size()).forEach(i -> {
                        if (i != 0 && i != 3) assertTrue(areOptionsCheckedAfter[i]);
                        else assertFalse(areOptionsCheckedAfter[i]);
                    })
            );
        }

        @Test
        void shouldSelectAllCheckboxesWhenUserSelectLastCheckbox() {
            //given
            var areOptionsCheckedBefore = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);

            //when
            checkboxes.get(checkboxes.size() - 1).setSelected(true);

            var areOptionsCheckedAfter = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);

            //then
            assertAll(
                    () -> Arrays.stream(areOptionsCheckedBefore).forEach(Assertions::assertFalse),
                    () -> Arrays.stream(areOptionsCheckedAfter).forEach(Assertions::assertTrue)
            );
        }

        @Test
        void shouldDeselectAllCheckboxesWhenUserDeselectLastCheckbox() {
            //given
            checkboxes.forEach(box -> box.setSelected(true));

            //when
            var areOptionsCheckedBefore = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);
            checkboxes.get(checkboxes.size() - 1).setSelected(false);

            var areOptionsCheckedAfter = checkboxes.stream().map(AbstractButton::isSelected).toArray(Boolean[]::new);

            //then
            assertAll(
                    () -> Arrays.stream(areOptionsCheckedBefore).forEach(Assertions::assertTrue),
                    () -> Arrays.stream(areOptionsCheckedAfter).forEach(Assertions::assertFalse)
            );
        }
    }

    @Nested
    class GetSelectedWeekdaysTests {
        @Test
        void shouldReturnChosenWeekdays() {
            //given
            checkboxes.getFirst().setSelected(true);
            checkboxes.get(3).setSelected(true);

            //when
            var chosenWeekdays = cleanerService.getSelectedWeekdays();

            //then
            assertAll(
                    () -> assertThat(chosenWeekdays.size(), is(2)),
                    () -> assertTrue(chosenWeekdays.contains(WeekDays.MONDAY)),
                    () -> assertTrue(chosenWeekdays.contains(WeekDays.THURSDAY)),
                    () -> Arrays.stream(WeekDays.values())
                            .filter(weekday -> !weekday.equals(WeekDays.MONDAY) && !weekday.equals(WeekDays.THURSDAY))
                            .forEach(weekday -> assertFalse(chosenWeekdays.contains(weekday)))
            );
        }

        @Test
        void shouldReturnNullIfThereIsNoChosenWeekdays() {
            //given+when
            var chosenWeekdays = cleanerService.getSelectedWeekdays();

            //then
            assertThat(chosenWeekdays, is(nullValue()));
        }

        @Test
        void shouldReturnAllWeekdaysWhenLastCheckBoxIsSelected() {
            //given
            checkboxes.get(checkboxes.size() - 1).setSelected(true);

            //when
            var chosenWeekdays = cleanerService.getSelectedWeekdays();

            //then
            assertAll(
                    () -> assertThat(chosenWeekdays.size(), is(7)),
                    () -> Arrays.stream(WeekDays.values()).forEach(weekday -> assertTrue(chosenWeekdays.contains(weekday)))
            );
        }
    }
}