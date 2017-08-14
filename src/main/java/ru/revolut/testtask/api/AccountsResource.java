package ru.revolut.testtask.api;

import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.controllers.BasicOperationsController;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {
    private BasicOperationsController accountController;

    public AccountsResource() {
        accountController = QuasiBeanManager.getBasicOperationsController();
    }

    @GET
    @Path("/{id}")
    public Response byId(@PathParam("id") Long accountId) {
        Account account = accountController.getAccountById(accountId);
        if(account != null) {
            return Response.ok(account).build();
        }

        return Response.status(404).build();
    }

    @POST
    public Account create(@FormParam("placement") @DefaultValue("0.0") @Min(0) BigDecimal placement) {
        return accountController.createNewAccount(placement);
    }

    @GET
    @Path("/{id}/transactions")
    public List<Transaction> transactions(@PathParam("id") Long accountId) {
        return accountController.getTransactionsOfAccount(accountId);
    }
}
