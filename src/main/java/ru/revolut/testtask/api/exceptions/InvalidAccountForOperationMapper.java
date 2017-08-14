package ru.revolut.testtask.api.exceptions;

import ru.revolut.testtask.controllers.exceptions.InvalidAccountForOperationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class InvalidAccountForOperationMapper implements ExceptionMapper<InvalidAccountForOperationException> {
    @Override
    public Response toResponse(InvalidAccountForOperationException exception) {
        return Response.status(400).entity(exception).build();
    }
}
