package ru.revolut.testtask.controllers.exceptions;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class InvalidAccountForOperationException extends RuntimeException {
    public InvalidAccountForOperationException() {
    }

    public InvalidAccountForOperationException(String message) {
        super(message);
    }
}
