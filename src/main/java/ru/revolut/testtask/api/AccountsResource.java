package ru.revolut.testtask.api;

import ru.revolut.testtask.controllers.AccountController;
import ru.revolut.testtask.dbmodel.Account;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
}
