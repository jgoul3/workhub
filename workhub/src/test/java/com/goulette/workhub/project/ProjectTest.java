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
    void nullPlannedDates() {
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
}