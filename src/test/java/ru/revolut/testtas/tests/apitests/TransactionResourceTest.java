package ru.revolut.testtas.tests.apitests;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.revolut.testtask.EmbeddedServer;
import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class TransactionResourceTest {
    private WebTarget target;

    @Before
    public void startServer() throws Exception {
        QuasiBeanManager.init("revolut-test-task-test");
        EmbeddedServer.startServer();

        Client client = ClientBuilder.newClient();
        target = client.target("http://localhost:" + EmbeddedServer.SERVER_PORT);

        QuasiBeanManager.getBasicOperationsController().createNewAccount(new BigDecimal(255.5));
        QuasiBeanManager.getBasicOperationsController().createNewAccount(new BigDecimal(255.5));
    }

    @After
    public void stopServer() throws Exception {
        EmbeddedServer.stopServer();
    }

    @Test
    public void makeTransactionTest() {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("source", "1");
        params.add("destination", "2");
        params.add("amount", "255.5");
        params.add("description", "No");
        Transaction response = target.path("api")
                .path("transactions")
                .request()
                .post(Entity.form(params))
                .readEntity(Transaction.class);
        assertNotNull(response.getId());
        assertThat(response.getAmount(), Matchers.comparesEqualTo(BigDecimal.valueOf(255.5)));
    }

    @Test
    public void depositMoneyTest() {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("destination", "2");
        params.add("amount", "500");
        Transaction response = target.path("api")
                .path("transactions")
                .request()
                .post(Entity.form(params))
                .readEntity(Transaction.class);
        assertNotNull(response.getId());
        assertThat(response.getAmount(), Matchers.comparesEqualTo(BigDecimal.valueOf(500.0)));
    }

    @Test
    public void withdrawMoneyTest() {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("source", "2");
        params.add("amount", "200");
        Transaction response = target.path("api")
                .path("transactions")
                .request()
                .post(Entity.form(params))
                .readEntity(Transaction.class);
        assertNotNull(response.getId());
        assertThat(response.getAmount(), Matchers.comparesEqualTo(BigDecimal.valueOf(200.0)));
    }

    @Test
    public void bothAccountsNullThrows400ErrorTest() {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("amount", "200");
        Response response = target.path("api")
                .path("transactions")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void amountZeroThrows400ErrorTest() {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("destination", "2");
        params.add("source", "2");
        params.add("amount", "0");
        Response response = target.path("api")
                .path("transactions")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void amountLessZeroThrows400ErrorTest() {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("destination", "2");
        params.add("source", "2");
        params.add("amount", "-2");
        Response response = target.path("api")
                .path("transactions")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }
}
