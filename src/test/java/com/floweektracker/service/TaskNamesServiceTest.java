package com.floweektracker.service;

import com.floweektracker.TestHelper;
import com.floweektracker.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskNamesServiceTest {
    private static final TestHelper helper = new TestHelper();
    private static final TaskNamesService taskNamesService = TaskNamesService.getService();
    private static final String TASK_NAME_TEST = "Task1";
    private Set<String> taskNames;
    private static File dir;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        taskNames = taskNamesService.getTaskNames();

        if (testInfo.getTags().contains("withTaskName")) taskNamesService.addTaskName(TASK_NAME_TEST);
        if (testInfo.getTags().contains("repoTests")) {
            dir = new File("data");
            dir.mkdir();
            RepositoryConfigurator.getConfigurator().setUpRepositories(dir);
        }
    }

    @AfterEach
    void cleanUp() {
        taskNames.clear();

        if (dir != null && dir.exists()) helper.deleteDirectory(dir);
        TaskNamesRepository.getRepository().setTaskNames(null);
    }

    @Test
    void constructorShouldCreateSameInstances() {
        assertSame(taskNamesService, TaskNamesService.getService());
    }

    @Nested
    class AddTaskNameTests {
        @Test
        void shouldAddTaskNameToBase() {
            //given
            var sizeBaseBefore = taskNames.size();

            //when
            var isTaskAdded = taskNamesService.addTaskName(TASK_NAME_TEST);
            var sizeBaseAfter = taskNames.size();

            //then
            assertAll(
                    () -> assertThat(sizeBaseBefore, is(0)),
                    () -> assertTrue(isTaskAdded),
                    () -> assertThat(sizeBaseAfter, is(1)),
                    () -> assertTrue(taskNames.contains(TASK_NAME_TEST))
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"NULL", " ", ""})
        void shouldReturnFalseIfTaskNameIsInvalid(String taskName) {
            //given
            if (taskName.equals("NULL")) {taskName = null;}

            var sizeBaseBefore = taskNames.size();

            //when
            var isTaskAdded = taskNamesService.addTaskName(taskName);
            var sizeBaseAfter = taskNames.size();

            //when+then
            assertAll(
                    () -> assertThat(sizeBaseBefore, is(0)),
                    () -> assertFalse(isTaskAdded),
                    () -> assertThat(sizeBaseAfter, is(0))
            );
        }

        @Tag("withTaskName")
        @Test
        void addedTaskNameShouldNotBeExistedBeforeInBase() {
            //given
            var names = (Arrays.asList("Task1", new String("Task1")));

            var sizeBaseBeforeAddTaskNames = taskNames.size();

            //when
            var areTaskNamesAdded = names.stream().map(taskNamesService::addTaskName).toList();

            var sizeBaseAfterAddTaskNames = taskNames.size();

            //when+then
            assertAll(
                    () -> assertThat(sizeBaseBeforeAddTaskNames, is(1)),
                    () -> areTaskNamesAdded.forEach(Assertions::assertFalse),
                    () -> assertThat(sizeBaseAfterAddTaskNames, is(1))
            );
        }
    }

    @Nested
    class DeleteTaskNameTests {
        @Tag("withTaskName")
        @Test
        void taskNameShouldBeDeletedFromBase() {
            //given
            var sizeBaseBefore = taskNames.size();

            //when
            var isTaskDeleted = taskNamesService.deleteTaskName(TASK_NAME_TEST);
            var sizeBaseAfter = taskNames.size();

            //when+then
            assertAll(
                    () -> assertThat(sizeBaseBefore, is(1)),
                    () -> assertTrue(isTaskDeleted),
                    () -> assertThat(sizeBaseAfter, is(0)),
                    () -> assertFalse(taskNames.contains(TASK_NAME_TEST))
            );
        }

        @Tag("withTaskName")
        @ParameterizedTest
        @ValueSource(strings = {"NULL", " ", ""})
        void taskNameDeletedFromBaseShouldNotBeInvalid(String taskName) {
            //given
            if (taskName.equals("NULL")) {taskName = null;}

            var sizeBaseBefore = taskNames.size();

            //when
            var isTaskDeleted = taskNamesService.deleteTaskName(taskName);
            var sizeBaseAfter = taskNames.size();

            //then
            assertAll(
                    () -> assertThat(sizeBaseBefore, is(1)),
                    () -> assertFalse(isTaskDeleted),
                    () -> assertThat(sizeBaseAfter, is(1)),
                    () -> assertTrue(taskNames.contains(TASK_NAME_TEST))
            );
        }

        @Tag("withTaskName")
        @Test
        void shouldReturnFalseIfTaskNotExistBeforeCallDeleteMethod() {
            //given
            var sizeBaseBefore = taskNames.size();

            //when
            var isTaskDeleted = taskNamesService.deleteTaskName("Task2");
            var sizeBaseAfter = taskNames.size();

            //then
            assertAll(
                    () -> assertThat(sizeBaseBefore, is(1)),
                    () -> assertFalse(isTaskDeleted),
                    () -> assertThat(sizeBaseAfter, is(1)),
                    () -> assertTrue(taskNames.contains(TASK_NAME_TEST))
            );
        }
    }

    @Tag("repoTests")
    @Nested
    class SavePlannerTests {
        private final TaskNamesRepository taskNamesRepository = TaskNamesRepository.getRepository();

        @Test
        void saveTaskNamesShouldSyncTaskNamesWithRepository() {
            //given
            taskNames.add("taskName1");
            var repoTaskNamesBefore = TaskNamesRepository.getRepository().getTaskNames();

            //when
            taskNamesService.saveTaskNames();
            var repoTaskNamesAfter = TaskNamesRepository.getRepository().getTaskNames();

            //then
            assertAll(
                    () -> assertThat(repoTaskNamesBefore, is(nullValue())),
                    () -> assertThat(repoTaskNamesAfter, is(taskNames))
            );
        }

        @Test
        void saveTaskNamesShouldCreateFileIfNotExists() {
            //given
            taskNames.add("taskName1");
            var taskNamesFile = new File(dir, "TaskNames.json");
            var isTaskNamesFileExistsBefore = taskNamesFile.exists();

            //when
            taskNamesService.saveTaskNames();

            var isTaskNamesFileExistsAfter = taskNamesFile.exists();
            taskNamesRepository.loadTaskNames();
            var actualTaskNames = taskNamesRepository.getTaskNames();

            //then
            assertAll(
                    () -> assertFalse(isTaskNamesFileExistsBefore),
                    () -> assertTrue(isTaskNamesFileExistsAfter),
                    () -> assertThat(actualTaskNames, is(taskNames))
            );
        }

        @Test
        void savePlannerShouldUpdateFileIfExists() throws IOException {
            //given
            taskNames.add("taskName1");
            var taskNamesFile = new File(dir, "TaskNames.json");
            taskNamesFile.createNewFile();
            var isTaskNamesFileExistsBefore = taskNamesFile.exists();
            var fileSizeBefore = taskNamesFile.exists() ? taskNamesFile.length() : 0L;

            //when
            taskNamesService.saveTaskNames();

            var isTaskNamesFileExistsAfter = taskNamesFile.exists();
            taskNamesRepository.loadTaskNames();
            var actualTaskNames = taskNamesRepository.getTaskNames();
            var fileSizeAfter = taskNamesFile.exists() ? taskNamesFile.length() : 0L;

            //then
            assertAll(
                    () -> assertTrue(isTaskNamesFileExistsBefore),
                    () -> assertTrue(isTaskNamesFileExistsAfter),
                    () -> assertTrue(fileSizeBefore < fileSizeAfter),
                    () -> assertThat(actualTaskNames, is(taskNames))
            );
        }
    }

    @Test
    void taskNamesShouldBeArrangedAlphabetically() {
        //given
        var customTaskNames = new ArrayList<>(Arrays.asList("Task", "Important Task", "Very Important Task", "Extra Task"));

        //when
        customTaskNames.forEach(taskNamesService::addTaskName);

        var customSortedTaskNames = taskNames.toArray(new String[0]);

        //then
        assertAll(
                () -> assertThat(taskNames.size(), is(4)),
                () -> assertThat(customSortedTaskNames[0], is("Extra Task")),
                () -> assertThat(customSortedTaskNames[1], is("Important Task")),
                () -> assertThat(customSortedTaskNames[2], is("Task")),
                () -> assertThat(customSortedTaskNames[3], is("Very Important Task"))
        );
    }
}