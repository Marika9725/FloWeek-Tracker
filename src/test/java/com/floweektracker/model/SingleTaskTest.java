package com.floweektracker.model;

import com.floweektracker.TestHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class SingleTaskTest {
    private final TestHelper helper = new TestHelper();
    private SingleTask task;

    @BeforeEach
    void setUp() {
        this.task = helper.createBaseTask();
    }

    @AfterEach
    void tearDown() {
        this.task = null;
    }

    @Test
    void shouldNotBeAbleToCreateSingleTaskWithNullTime() {
        //when+then
        assertThrows(IllegalArgumentException.class, () -> new SingleTask("taskName", "description", null, true, WeekDays.MONDAY, (byte) 5));
    }

    @Test
    void shouldNotBeAbleToCreateSingleTaskWithNullWeekday() {
        //when+then
        assertThrows(IllegalArgumentException.class, () -> new SingleTask("taskName", "description", LocalTime.of(12, 0), true, null, (byte) 5));
    }

    @Nested
    class PriorityTests {
        @ParameterizedTest
        @ValueSource(bytes = {-5, 12})
        void shouldNotBeAbleToCreateSingleTaskWithPriorityOutOfRange(byte priority) {
            //when+then
            assertThrows(IllegalArgumentException.class, () -> new SingleTask("taskName", "description", LocalTime.of(12, 0), true, WeekDays.MONDAY, priority));
        }

        @ParameterizedTest
        @ValueSource(bytes = {-5, 12})
        void shouldNotBeAbleToChangePriorityWhenIsOutOfRange(byte priority) {
            //when+then
            assertThrows(IllegalArgumentException.class, () -> task.setPriority(priority));
        }

        @Test
        void shouldBeAbleToChangePriorityWhenIsInRange() {
            //given+when
            task.setPriority((byte) 6);

            //then
            assertThat(task.getPriority(), is((byte) 6));
        }
    }

    @Test
    void shouldReturnPointsBasedOnIsDoneValue() {
        //given
        var doneTask = helper.createBaseTask();
        doneTask.setDone(true);

        //when+then
        assertAll(
                () -> assertThat(doneTask.calculatePoints(), is(doneTask.getPriority())),
                () -> assertThat(task.calculatePoints(), is((byte) 0))
        );
    }

    @Nested
    class CopyTests {
        @Test
        void shouldBeAbleToCopySingleTask() {
            //given+when
            var copy = task.copy();

            //then
            assertThat(task, is(copy));
        }

        @Test
        void singleTaskCopyShouldHaveNewReference() {
            //given+when
            var copy = task.copy();

            //then
            assertThat(task, not(sameInstance(copy)));
        }
    }
}