package com.goulette.workhub.project;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Project() {}

    public Project(String name, String description, BigDecimal budget, LocalDate plannedStartDate, LocalDate plannedEndDate) {
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.status = ProjectStatus.PLANNING;
        validatePlannedDates(plannedStartDate, plannedEndDate);
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public void reschedule(LocalDate plannedStartDate, LocalDate plannedEndDate) {
        validatePlannedDates(plannedStartDate, plannedEndDate);
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
    }

    private static void validatePlannedDates(LocalDate plannedStartDate, LocalDate plannedEndDate) {
        if (plannedStartDate != null && plannedEndDate != null && plannedStartDate.isAfter(plannedEndDate)) {
            throw new InvalidProjectDatesException(plannedStartDate, plannedEndDate);
        }
    }

    //getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getBudget() { return budget; }
    public LocalDate getPlannedStartDate() { return plannedStartDate; }
    public LocalDate getPlannedEndDate() { return plannedEndDate; }
    public LocalDate getActualStartDate() { return actualStartDate; }
    public LocalDate getActualEndDate() { return actualEndDate; }
    public ProjectStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    //setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public void setActualStartDate(LocalDate actualStartDate) { this.actualStartDate = actualStartDate; }
    public void setActualEndDate(LocalDate actualEndDate) { this.actualEndDate = actualEndDate; }
    public void setStatus(ProjectStatus status) { this.status = status; }
}
