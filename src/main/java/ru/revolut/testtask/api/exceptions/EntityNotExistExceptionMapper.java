package ru.revolut.testtask.api.exceptions;

import ru.revolut.testtask.controllers.exceptions.EntityNotExistException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class EntityNotExistExceptionMapper implements ExceptionMapper<EntityNotExistException> {
    @Override
    public Response toResponse(EntityNotExistException exception) {
        return Response.status(404).entity(exception.getMessage()).build();
    }
}
