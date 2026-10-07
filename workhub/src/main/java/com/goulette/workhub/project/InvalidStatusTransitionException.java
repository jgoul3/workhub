package com.goulette.workhub.project;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(ProjectStatus oldStatus, ProjectStatus newStatus) {
        super("Cannot set a project currently in " + oldStatus + " to " + newStatus + ".");
    }
}
