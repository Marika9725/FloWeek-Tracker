package com.floweektracker;

import com.floweektracker.model.WeekDays;
import com.floweektracker.view.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class MainFrameTest {
    private final TestHelper helper = new TestHelper();
    private final MainFrame mainFrame = MainFrame.getMAIN_FRAME();
    private Container cardPanel = mainFrame.getContentPane();

    @AfterEach
    void cleanUp() {
        mainFrame.setVisible(false);
        this.cardPanel = mainFrame.getContentPane();
    }

    @Nested
    class MainFrameFrameTest {
        @Test
        void mainFrameTitleShouldNotBeBlank() {
            //given+when
            var title = mainFrame.getTitle();

            //then
            assertThat(title, is("FloWeek Tracker"));
        }

        @Test
        void mainFrameShouldBeResizable() {
            //given+when
            var isResizable = mainFrame.isResizable();

            //then
            assertTrue(isResizable);
        }

        @Test
        void mainFrameShouldContainsCardPanel() {
            assertAll(
                    () -> assertThat(cardPanel, notNullValue()),
                    () -> assertThat(cardPanel, is(instanceOf(JPanel.class))),
                    () -> assertThat(cardPanel.getLayout(), is(instanceOf(CardLayout.class))),
                    () -> assertThat(cardPanel.getName(), is("cardPanel")),
                    () -> assertTrue(cardPanel.isDoubleBuffered())
            );
        }
    }

    @Nested
    class CardPanelTest {
        @Test
        void cardPanelShouldContainsMainPanel() {
            //given+when
            var expectedMainPanel = MainPanelView.getView();
            var actualMainPanel = helper.findComponent("MainPanel", cardPanel);

            //then
            assertAll(
                    () -> assertThat(actualMainPanel, is(notNullValue())),
                    () -> assertThat(actualMainPanel, is(expectedMainPanel))
            );
        }

        @ParameterizedTest
        @CsvSource({"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
        void cardPanelShouldContainsPanelsForEachWeekday(String panelName) {
            //given
            var expectedWeekdayPanel = new WeekdayPlannerView(WeekDays.valueOf(panelName));

            //when
            var actualWeekdayPanel = helper.findComponent(panelName, cardPanel);

            //then
            assertThat(actualWeekdayPanel, is(notNullValue()));
            assertAll(
                    () -> assertThat(expectedWeekdayPanel.getClass(), is(actualWeekdayPanel.getClass())),
                    () -> assertThat(expectedWeekdayPanel.getName(), is(actualWeekdayPanel.getName()))
            );
        }

        @Test
        void cardPanelShouldContainsBaseInfo() {
            //given
            var expectedBaseInfoPanel = new InfoView("Baza");

            //when
            var actualBaseInfoPanel = helper.findComponent("Baza", cardPanel);

            //then
            assertThat(actualBaseInfoPanel, is(notNullValue()));
            assertAll(
                    () -> assertThat(actualBaseInfoPanel.getClass(), is(expectedBaseInfoPanel.getClass())),
                    () -> assertThat(actualBaseInfoPanel.getName(), is(expectedBaseInfoPanel.getName()))
            );
        }

        @Test
        void cardPanelShouldContainsPlannerInfo() {
            //given
            var expectedPlannerInfoPanel = new InfoView("Planer");

            //when
            var actualPlannerInfoPanel = helper.findComponent("Planer", cardPanel);

            //then
            assertThat(actualPlannerInfoPanel, is(notNullValue()));
            assertAll(
                    () -> assertThat(actualPlannerInfoPanel.getClass(), is(expectedPlannerInfoPanel.getClass())),
                    () -> assertThat(actualPlannerInfoPanel.getName(), is(expectedPlannerInfoPanel.getName()))
            );
        }
    }

    @Nested
    class SwitchCardTests {
        @ParameterizedTest
        @ValueSource(strings = {"mainPanel", "mondayPanel", "tuesdayPanel", "wednesdayPanel", "thursdayPanel", "fridayPanel", "saturdayPanel", "sundayPanel"})
        void shouldSwitchCard(String panelName) {
            //given+when
            var isCardSwitched = mainFrame.switchCard(panelName);

            //then
            assertTrue(isCardSwitched);
        }

        @ParameterizedTest
        @MethodSource("createValidPanelNames")
        void shouldNotSwitchCardIfPanelNameIsValid(Object panelName) {
            //given+when
            var isCardSwitched = panelName == null ? mainFrame.switchCard(null) : mainFrame.switchCard(panelName.toString());

            //then
            assertFalse(isCardSwitched);
        }

        private static Stream<Arguments> createValidPanelNames() {
            return Stream.of(
                    Arguments.of("validPanelName"),
                    Arguments.of(""),
                    Arguments.of(" "),
                    Arguments.of((String) null),
                    Arguments.of(123),
                    Arguments.of(new Object())
            );
        }
    }
}