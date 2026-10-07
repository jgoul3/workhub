package com.goulette.workhub.project;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


class ProjectTest {

    private Project projectWithDates(LocalDate start, LocalDate end) {
        return new Project("Website Redesign", "Refresh the site",
                new BigDecimal("25000.00"),
                start, end);
    }

    private Project activeProject() {
        Project project = projectWithDates(null, null);
        project.start(LocalDate.of(2026, 10, 6));
        return project;
    }

    @Test
    void newProjectStartsInPlanning() {
        Project project = projectWithDates(LocalDate.of(2026, 10, 1),
                                            LocalDate.of(2026, 12, 15));

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.PLANNING);
    }

    @Test
    void constructorRejectsEndDateBeforeStartDate() {

        assertThatThrownBy(() -> projectWithDates(LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 10, 15)))
                .isInstanceOf(InvalidProjectDatesException.class);
    }

    @Test
    void startDateCanEqualEndDate() {
        LocalDate date = LocalDate.of(2026, 10, 1);
        Project project = projectWithDates(date, date);

        assertThat(project.getPlannedStartDate()).isEqualTo(date);
        assertThat(project.getPlannedEndDate()).isEqualTo(date);
    }

    @Test
    void constructorAllowsMissingStartDate() {
        Project project = projectWithDates(null,
                LocalDate.of(2026, 12, 15));

        assertThat(project.getPlannedStartDate()).isNull();
    }

    @Test
    void constructorAllowsMissingEndDate() {
        Project project = projectWithDates(LocalDate.of(2026, 10, 1),
                null);

        assertThat(project.getPlannedEndDate()).isNull();
    }

    @Test
    void constructorAllowsBothDatesMissing() {
        Project project = projectWithDates(null, null);

        assertThat(project.getPlannedStartDate()).isNull();
        assertThat(project.getPlannedEndDate()).isNull();
    }

    @Test
    void rescheduleUpdatesBothPlannedDates() {

        LocalDate newStart = LocalDate.of(2026, 11, 1);
        LocalDate newEnd = LocalDate.of(2027, 1, 15);
        Project project = projectWithDates(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15));

        project.reschedule(newStart, newEnd);

        assertThat(project.getPlannedStartDate()).isEqualTo(newStart);
        assertThat(project.getPlannedEndDate()).isEqualTo(newEnd);
    }

    @Test
    void rescheduleRejectsEndBeforeStartAndKeepsOriginalDates() {
        LocalDate originalStart = LocalDate.of(2026, 10, 1);
        LocalDate originalEnd = LocalDate.of(2026, 12, 15);
        Project project = projectWithDates(originalStart, originalEnd);

        assertThatThrownBy(() -> project.reschedule(
                LocalDate.of(2027, 1, 15),     // start...
                LocalDate.of(2026, 11, 30)))   // ...after end
                .isInstanceOf(InvalidProjectDatesException.class);

        assertThat(project.getPlannedStartDate()).isEqualTo(originalStart);
        assertThat(project.getPlannedEndDate()).isEqualTo(originalEnd);
    }

    @Test
    void rescheduleCanClearStartDate() {
        Project project = projectWithDates(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15));

        project.reschedule(null, LocalDate.of(2026, 12, 15));

        assertThat(project.getPlannedStartDate()).isNull();
    }

    @Test
    void rescheduleCanClearEndDate() {
        Project project = projectWithDates(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15));

        project.reschedule(LocalDate.of(2026, 10, 1), null);

        assertThat(project.getPlannedEndDate()).isNull();
    }

    @Test
    void startMovesPlanningProjectToActiveAndRecordsStartDate() {
        Project project = projectWithDates(null, null);
        LocalDate startedOn = LocalDate.of(2026, 10, 5);

        project.start(startedOn);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(project.getActualStartDate()).isEqualTo(startedOn);
    }

    @Test
    void startRejectsProjectsNotInPlanning() {
        Project project = activeProject();

        assertThatThrownBy(() -> project.start(LocalDate.of(2026, 10, 6)))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in ACTIVE to ACTIVE.");
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
    }

    @Test
    void putOnHoldUpdatesStatusToOnHold() {
        Project project = activeProject();

        project.putOnHold();
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ON_HOLD);
    }

    @Test
    void putOnHoldRejectsWhenStatusIsNotActive() {
        Project project = projectWithDates(null, null);

        assertThatThrownBy(() -> project.putOnHold())
                .isInstanceOf(InvalidStatusTransitionException.class);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.PLANNING);
    }

    @Test
    void resumeUpdatesStatusToActive() {
        Project project = activeProject();
        project.putOnHold();
        project.resume();

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
    }

    @Test
    void resumeRejectsProjectsInPlanning() {
        Project planningProject = projectWithDates(null, null);

        assertThatThrownBy(() -> planningProject.resume())
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in PLANNING to ACTIVE.");
        assertThat(planningProject.getStatus()).isEqualTo(ProjectStatus.PLANNING);
    }

    @Test
    void resumeRejectsProjectsInActive() {
        Project activeProject = activeProject();

        assertThatThrownBy((() -> activeProject.resume()))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in ACTIVE to ACTIVE.");
        assertThat(activeProject.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
    }

    @Test
    void resumeRejectsProjectsInCompleted() {
        Project completedProject = activeProject();
        completedProject.complete(LocalDate.of(2026, 10, 6));
        assertThatThrownBy(() -> completedProject.resume())
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in COMPLETED to ACTIVE.");
        assertThat(completedProject.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }

    @Test
    void resumeRejectsProjectsInCanceled() {
        Project canceledProject = activeProject();
        canceledProject.cancel();
        assertThatThrownBy(() -> canceledProject.resume())
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in CANCELED to ACTIVE.");
        assertThat(canceledProject.getStatus()).isEqualTo(ProjectStatus.CANCELED);
    }

    @Test
    void completeSetsStatusToCompleteAndRecordsActualEndDate() {
        Project project = activeProject();
        LocalDate actualEndDate = LocalDate.of(2026, 10, 7);
        project.complete(actualEndDate);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
        assertThat(project.getActualEndDate()).isEqualTo(actualEndDate);
    }

    @Test
    void completeRejectsProjectsNeverStarted() {
        Project project = projectWithDates(null, null);

        assertThatThrownBy(() -> project.complete(LocalDate.of(2026, 10, 7)))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in PLANNING to COMPLETED.");
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.PLANNING);
    }

    @Test
    void cancelWorksFromPlanning() {
        Project planningProject = projectWithDates(null, null);
        planningProject.cancel();

        assertThat(planningProject.getStatus()).isEqualTo(ProjectStatus.CANCELED);
    }

    @Test
    void cancelWorksFromActive() {
        Project activeProject = activeProject();
        activeProject.cancel();

        assertThat(activeProject.getStatus()).isEqualTo(ProjectStatus.CANCELED);
    }

    @Test
    void cancelWorksFromOnHold() {
        Project onHoldProject = activeProject();
        onHoldProject.putOnHold();
        onHoldProject.cancel();

        assertThat(onHoldProject.getStatus()).isEqualTo(ProjectStatus.CANCELED);
    }

    @Test
    void cancelRejectsProjectWithCanceledStatus() {
        Project canceledProject = activeProject();
        canceledProject.cancel();

        assertThatThrownBy(() -> canceledProject.cancel())
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in CANCELED to CANCELED.");
        assertThat(canceledProject.getStatus()).isEqualTo(ProjectStatus.CANCELED);
    }

    @Test
    void cancelRejectsProjectWithCompletedStatus() {
        Project completedProject = activeProject();
        completedProject.complete(LocalDate.of(2026, 10, 6));

        assertThatThrownBy(() -> completedProject.cancel())
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot set a project currently in COMPLETED to CANCELED.");
        assertThat(completedProject.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }
}