package com.goulette.workhub.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProjectRequest(
        @NotBlank(message = "Name cannot be blank.")
        @Size(max = 255)
        String name,

        String description,

        @NotNull
        @PositiveOrZero
        BigDecimal budget,

        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}

