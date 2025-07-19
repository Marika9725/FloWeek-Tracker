package com.floweektracker.view;

import com.floweektracker.TestHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MainFramePanelViewTest {
    private final TestHelper helper = new TestHelper();
    private final MainPanelView mainPanel = MainPanelView.getView();

    @Test
    void constructorShouldConfigureMainPanel() {
        assertAll(
                () -> assertThat(mainPanel, is(instanceOf(JPanel.class))),
                () -> assertThat(mainPanel.getName(), is("MainPanel")),
                () -> assertThat(mainPanel.getLayout(), instanceOf(BoxLayout.class))
        );
    }

    @Nested
    class TitlePanelTests {
        @Test
        void mainPanelShouldContainsTitlePanel() {
            //given+when
            var titlePanel = (JPanel) helper.findComponent("TitlePanel", mainPanel);

            //then
            assertAll(
                    () -> assertThat(titlePanel, is(notNullValue())),
                    () -> assertThat(titlePanel, is(instanceOf(JPanel.class))),
                    () -> assertThat(titlePanel != null ? titlePanel.getLayout() : null, is(instanceOf(BoxLayout.class)))
            );
        }

        @Test
        void titlePanelShouldContainsInfoButton() {
            //given+when
            var titlePanel = (JPanel) helper.findComponent("TitlePanel", mainPanel);
            var infoButton = (titlePanel != null) ? (JButton) helper.findComponent("infoButton", titlePanel) : null;

            //then
            assertAll(
                    () -> assertThat(infoButton, is(notNullValue())),
                    () -> assertThat(infoButton, is(instanceOf(JButton.class)))
            );
        }

        @Test
        void titlePanelShouldContainsTitleLabel() {
            //given+when
            var titlePanel = (JPanel) helper.findComponent("TitlePanel", mainPanel);
            var titleLabel = (titlePanel != null) ? (JLabel) helper.findComponent("titleLabel", titlePanel) : null;

            //then
            assertThat(titleLabel, is(notNullValue()));
            assertAll(
                    () -> assertThat(titleLabel, is(instanceOf(JLabel.class))),
                    () -> assertThat(titleLabel.getText(), is("TYGODNIOWY PLANER")),
                    () -> assertThat(titleLabel.getAlignmentX(), is(Component.CENTER_ALIGNMENT)),
                    () -> assertThat(titleLabel.getFont(), is(new Font("Arial", Font.BOLD, 40)))
            );
        }
    }

    @Nested
    class ButtonsPanelTests {
        @Test
        void mainPanelShouldContainsButtonsPanel() {
            //given+when
            var buttonsPanel = (JPanel) helper.findComponent("ButtonsPanel", mainPanel);

            //then
            assertAll(
                    () -> assertThat(buttonsPanel, is(notNullValue())),
                    () -> assertThat(buttonsPanel, is(instanceOf(JPanel.class))),
                    () -> assertThat(Objects.requireNonNull(buttonsPanel).getLayout(), instanceOf(FlowLayout.class))
            );
        }

        @ParameterizedTest
        @CsvSource({"addButton, Dodaj zadanie", "addTaskNameButton, Dodaj zadanie do bazy", "deleteButton, Usuń zadanie",
                "cleanScheduleButton, Wyczyść planer", "resetPointsButton, Wyzeruj punkty"})
        void buttonsPanelShouldContainsButton(String buttonName, String buttonText) {
            //given
            var buttonsPanel = (JPanel) helper.findComponent("ButtonsPanel", mainPanel);

            //when
            var button = (JButton) helper.findComponent(buttonName, Objects.requireNonNull(buttonsPanel));

            //then
            assertThat(button, is(notNullValue()));
            assertThat(button.getText(), is(buttonText));
        }
    }

    @Test
    void mainPanelShouldContainsPlanner() {
        //given+when
        var planner = (JTable) helper.findComponent("planner", mainPanel);

        //then
        assertAll(
                () -> assertThat(planner, is(notNullValue())),
                () -> assertThat(planner, is(instanceOf(JTable.class)))
        );
    }

    @Test
    void mainPanelShouldContainsBottomInfo() {
        //given+when
        var bottomLabel = (JLabel) helper.findComponent("bottomInfo", mainPanel);

        //then
        assertThat(bottomLabel, is(notNullValue()));

        assertAll(
                () -> assertThat(bottomLabel.getText(), is("Aby uzyskać więcej informacji, kliknij w ikonę \"i\" znajdującą się w lewym górnym rogu.")),
                () -> assertThat(bottomLabel.getAlignmentX(), is(Component.CENTER_ALIGNMENT))
        );
    }
}