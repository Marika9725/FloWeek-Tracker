package com.floweektracker.repository;

import com.floweektracker.TestHelper;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryConfiguratorTest {
    private final TestHelper testHelper = new TestHelper();
    private final RepositoryConfigurator repositoryConfigurator = RepositoryConfigurator.getConfigurator();
    private final File dataDir = new File("data");
    private final PlannerRepository plannerRepository = PlannerRepository.getRepository();
    private final TaskNamesRepository taskNamesRepository = TaskNamesRepository.getRepository();

    @BeforeEach
    void setUp(TestInfo info) {
        if (info.getTags().contains("createDataDir")) dataDir.mkdir();
    }

    @AfterEach
    void tearDown(TestInfo info) {
        if (info.getTags().contains("createDataDir")) {
            testHelper.deleteDirectory(dataDir);
            plannerRepository.setFile(null);
            taskNamesRepository.setFile(null);
        }

        plannerRepository.setFile(null);
    }

    @Test
    void instancesShouldBeSame() {
        //given+when
        var repositoryConfigurator2 = RepositoryConfigurator.getConfigurator();

        //then
        assertThat(repositoryConfigurator, is(repositoryConfigurator2));
    }

    @Nested
    class SetUpRepositories {
        @Tag("createDataDir")
        @Test
        void shouldSetFileToPlannerRepository() {
            //given
            var fileBefore = plannerRepository.getFile();

            //when
            var isPlannerRepositorySetUp = repositoryConfigurator.setUpRepositories(dataDir);
            var fileAfter = plannerRepository.getFile();

            //then
            assertAll(
                    () -> assertThat(fileBefore, is(nullValue())),
                    () -> assertTrue(isPlannerRepositorySetUp),
                    () -> assertThat(fileAfter.getName(), is("Planner.json"))
            );
        }

        @Tag("createDataDir")
        @Test
        void shouldSetFileToTaskNamesRepository() {
            //given
            var fileBefore = taskNamesRepository.getFile();

            //when
            var isTaskNamesRepositorySetUp = repositoryConfigurator.setUpRepositories(dataDir);
            var fileAfter = taskNamesRepository.getFile();

            //then
            assertAll(
                    () -> assertThat(fileBefore, is(nullValue())),
                    () -> assertTrue(isTaskNamesRepositorySetUp),
                    () -> assertThat(fileAfter.getName(), is("TaskNames.json"))
            );
        }

        @Test
        void shouldReturnFalseWhenDirectoryIsNull() {
            //given
            var fileBefore = plannerRepository.getFile();

            //when
            var isPlannerRepositorySetUp = repositoryConfigurator.setUpRepositories(null);
            var fileAfter = plannerRepository.getFile();

            //then
            assertAll(
                    () -> assertThat(fileBefore, is(nullValue())),
                    () -> assertFalse(isPlannerRepositorySetUp),
                    () -> assertThat(fileAfter, is(nullValue()))
            );
        }

        @Test
        void shouldReturnFalseWhenDirectoryNotExists() {
            //given
            var fileBefore = plannerRepository.getFile();

            //when
            var isPlannerRepositorySetUp = repositoryConfigurator.setUpRepositories(dataDir);
            var fileAfter = plannerRepository.getFile();

            //then
            assertAll(
                    () -> assertThat(fileBefore, is(nullValue())),
                    () -> assertFalse(isPlannerRepositorySetUp),
                    () -> assertThat(fileAfter, is(nullValue()))
            );
        }
    }
}