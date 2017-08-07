package ru.revolut.testtask.api;

import ru.revolut.testtask.controllers.AccountController;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.ws.rs.*;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
@Path("/accounts")
public class AccountsResource {
    private AccountController accountController;

    public AccountsResource() {
        accountController = AccountController.getInstance();
    }

    @GET
    @Path("/{id}")
    public Account byId(@PathParam("id") Long accountId) {
        return accountController.getAccountById(accountId);
    }

    @POST
    public Account create(@FormParam("placement") @DefaultValue("0.0") Double placement) {
        return accountController.createNewAccount(placement);
    }

    @GET
    public List<Transaction> transactions() {
        return Collections.emptyList();
    }
}
