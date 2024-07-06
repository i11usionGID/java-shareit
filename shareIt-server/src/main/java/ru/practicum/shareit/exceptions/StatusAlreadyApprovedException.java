package ru.practicum.shareit.exceptions;

public class StatusAlreadyApprovedException extends RuntimeException {
    public StatusAlreadyApprovedException(String message) {
        super(message);
    }
}
