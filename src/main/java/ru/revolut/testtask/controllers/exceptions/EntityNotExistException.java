package ru.revolut.testtask.controllers.exceptions;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class EntityNotExistException extends RuntimeException {
    public EntityNotExistException() {
    }

    public EntityNotExistException(String message) {
        super(message);
    }

    public EntityNotExistException(long entityId) {
        super(String.format("Requested entity with id %d doesn't exist", entityId));
    }
}
