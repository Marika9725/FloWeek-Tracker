package com.floweektracker.repository;

import com.floweektracker.TestHelper;
import com.floweektracker.model.*;
import com.floweektracker.service.TasksService;
import com.floweektracker.util.DialogUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class PlannerRepositoryTest {
    private final PlannerRepository plannerRepository = PlannerRepository.getRepository();
    private final Map<WeekDays, Map<LocalTime, SingleTask>> schedule = createSchedule();
    private File tempFile;

    @BeforeEach
    void setUp(TestInfo testInfo) throws IOException {
        var tags = testInfo.getTags();
        this.tempFile = Files.createTempFile("Planner", ".json").toFile();

        if (tags.contains("setTaskNames")) plannerRepository.setSchedule(schedule);
        if (tags.contains("setFile")) plannerRepository.setFile(tempFile);
        if (tags.contains("setFakeFile")) {
            this.tempFile = new File(System.getProperty("user.dir"), "test-folder");
            tempFile.mkdir();
            plannerRepository.setFile(tempFile);
        }
    }

    @AfterEach
    void tearDown() {
        plannerRepository.setSchedule(null);
        plannerRepository.setFile(null);

        try {Files.deleteIfExists(tempFile.toPath());} catch (IOException _) {}
    }

    @Nested
    class SaveTests {
        @Tag("setTaskNames")
        @Tag("setFile")
        @Test
        void shouldSaveScheduleToFile() {
            //given+when
            var isPlannerSaved = plannerRepository.savePlanner();
            var actualSchedule = readFile(tempFile);

            //then
            assertTrue(isPlannerSaved);
            assertThat(actualSchedule, is(schedule));
        }

        @Tag("setTaskNames")
        @Test
        void shouldReturnFalseWhenFileIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                var isPlannerSaved = plannerRepository.savePlanner();

                //then
                assertFalse(isPlannerSaved);
                assertThat(tempFile.length(), is(0L));
            }
        }

        @Tag("setTaskNames")
        @Test
        void shouldShowMessageDialogWhenFileIsNull() {
            try (var dialogUtils = mockStatic(DialogUtils.class)) {
                //given+when
                plannerRepository.savePlanner();

                //then
                dialogUtils.verify(() -> DialogUtils.showMessageDialog(anyString(), anyString()));
            }
        }

        @Tag("setFile")
        @Test
        void shouldReturnFalseWhenScheduleIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                var isPlannerSaved = plannerRepository.savePlanner();

                //then
                assertFalse(isPlannerSaved);
            }
        }

        @Tag("setFile")
        @Test
        void shouldShowMessageDialogWhenScheduleIsNull() {
            try (var dialogUtils = mockStatic(DialogUtils.class)) {
                //given+when
                plannerRepository.savePlanner();

                //then
                dialogUtils.verify(() -> DialogUtils.showMessageDialog(anyString(), anyString()));
            }
        }

        @Tag("setFile")
        @Test
        void savedFileShouldBeEmptyWhenScheduleIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                plannerRepository.savePlanner();

                //then
                assertThat(tempFile.length(), is(0L));
            }
        }

        private Map<WeekDays, Map<LocalTime, SingleTask>> readFile(File tempFile) {
            try (var reader = new FileReader(tempFile)) {
                var type = new TypeToken<Map<WeekDays, Map<LocalTime, SingleTask>>>() {}.getType();
                var gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeAdapter()).setPrettyPrinting().create();

                return gson.fromJson(reader, type);
            } catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    @Nested
    class LoadTests {
        @Tag("setTaskNames")
        @Tag("setFile")
        @Test
        void shouldLoadScheduleFromFile() {
            //given
            plannerRepository.savePlanner();
            plannerRepository.setSchedule(null);

            //when
            var isPlannerLoaded = plannerRepository.loadPlanner();
            var actualSchedule = plannerRepository.getSchedule();

            //then
            assertTrue(isPlannerLoaded);
            assertThat(actualSchedule, is(schedule));
        }

        @Test
        void shouldReturnFalseWhenFileIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                var isPlannerLoaded = plannerRepository.loadPlanner();

                //then
                assertFalse(isPlannerLoaded);
            }
        }

        @Test
        void loadedScheduleShouldBeNullWhenFileIsNull() {
            try (var _ = mockStatic(DialogUtils.class)) {
                //given+when
                plannerRepository.loadPlanner();
                var actualSchedule = plannerRepository.getSchedule();

                //then
                assertThat(actualSchedule, is(nullValue()));
            }
        }

        @Tag("setFakeFile") @Test
        void shouldShowMessageDialogWhenFileIsADirectory() {
            //given
            try (var dialogUtils = mockStatic(DialogUtils.class)) {
                //when
                plannerRepository.loadPlanner();

                //then
                dialogUtils.verify(() -> DialogUtils.showMessageDialog(anyString(), anyString()));
            }
        }

        @Tag("setFakeFile") @Test
        void savedPlannerShouldBeNullWhenFileIsADirectory() {
            //given
            try (var _ = mockStatic(DialogUtils.class)) {
                //when
                plannerRepository.loadPlanner();
                var actualPlanner = plannerRepository.getSchedule();

                //then
                assertThat(actualPlanner, is(nullValue()));
            }
        }
    }

    //region helper methods
    private Map<WeekDays, Map<LocalTime, SingleTask>> createSchedule() {
        var helper = new TestHelper();
        var tasksService = TasksService.getService();
        tasksService.addTask(helper.createBaseTask());
        tasksService.addTask(helper.createEditedTask());

        return tasksService.getSchedule();
    }
    //endregion
}