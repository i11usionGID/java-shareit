package ru.practicum.shareit.exceptions;

public class ErrorResponse {
    private final String e;

    public ErrorResponse(String e) {
        this.e = e;
    }

    public String getError() {
        return e;
    }
}
