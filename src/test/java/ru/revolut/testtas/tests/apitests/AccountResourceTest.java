package ru.revolut.testtas.tests.apitests;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.revolut.testtask.EmbeddedServer;
import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class AccountResourceTest {
    private WebTarget target;

    @Before
    public void startServer() throws Exception {
        QuasiBeanManager.init("revolut-test-task-test");
        EmbeddedServer.startServer();

        Client client = ClientBuilder.newClient();
        target = client.target("http://localhost:" + EmbeddedServer.SERVER_PORT);
    }

    @After
    public void stopServer() throws Exception {
        EmbeddedServer.stopServer();
    }

    @Test
    public void createTest() {
        MultivaluedMap<String, String> data = new MultivaluedStringMap();
        data.add("placement", "255.5");
        Account createdEntity = target.path("api")
                .path("accounts")
                .request()
                .post(Entity.form(data))
                .readEntity(Account.class);
        Account getEntity = QuasiBeanManager.getBasicOperationsController().getAccountById(createdEntity.getId());
        assertEquals(createdEntity.getId(), getEntity.getId());
        assertNotNull(createdEntity.getId());
        assertThat(createdEntity.getDebit(), Matchers.comparesEqualTo(BigDecimal.valueOf(255.5)));
        Account createdEntityZeroPlacement = target.path("api")
                .path("accounts")
                .request()
                .post(Entity.form(new MultivaluedStringMap()))
                .readEntity(Account.class);
        Account getEntityZero =
                QuasiBeanManager.getBasicOperationsController().getAccountById(createdEntityZeroPlacement.getId());
        assertEquals(createdEntityZeroPlacement.getId(), getEntityZero.getId());
        assertNotNull(createdEntityZeroPlacement.getId());
        assertThat(createdEntityZeroPlacement.getDebit(), Matchers.comparesEqualTo(BigDecimal.valueOf(0)));
    }

    @Test
    public void getTest() {
        Account createdEntity =
                QuasiBeanManager.getBasicOperationsController().createNewAccount(BigDecimal.valueOf(255.5));

        Account getEntity = target.path("api")
                .path("accounts")
                .path(createdEntity.getId().toString())
                .request()
                .get()
                .readEntity(Account.class);
        assertEquals(createdEntity.getId(), getEntity.getId());
        assertNotNull(createdEntity.getId());
        assertThat(createdEntity.getDebit(), Matchers.comparesEqualTo(BigDecimal.valueOf(255.5)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getTransactionsTest() {
        Account createdEntity =
                QuasiBeanManager.getBasicOperationsController().createNewAccount(BigDecimal.valueOf(255.5));

        Response response = target.path("api")
                .path("accounts")
                .path(createdEntity.getId().toString())
                .path("transactions")
                .request()
                .get(Response.class);
        List<Transaction> responseEntity = response.readEntity(new GenericType<List<Transaction>>() {});
        assertEquals(1, responseEntity.size());
        assertNotNull(responseEntity.get(0).getId());
    }

    @Test
    public void createAccountWithWrongPlacementShouldThrow400Error() {
        MultivaluedMap<String, String> data = new MultivaluedStringMap();
        data.add("placement", "-255.5");
        Response response = target.path("api")
                .path("accounts")
                .request()
                .post(Entity.form(data));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void getWithWrongIdThrows404Error() {
        Response response = target.path("api")
                .path("accounts")
                .path("1190")
                .request()
                .get();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void getTransactionsWithWrongAccountIdThrows404Error() {
        Response response = target.path("api")
                .path("accounts")
                .path("1190")
                .path("transactions")
                .request()
                .get();
        assertEquals(404, response.getStatus());
    }
}
