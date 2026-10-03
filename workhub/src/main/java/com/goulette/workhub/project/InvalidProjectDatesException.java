package com.goulette.workhub.project;

import java.time.LocalDate;

public class InvalidProjectDatesException extends RuntimeException {
    public InvalidProjectDatesException(LocalDate plannedStartDate, LocalDate plannedEndDate) {
        super("Planned start date of " + plannedStartDate +
                " cannot be after planned end date of " + plannedEndDate + ".");
    }
}
