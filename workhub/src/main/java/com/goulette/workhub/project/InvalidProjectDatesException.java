package com.goulette.workhub.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProjectDatesException extends RuntimeException {
    public InvalidProjectDatesException(LocalDate plannedStartDate, LocalDate plannedEndDate) {
        super("Planned start date of " + plannedStartDate +
                " cannot be after planned end date of " + plannedEndDate + ".");
    }
}
