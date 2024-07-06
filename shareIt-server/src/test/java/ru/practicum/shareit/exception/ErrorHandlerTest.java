package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void dataAlreadyExistException() {
        DataAlreadyExistException dataAlreadyExistException = new DataAlreadyExistException("error");
        ErrorResponse errorResponse = errorHandler.dataAlreadyExistException(dataAlreadyExistException);
        assertEquals(dataAlreadyExistException.getMessage(), errorResponse.getError());
    }

    @Test
    void dataNotFoundException() {
        DataNotFoundException dataNotFoundException = new DataNotFoundException("error");
        ErrorResponse errorResponse = errorHandler.dataNotFoundException(dataNotFoundException);
        assertEquals(dataNotFoundException.getMessage(), errorResponse.getError());
    }

    @Test
    void wrongOwnerException() {
        WrongOwnerException wrongOwnerException = new WrongOwnerException("error");
        ErrorResponse errorResponse = errorHandler.wrongOwnerException(wrongOwnerException);
        assertEquals(wrongOwnerException.getMessage(), errorResponse.getError());
    }

    @Test
    void wrongDateException() {
        WrongDateException wrongDateException = new WrongDateException("error");
        ErrorResponse errorResponse = errorHandler.wrongDateException(wrongDateException);
        assertEquals(wrongDateException.getMessage(), errorResponse.getError());
    }

    @Test
    void selfBookingException() {
        SelfBookingException selfBookingException = new SelfBookingException("error");
        ErrorResponse errorResponse = errorHandler.selfBookingException(selfBookingException);
        assertEquals(selfBookingException.getMessage(), errorResponse.getError());
    }

    @Test
    void unavailableItemException() {
        UnavailableItemException unavailableItemException = new UnavailableItemException("error");
        ErrorResponse errorResponse = errorHandler.unavailableItemException(unavailableItemException);
        assertEquals(unavailableItemException.getMessage(), errorResponse.getError());
    }

    @Test
    void statusAlreadyApprovedException() {
        StatusAlreadyApprovedException statusAlreadyApprovedException = new StatusAlreadyApprovedException("error");
        ErrorResponse errorResponse = errorHandler.statusAlreadyApprovedException(statusAlreadyApprovedException);
        assertEquals(statusAlreadyApprovedException.getMessage(), errorResponse.getError());
    }

    @Test
    void wrongAuthorException() {
        WrongAuthorException wrongAuthorException = new WrongAuthorException("error");
        ErrorResponse errorResponse = errorHandler.wrongAuthorException(wrongAuthorException);
        assertEquals(wrongAuthorException.getMessage(), errorResponse.getError());
    }
}