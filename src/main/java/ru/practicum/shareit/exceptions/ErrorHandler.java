package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse dataAlreadyExistException(final DataAlreadyExistException e) {
        log.info("Возникло исключение с повторяющимися данными.");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse dataNotFoundException(final DataNotFoundException e) {
        log.info("Возникло исключение с отсутствием данных.");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse wrongOwnerException(final WrongOwnerException e) {
        log.info("Попытка изменения объектов, не принадлежащих пользователю.");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse wrongDateException(final WrongDateException e) {
        log.info("Неправильная дата бронирования.");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse selfBookingException(final SelfBookingException e) {
        log.info("Попытка забронировать свою вещь.");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse unavailableItemException(final UnavailableItemException e) {
        log.info("Попытка забронировать недоступный предмет.");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse statusAlreadyApprovedException(final StatusAlreadyApprovedException e) {
        log.info("Попытка подтвердить уже подтвержденное бронирование.");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse wrongAuthorException(final WrongAuthorException e) {
        log.info("Попытка оставить комментарий без бронирования.");
        return new ErrorResponse(e.getMessage());
    }
}
