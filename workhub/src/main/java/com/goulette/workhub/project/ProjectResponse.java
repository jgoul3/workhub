package com.goulette.workhub.project;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        BigDecimal budget,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        LocalDate actualStartDate,
        LocalDate actualEndDate,
        ProjectStatus status,
        Instant createdAt
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getBudget(),
                project.getPlannedStartDate(),
                project.getPlannedEndDate(),
                project.getActualStartDate(),
                project.getActualEndDate(),
                project.getStatus(),
                project.getCreatedAt()
        );
    }
}