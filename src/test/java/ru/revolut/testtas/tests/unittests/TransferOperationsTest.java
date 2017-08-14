package ru.revolut.testtas.tests.unittests;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.controllers.BasicOperationsController;
import ru.revolut.testtask.controllers.TransferOperationsController;
import ru.revolut.testtask.controllers.exceptions.EntityNotExistException;
import ru.revolut.testtask.controllers.exceptions.InvalidAccountForOperationException;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class TransferOperationsTest {
    private static Date testStartupTime;
    private static BasicOperationsController basicOperationsController;
    private static TransferOperationsController transferOperationsController;

    @BeforeClass
    public static void initialize() {
        testStartupTime = new Date();
        QuasiBeanManager.init("revolut-test-task-test");
        basicOperationsController = QuasiBeanManager.getBasicOperationsController();
        transferOperationsController = QuasiBeanManager.getTransferOperationsController();
    }

    @Before
    public void reconnectDb() {
        QuasiBeanManager.getDatabaseController().reconnect();
        BasicOperationsController basicOperationsController = QuasiBeanManager.getBasicOperationsController();
        basicOperationsController.createNewAccount(BigDecimal.valueOf(255.5)); // Id 1
        basicOperationsController.createNewAccount(BigDecimal.valueOf(255.5)); // Id 2
    }

    @Test
    public void simpleTransferTest() {
        Account accountOne = basicOperationsController.getAccountById(1L);
        Account accountTwo = basicOperationsController.getAccountById(2L);

        Transaction transaction = transferOperationsController.transferMoney(
                accountOne.getId(), accountTwo.getId(), BigDecimal.valueOf(55.5), "simpleTransferTest");

        accountOne = basicOperationsController.getAccountById(1L);
        accountTwo = basicOperationsController.getAccountById(2L);
        assertThat(
                accountOne.getDebit(),
                Matchers.comparesEqualTo(BigDecimal.valueOf(255.5).subtract(BigDecimal.valueOf(55.5)))
        );
        assertThat(
                accountTwo.getDebit(),
                Matchers.comparesEqualTo(BigDecimal.valueOf(255.5).add(BigDecimal.valueOf(55.5)))
        );

        assertEquals(accountOne, transaction.getSource());
        assertEquals(accountTwo, transaction.getDestination());
        assertEquals("simpleTransferTest", transaction.getDescription());
        assertTrue(transaction.getTime().after(testStartupTime));
        assertThat(
                transaction.getAmount(),
                Matchers.comparesEqualTo(BigDecimal.valueOf(55.5))
        );
        assertEquals(transaction, basicOperationsController.getTransactionById(transaction.getId()));

        assertEquals(2, basicOperationsController.getTransactionsOfAccount(accountOne.getId()).size());
        assertEquals(2, basicOperationsController.getTransactionsOfAccount(accountTwo.getId()).size());
    }

    @Test
    public void withdrawTest() {
        Transaction transaction = transferOperationsController.transferMoney(
                basicOperationsController.getAccountById(1L).getId(), null,
                BigDecimal.valueOf(55.5), "");
        Account account = basicOperationsController.getAccountById(1L);
        assertThat(
                account.getDebit(),
                Matchers.comparesEqualTo(BigDecimal.valueOf(255.5).subtract(BigDecimal.valueOf(55.5)))
        );
        assertEquals(2, basicOperationsController.getTransactionsOfAccount(account.getId()).size());

        assertThat(
                transaction.getAmount(),
                Matchers.comparesEqualTo(BigDecimal.valueOf(55.5))
        );
        assertEquals(account.getId(), transaction.getSource().getId());
        assertNull(transaction.getDestination());
    }

    @Test
    public void depositingTest() {
        Transaction transaction = transferOperationsController.transferMoney(
                null, 1L,
                BigDecimal.valueOf(55.5), "");
        Account account = basicOperationsController.getAccountById(1L);
        assertThat(
                account.getDebit(),
                Matchers.comparesEqualTo(BigDecimal.valueOf(255.5).add(BigDecimal.valueOf(55.5)))
        );
        assertEquals(2, basicOperationsController.getTransactionsOfAccount(account.getId()).size());

        assertThat(
                transaction.getAmount(),
                Matchers.comparesEqualTo(BigDecimal.valueOf(55.5))
        );
        assertEquals(account.getId(), transaction.getDestination().getId());
        assertNull(transaction.getSource());
    }

    @Test(expected = InvalidAccountForOperationException.class)
    public void bothAccountsNullExceptionThrowTest() {
        transferOperationsController.transferMoney(null, null, BigDecimal.valueOf(2), "");
    }

    @Test(expected = EntityNotExistException.class)
    public void incorrectBothAccountIdExceptionThrowTest() {
        transferOperationsController.transferMoney(2017L, 2018L, BigDecimal.valueOf(4), "");
    }

    @Test(expected = EntityNotExistException.class)
    public void incorrectSourceAccountIdExceptionThrowTest() {
        transferOperationsController.transferMoney(2017L, 1L, BigDecimal.valueOf(4), "");
    }

    @Test(expected = EntityNotExistException.class)
    public void incorrectDestinationAccountIdExceptionThrowTest() {
        transferOperationsController.transferMoney(1L, 2018L, BigDecimal.valueOf(4), "");
    }
}
