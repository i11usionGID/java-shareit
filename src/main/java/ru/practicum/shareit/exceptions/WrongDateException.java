package ru.practicum.shareit.exceptions;

public class WrongDateException extends RuntimeException {
    public WrongDateException(String message) {
        super(message);
    }
}
