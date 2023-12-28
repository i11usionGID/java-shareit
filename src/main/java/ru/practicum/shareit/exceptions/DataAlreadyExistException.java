package ru.practicum.shareit.exceptions;

public class DataAlreadyExistException extends RuntimeException {
    public DataAlreadyExistException() {
        super();
    }

    public DataAlreadyExistException(String message) {
        super(message);
    }
}
