package com.floweektracker.repository;

import com.floweektracker.service.*;
import com.floweektracker.util.DialogUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class TaskNamesRepositoryTest {
    private final TaskNamesRepository taskNamesRepository = TaskNamesRepository.getRepository();
    private final Set<String> taskNames = createTaskNames();
    private File tempFile;
    private final static TaskNamesService taskNamesService = TaskNamesService.getService();

    @BeforeEach
    void setUp(TestInfo testInfo) throws IOException {
        var tags = testInfo.getTags();
        this.tempFile = Files.createTempFile("TaskNames", ".json").toFile();

        if (tags.contains("setTaskNames")) taskNamesRepository.setTaskNames(taskNames);
        if (tags.contains("setFile")) taskNamesRepository.setFile(tempFile);
        if (tags.contains("setFakeFile")) {
            this.tempFile = new File(System.getProperty("user.dir"), "test-folder");
            tempFile.mkdir();
            taskNamesRepository.setFile(tempFile);
        }
    }

    @AfterEach
    void tearDown() {
        taskNamesRepository.setTaskNames(null);
        taskNamesRepository.setFile(null);

        try {Files.deleteIfExists(tempFile.toPath());} catch (IOException _) {}
    }

    @AfterAll
    static void afterAll() {
        var taskNames = new ArrayList<>(taskNamesService.getTaskNames());
        taskNames.forEach(taskNamesService::deleteTaskName);
    }

    @Nested
    class SaveTests {
        @Tag("setTaskNames")
        @Tag("setFile")
        @Test
        void shouldSaveScheduleToFile() {
            //given+when
            var areTaskNamesSaved = taskNamesRepository.saveTaskNames();
            var actualTaskNames = readFile(tempFile);

            //then
            assertAll(
                    () -> assertTrue(areTaskNamesSaved),
                    () -> assertThat(actualTaskNames, is(taskNames))
            );
        }

        @Tag("setTaskNames")
        @Test
        void shouldReturnFalseWhenFileIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                var areTaskNamesSaved = taskNamesRepository.saveTaskNames();

                //then
                assertFalse(areTaskNamesSaved);
                assertThat(tempFile.length(), is(0L));
            }
        }

        @Tag("setTaskNames")
        @Test
        void shouldShowMessageDialogWhenFileIsNull() {
            try (var dialogUtils = mockStatic(DialogUtils.class)) {
                //given+when
                taskNamesRepository.saveTaskNames();

                //then
                dialogUtils.verify(() -> DialogUtils.showMessageDialog(anyString(), anyString()));
            }
        }

        @Tag("setFile")
        @Test
        void shouldReturnFalseWhenTaskNamesAreNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                var areTaskNamesSaved = taskNamesRepository.saveTaskNames();

                //then
                assertFalse(areTaskNamesSaved);
            }
        }

        @Tag("setFile")
        @Test
        void shouldShowMessageDialogWhenTaskNamesAreNull() {
            try (var dialogUtils = mockStatic(DialogUtils.class)) {
                //given+when
                taskNamesRepository.saveTaskNames();

                //then
                dialogUtils.verify(() -> DialogUtils.showMessageDialog(anyString(), anyString()));
            }
        }

        @Tag("setFile")
        @Test
        void savedFileShouldBeEmptyWhenTaskNamesAreNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                taskNamesRepository.saveTaskNames();

                //then
                assertThat(tempFile.length(), is(0L));
            }
        }

        private Set<String> readFile(File tempFile) {
            try (var reader = new FileReader(tempFile)) {
                var type = new TypeToken<Set<String>>() {}.getType();
                var gson = new GsonBuilder().setPrettyPrinting().create();

                return gson.fromJson(reader, type);
            } catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    @Nested
    class LoadTests {
        @Tag("setTaskNames")
        @Tag("setFile")
        @Test
        void shouldLoadTaskNamesFromFile() {
            //given
            taskNamesRepository.saveTaskNames();
            taskNamesRepository.setTaskNames(null);

            //when
            var areTaskNamesLoaded = taskNamesRepository.loadTaskNames();
            var actualTaskNames = taskNamesRepository.getTaskNames();

            //then
            assertTrue(areTaskNamesLoaded);
            assertThat(actualTaskNames, is(taskNames));
        }

        @Test
        void shouldReturnFalseWhenFileIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                var areTaskNamesLoaded = taskNamesRepository.loadTaskNames();

                //then
                assertFalse(areTaskNamesLoaded);
            }
        }

        @Test
        void loadedScheduleShouldBeNullWhenFileIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                taskNamesRepository.loadTaskNames();
                var actualSchedule = taskNamesRepository.getTaskNames();

                //then
                assertThat(actualSchedule, is(nullValue()));
            }
        }

        @Tag("setFakeFile") @Test
        void savedPlannerShouldBeNullWhenFileIsADirectory() {
            //given
            try (var _ = mockStatic(DialogUtils.class)) {
                //when
                taskNamesRepository.loadTaskNames();
                var actualTaskNames = taskNamesRepository.getTaskNames();

                //then
                assertThat(actualTaskNames, is(nullValue()));
            }
        }
    }

    //region helper methods
    private Set<String> createTaskNames() {
        taskNamesService.addTaskName("Task1");
        taskNamesService.addTaskName("Task2");
        taskNamesService.addTaskName("Task3");

        return taskNamesService.getTaskNames();
    }
    //endregion
}
