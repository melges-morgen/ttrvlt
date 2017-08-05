package ru.revolut.testtask.api;

import ru.revolut.testtask.dbmodel.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
@Path("transactions")
public class TransactionsResource {
    @GET
    @Path("{id}")
    public Transaction byId(@PathParam("id") Long transactionId) {
        return null;
    }
}
