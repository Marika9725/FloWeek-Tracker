package com.floweektracker.service;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TasksServiceTest {
    private static final TestHelper helper = new TestHelper();
    private static final TasksService tasksService = TasksService.getService();
    private static Map<WeekDays, Map<LocalTime, SingleTask>> schedule;
    private static SingleTask baseTask;
    private static File dir;

    @BeforeAll
    static void setUpBeforeAll() {
        schedule = tasksService.getSchedule();
        baseTask = helper.createBaseTask();
    }

    @BeforeEach
    void setUpBeforeEach(TestInfo info) {
        if (info.getTags().contains("repoTests")) {
            dir = new File("data");
            dir.mkdir();
            RepositoryConfigurator.getConfigurator().setUpRepositories(dir);
        }
    }

    @AfterEach
    void cleanUpAfterEach() {
        schedule.values().forEach(Map::clear);
        if (dir != null && dir.exists()) helper.deleteDirectory(dir);
        PlannerRepository.getRepository().setSchedule(null);
    }

    @Nested
    class ConstructorTests {
        @Test
        void instancesShouldBeSame() {
            assertThat(TasksService.getService(), sameInstance(TasksService.getService()));
        }

        @Test
        void shouldInitializeScheduleWithEmptyMapsForEachWeekDay() {
            assertTrue(schedule.values().stream().allMatch(Map::isEmpty));
        }
    }

    @Nested
    class AddTaskTests {
        @Test
        void shouldAddTaskToSchedule() {
            assertAll(
                    () -> assertTrue(tasksService.addTask(baseTask)),
                    () -> assertThat(schedule.get(baseTask.getWeekday()).size(), is(1)),
                    () -> assertTrue(schedule.get(baseTask.getWeekday()).containsValue(baseTask))
            );
        }

        @Test
        void shouldReturnFalseIfAddedTaskIsNull() {
            assertFalse(() -> tasksService.addTask(null));
        }
    }

    @Nested
    class DeleteTaskTests {
        @Test
        void shouldDeleteTaskFromSchedule() {
            //given
            tasksService.addTask(baseTask);
            var isTaskBefore = schedule.get(baseTask.getWeekday()).containsValue(baseTask);

            //when
            var isTaskDeleted = tasksService.deleteTask(baseTask);
            var isTaskAfter = schedule.get(baseTask.getWeekday()).containsValue(baseTask);

            //then
            assertAll(
                    () -> assertTrue(isTaskBefore),
                    () -> assertTrue(isTaskDeleted),
                    () -> assertThat(schedule.get(baseTask.getWeekday()).size(), is(0)),
                    () -> assertFalse(isTaskAfter)
            );
        }

        @Test
        void shouldReturnFalseIfDeletedTaskIsNull() {
            assertFalse(tasksService.deleteTask(null));
        }

        @Test
        void shouldNotDeleteTaskIfEventTimeIsNotOccupied() {
            //given
            var isEventTimeOccupiedBefore = schedule.get(baseTask.getWeekday()).containsKey(baseTask.getTime());

            //when
            var isTaskDeleted = tasksService.deleteTask(baseTask);
            var isEventTimeOccupiedAfter = schedule.get(baseTask.getWeekday()).containsKey(baseTask.getTime());

            //then
            assertAll(
                    () -> assertFalse(isEventTimeOccupiedBefore),
                    () -> assertFalse(isTaskDeleted),
                    () -> assertFalse(isEventTimeOccupiedAfter)
            );
        }
    }

    @Nested
    class EditTaskTests {
        @ParameterizedTest
        @MethodSource("createCorrectTasksForEditTaskMethod")
        void shouldEditTask(SingleTask task, SingleTask editedTask) {
            //given
            schedule.get(task.getWeekday()).put(task.getTime(), task);

            //when+then
            assertAll(
                    () -> assertTrue(tasksService.editTask(task, editedTask)),
                    () -> assertFalse(schedule.get(task.getWeekday()).containsValue(task)),
                    () -> assertTrue(schedule.get(editedTask.getWeekday()).containsValue(editedTask))
            );
        }

        @ParameterizedTest
        @MethodSource("createNullableTasksForEditTaskMethod")
        void shouldReturnFalseIfOneOfArgumentsFromEditTaskMethodIsNull(SingleTask task, SingleTask editedTask) {
            //given
            if (task != null) schedule.get(task.getWeekday()).put(task.getTime(), task);

            //when+then
            assertFalse(tasksService.editTask(task, editedTask));
            if (task != null) {
                assertTrue(schedule.get(task.getWeekday()).containsValue(task));
                assertThat(schedule.get(task.getWeekday()).get(task.getTime()), is(task));
            }

            if (editedTask != null) assertFalse(schedule.get(editedTask.getWeekday()).containsValue(editedTask));
        }

        @Test
        void shouldReturnFalseIfParametersToEditTaskAreSame() {
            //given
            var editedTask = helper.createBaseTask();
            schedule.get(baseTask.getWeekday()).put(baseTask.getTime(), baseTask);

            //when+then
            assertAll(
                    () -> assertFalse(tasksService.editTask(baseTask, editedTask)),
                    () -> assertThat(schedule.get(baseTask.getWeekday()).get(baseTask.getTime()), sameInstance(baseTask)),
                    () -> assertThat(schedule.get(editedTask.getWeekday()).get(editedTask.getTime()), not(sameInstance(editedTask)))
            );
        }

        @Test
        void shouldReturnFalseIfTaskNotExistInScheduleBeforeEditing() {
            //given
            var editedTask = helper.createEditedTask();

            //when+then
            assertAll(
                    () -> assertFalse(tasksService.editTask(baseTask, editedTask)),
                    () -> assertFalse(schedule.get(baseTask.getWeekday()).containsValue(baseTask)),
                    () -> assertFalse(schedule.get(editedTask.getWeekday()).containsValue(editedTask))
            );
        }

        @Test
        void shouldReturnFalseIfEditedTaskExistsInScheduleBeforeEditing() {
            //given
            var editedTask = helper.createEditedTask();
            schedule.get(baseTask.getWeekday()).put(baseTask.getTime(), baseTask);
            schedule.get(editedTask.getWeekday()).put(editedTask.getTime(), editedTask);

            //when
            var isTaskEdited = tasksService.editTask(baseTask, editedTask);

            //then
            assertAll(
                    () -> assertFalse(isTaskEdited),
                    () -> assertTrue(schedule.get(baseTask.getWeekday()).containsValue(baseTask)),
                    () -> assertTrue(schedule.get(editedTask.getWeekday()).containsValue(editedTask))
            );
        }

        private static Stream<Arguments> createCorrectTasksForEditTaskMethod() {
            var tasks = new ArrayList<>(List.of(helper.createBaseTask(), helper.createBaseTask(), helper.createBaseTask()));
            tasks.getFirst().setTaskName("taskName1");
            tasks.getFirst().setDone(false);
            tasks.get(1).setTime(LocalTime.of(20, 30));
            tasks.getLast().setWeekday(WeekDays.FRIDAY);

            return Stream.of(
                    Arguments.of(helper.createBaseTask(), tasks.getFirst()),
                    Arguments.of(helper.createBaseTask(), tasks.get(1)),
                    Arguments.of(helper.createBaseTask(), tasks.getLast())
            );
        }

        private static Stream<Arguments> createNullableTasksForEditTaskMethod() {
            return Stream.of(
                    Arguments.of(null, helper.createBaseTask()),
                    Arguments.of(helper.createBaseTask(), null),
                    Arguments.of(null, null)
            );
        }
    }

    @Nested
    class CollectSortedTimesTests {
        @Test
        void shouldReturnAllTimesFromSchedule() {
            //given
            var expectedTimes = createLocalTimes();
            expectedTimes.forEach(time -> schedule.get(WeekDays.MONDAY).put(time, helper.createBaseTask()));

            //when
            var actualTimes = tasksService.collectSortedTimes();

            //then
            assertAll(
                    () -> assertThat(actualTimes, is(instanceOf(TreeSet.class))),
                    () -> actualTimes.forEach(time -> assertThat(time, is(instanceOf(LocalTime.class)))),
                    () -> assertThat(actualTimes.size(), is(4)),
                    () -> assertTrue(actualTimes.containsAll(createLocalTimes()))
            );
        }

        @Test
        void returnedTimesShouldBeSorted() {
            //given
            var unsortedTimes = createLocalTimes();
            unsortedTimes.forEach(time -> schedule.get(WeekDays.MONDAY).put(time, helper.createBaseTask()));

            LocalTime[] expectedTimes = {
                    LocalTime.of(9, 15),
                    LocalTime.of(12, 30),
                    LocalTime.of(17, 45),
                    LocalTime.of(18, 50)
            };

            //when
            var actualTimes = tasksService.collectSortedTimes().toArray(LocalTime[]::new);

            //then
            assertThat(actualTimes, is(expectedTimes));
        }

        @Test
        void returnedTimesShouldBeUnique() {
            //given
            schedule.get(WeekDays.MONDAY).put(LocalTime.of(12, 30), helper.createBaseTask());
            schedule.get(WeekDays.TUESDAY).put(LocalTime.of(12, 30), helper.createBaseTask());

            //when
            var times = tasksService.collectSortedTimes();

            //then
            assertAll(
                    () -> assertThat(times.size(), is(1)),
                    () -> assertThat(times.getFirst(), is(LocalTime.of(12, 30)))
            );
        }

        private List<LocalTime> createLocalTimes() {
            return Stream.of(
                    LocalTime.of(12, 30),
                    LocalTime.of(9, 15),
                    LocalTime.of(17, 45),
                    LocalTime.of(18, 50)
            ).toList();
        }
    }

    @Nested
    class CountPointsTests {
        @ParameterizedTest
        @MethodSource("com.floweektracker.service.TasksServiceTest#createTasksForCountPointsFromWeekday")
        void shouldCorrectlyCountAchievedPointsFromSelectedWeekday(SingleTask task1, SingleTask task2) {
            //given
            task2.setTime(LocalTime.of(13, 0));
            schedule.get(task1.getWeekday()).put(task1.getTime(), task1);
            schedule.get(task2.getWeekday()).put(task2.getTime(), task2);

            var expectedPoints = (task1.isDone() ? task1.getPriority() : 0) + (task2.isDone() ? task2.getPriority() : 0);

            //when
            var actualAchievedPoints = tasksService.countPoints(task1.getWeekday(), SingleTask::calculatePoints);

            //then
            assertThat(actualAchievedPoints, is(expectedPoints));
        }

        @Test
        void shouldReturnZeroAchievedPointsWhenThereIsNoTasksInSchedule() {
            //given+when
            var achievedPoints = tasksService.countPoints(WeekDays.MONDAY, SingleTask::calculatePoints);

            //then
            assertThat(achievedPoints, is(0));
        }

        @ParameterizedTest
        @MethodSource("com.floweektracker.service.TasksServiceTest#createTasksForCountPointsFromWeekday")
        void shouldCorrectlyCountTotalPointsFromSelectedWeekday(SingleTask task1, SingleTask task2) {
            //given
            task2.setTime(LocalTime.of(13, 0));
            schedule.get(task1.getWeekday()).put(task1.getTime(), task1);
            schedule.get(task2.getWeekday()).put(task2.getTime(), task2);

            var expectedTotalPoints = task1.getPriority() + task2.getPriority();

            //when
            var actualTotalPoints = tasksService.countPoints(task1.getWeekday(), SingleTask::getPriority);

            //then
            assertThat(actualTotalPoints, is(expectedTotalPoints));
        }

        @Test
        void shouldReturnZeroTotalPointsWhenThereIsNoTasksInSchedule() {
            assertThat(tasksService.countPoints(WeekDays.MONDAY, SingleTask::getPriority), is(0));
        }

        @Test
        void shouldReturnZeroWhenSingleTaskMethodIsNull() {
            //given + when
            var actualPoints = tasksService.countPoints(WeekDays.MONDAY, null);

            //then
            assertThat(actualPoints, is(0));
        }
    }

    @Nested
    class GetTasksByEventTimeTests {
        @Test
        void shouldReturnTaskByEventTime() {
            //given
            schedule.get(baseTask.getWeekday()).put(baseTask.getTime(), baseTask);

            //when
            var task = tasksService.getTaskByEventTime(baseTask.getWeekday(), baseTask.getTime());

            //then
            assertAll(
                    () -> assertThat(task, notNullValue()),
                    () -> assertThat(task, is(baseTask))
            );
        }

        @Test
        void shouldReturnNullIfEventTimeNotExist() {
            //given+when
            var task = tasksService.getTaskByEventTime(baseTask.getWeekday(), baseTask.getTime());

            //then
            assertAll(
                    () -> assertThat(task, nullValue()),
                    () -> assertThat(task, is(not(baseTask)))
            );
        }
    }

    @Nested
    class GetTasksFromWeekdayTests {
        @Test
        void shouldReturnWeekDayTasks() {
            //given
            schedule.get(baseTask.getWeekday()).put(baseTask.getTime(), baseTask);
            var expectedValue = List.of(baseTask);

            //when
            var actualValue = tasksService.getTasksFromWeekday(baseTask.getWeekday());

            //then
            assertThat(actualValue, is(notNullValue()));
            assertAll(
                    () -> assertThat(actualValue.size(), is(1)),
                    () -> assertThat(actualValue, equalTo(expectedValue))
            );
        }

        @Test
        void shouldReturnEmptyListWhenWeekDayHasNoTasks() {
            //given
            var expectedValue = new ArrayList<SingleTask>();

            //when
            var actualValue = tasksService.getTasksFromWeekday(baseTask.getWeekday());

            //then
            assertThat(actualValue, is(notNullValue()));
            assertAll(
                    () -> assertThat(actualValue.size(), is(0)),
                    () -> assertThat(actualValue, equalTo(expectedValue))
            );
        }
    }

    @Nested
    class FinderTaskTests {
        @Test
        void shouldReturnFalseIfTaskIsNull() {
            assertFalse(tasksService.isTaskInSchedule(null));
        }
    }

    @Tag("repoTests")
    @Nested
    class SavePlannerTests {
        private final PlannerRepository plannerRepository = PlannerRepository.getRepository();

        @Test
        void savePlannerShouldSyncScheduleWithRepository() {
            //given
            schedule.get(baseTask.getWeekday()).put(baseTask.getTime(), baseTask);
            var repoScheduleBefore = PlannerRepository.getRepository().getSchedule();

            //when
            tasksService.savePlanner();
            var repoScheduleAfter = PlannerRepository.getRepository().getSchedule();

            //then
            assertAll(
                    () -> assertThat(repoScheduleBefore, is(nullValue())),
                    () -> assertThat(repoScheduleAfter, is(schedule))
            );
        }

        @Test
        void savePlannerShouldCreateFileIfNotExists() {
            //given
            schedule.get(baseTask.getWeekday()).put(baseTask.getTime(), baseTask);
            var plannerFile = new File(dir, "Planner.json");
            var isPlannerFileExistsBefore = plannerFile.exists();

            //when
            tasksService.savePlanner();

            var isPlannerFileExistsAfter = plannerFile.exists();
            plannerRepository.loadPlanner();
            var actualSchedule = plannerRepository.getSchedule();

            //then
            assertAll(
                    () -> assertFalse(isPlannerFileExistsBefore),
                    () -> assertTrue(isPlannerFileExistsAfter),
                    () -> assertThat(actualSchedule, is(schedule))
            );
        }

        @Test
        void savePlannerShouldUpdateFileIfExists() throws IOException {
            //given
            schedule.get(baseTask.getWeekday()).put(baseTask.getTime(), baseTask);
            var plannerFile = new File(dir, "Planner.json");
            plannerFile.createNewFile();
            var isPlannerFileExistsBefore = plannerFile.exists();
            var fileSizeBefore = plannerFile.exists() ? plannerFile.length() : 0L;

            //when
            tasksService.savePlanner();

            var isPlannerFileExistsAfter = plannerFile.exists();
            plannerRepository.loadPlanner();
            var actualSchedule = plannerRepository.getSchedule();
            var fileSizeAfter = plannerFile.exists() ? plannerFile.length() : 0;

            //then
            assertAll(
                    () -> assertTrue(isPlannerFileExistsBefore),
                    () -> assertTrue(isPlannerFileExistsAfter),
                    () -> assertTrue(fileSizeBefore < fileSizeAfter),
                    () -> assertThat(actualSchedule, is(schedule))
            );
        }
    }

    public static Stream<Arguments> createTasksForCountPointsFromWeekday() {
        var taskWithFalseDone = helper.createBaseTask();
        var taskWithFalseDone2 = helper.createBaseTask();
        taskWithFalseDone.setDone(false);
        taskWithFalseDone2.setDone(false);


        return Stream.of(
                Arguments.of(helper.createBaseTask(), helper.createBaseTask()),
                Arguments.of(helper.createBaseTask(), taskWithFalseDone2),
                Arguments.of(taskWithFalseDone, taskWithFalseDone2)
        );
    }
}