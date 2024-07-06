package ru.practicum.shareit.exceptions;

public class WrongAuthorException extends RuntimeException {
    public WrongAuthorException(String message) {
        super(message);
    }
}
