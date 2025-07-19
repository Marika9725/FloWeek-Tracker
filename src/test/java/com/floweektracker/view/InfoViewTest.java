package com.floweektracker.view;

import com.floweektracker.TestHelper;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.hamcrest.Matchers.is;

class InfoViewTest {
    private final InfoView infoView = new InfoView("Baza");
    private final TestHelper helper = new TestHelper();
    private JPanel panel;

    @BeforeEach
    void setUp(TestInfo info) {
        var tag = info.getTags().stream().findFirst().orElse(null);

        switch (tag) {
            case "titlePanel", "contentPanel", "buttonPanel" ->
                    this.panel = (JPanel) helper.findComponent(tag, infoView);
            case null, default -> this.panel = null;
        }
    }

    @AfterEach
    void tearDown() {
        this.panel = null;
    }

    @Test
    void constructorShouldConfigureMainPanel() {
        assertThat(infoView, is(notNullValue()));
        assertAll(
                () -> assertThat(infoView, instanceOf(JPanel.class)),
                () -> assertThat(infoView.getName(), is("Baza")),
                () -> assertThat(infoView.getLayout(), instanceOf(BorderLayout.class))
        );
    }

    @Test
    void instancesShouldNotBeSame() {
        //given+when
        var infoView2 = new InfoView("Baza");

        //then
        assertThat(infoView, not(sameInstance(infoView2)));
    }

    @Tag("titlePanel")
    @Nested
    class TitlePanelTests {
        @Test
        void shouldContainsTitlePanel() {
            assertThat(panel, is(notNullValue()));
            assertThat(panel, is(instanceOf(JPanel.class)));
            assertThat(panel.getLayout(), instanceOf(FlowLayout.class));
        }

        @Test
        void titlePanelShouldContainsTitleLabel() {
            //given+when
            var titleLabel = (JLabel) helper.findComponent("titleLabel", Objects.requireNonNull(panel));

            //then
            assertThat(titleLabel, is(notNullValue()));
        }
    }

    @Tag("contentPanel")
    @Nested
    class ContentPanelTests {
        @Test
        void shouldContainsContentPanel() {
            assertThat(panel, is(notNullValue()));
            assertThat(panel, is(instanceOf(JPanel.class)));
            assertThat(panel.getLayout(), instanceOf(BoxLayout.class));
        }

        @Test
        void contentPanelShouldContainsMoreThanOneLabel() {
            //given+when
            var labels = panel.getComponents();

            //then
            assertThat(labels.length, is(greaterThan(1)));
            Arrays.stream(labels).forEach(label -> assertThat(label, is(instanceOf(JLabel.class))));
        }
    }

    @Nested
    class ButtonPanelTests {
        @Tag("buttonPanel") @Test
        void shouldContainsButtonPanel() {
            assertThat(panel, is(notNullValue()));
            assertThat(panel, is(instanceOf(JPanel.class)));
            assertThat(panel.getLayout(), instanceOf(FlowLayout.class));
        }

        @Tag("buttonPanel") @Test
        void shouldContainsReturnButton() {
            //given
            var returnButton = (JButton) helper.findComponent("returnButton", Objects.requireNonNull(panel));

            //then
            assertThat(returnButton, is(notNullValue()));
        }
    }
}