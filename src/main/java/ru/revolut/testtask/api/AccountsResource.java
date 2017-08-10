package ru.revolut.testtask.api;

import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.controllers.BasicOperationsController;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
@Path("/accounts/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {
    private BasicOperationsController accountController;

    public AccountsResource() {
        accountController = QuasiBeanManager.getBasicOperationsController();
    }

    @GET
    public Account byId(@PathParam("id") Long accountId) {
        return accountController.getAccountById(accountId);
    }

    @POST
    public Account create(@FormParam("placement") @DefaultValue("0.0") Double placement) {
        return accountController.createNewAccount(placement);
    }

    @GET
    @Path("/transactions")
    public List<Transaction> transactions(@PathParam("id") Long accountId) {
        return accountController.getTransactionsOfAccount(accountId);
    }
}
