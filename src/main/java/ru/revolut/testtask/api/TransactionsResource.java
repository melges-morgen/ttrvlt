package ru.revolut.testtask.api;

import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.api.validation.ValidTransactionAmount;
import ru.revolut.testtask.controllers.BasicOperationsController;
import ru.revolut.testtask.controllers.TransferOperationsController;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
    private BasicOperationsController infoController;
    private TransferOperationsController transferOperationsController;

    public TransactionsResource() {
        this.infoController = QuasiBeanManager.getBasicOperationsController();
        this.transferOperationsController = QuasiBeanManager.getTransferOperationsController();
    }

    @GET
    @Path("{id}")
    public Transaction byId(@PathParam("id") Long transactionId) {
        return infoController.getTransactionById(transactionId);
    }

    @POST
    public Transaction transferMoney(@FormParam("source") Long sourceId,
                                     @FormParam("destination") Long destinationId,
                                     @FormParam("amount") @ValidTransactionAmount BigDecimal amount,
                                     @FormParam("description") @DefaultValue("") String description) {
        return transferOperationsController.transferMoney(sourceId, destinationId, amount, description);
    }
}
