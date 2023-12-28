package ru.practicum.shareit.exceptions;

public class WrongOwnerException extends RuntimeException {
    public WrongOwnerException() {
        super();
    }

    public WrongOwnerException(String message) {
        super(message);
    }
}
