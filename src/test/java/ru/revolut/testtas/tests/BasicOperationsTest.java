package ru.revolut.testtas.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.controllers.BasicOperationsController;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */

public class BasicOperationsTest {
    private static Date testStartupTime;

    @BeforeClass
    public static void initialize() {
        testStartupTime = new Date();
        QuasiBeanManager.init("revolut-test-task-test");
    }

    @Before
    public void reconnectDb() {
        QuasiBeanManager.getDatabaseController().reconnect();
    }

    @Test
    public void createEmptyAccountTest() {
        BasicOperationsController basicOperationsController = QuasiBeanManager.getBasicOperationsController();
        Account createdAccount = basicOperationsController.createNewAccount(0.0);
        assertEquals(0.0, createdAccount.getDebit(), 0.001);
        assertEquals((long) 1, (long) createdAccount.getId());
        assertTrue(createdAccount.getOpenDate().after(testStartupTime));

        Account accountInDb = basicOperationsController.getAccountById(createdAccount.getId());
        assertEquals(createdAccount, accountInDb);

        List<Transaction> relatedTransactions = basicOperationsController.getTransactionsOfAccount(createdAccount.getId());
        assertEquals(0, relatedTransactions.size());
    }

    @Test
    public void createAccountWithMoneyTest() {
        BasicOperationsController basicOperationsController = QuasiBeanManager.getBasicOperationsController();
        Account createdAccount = basicOperationsController.createNewAccount(25.5);
        assertEquals(25.5, createdAccount.getDebit(), 0.001);
        assertEquals(1L, (long) createdAccount.getId());
        assertTrue(createdAccount.getOpenDate().after(testStartupTime));

        Account accountInDb = basicOperationsController.getAccountById(createdAccount.getId());
        assertEquals(createdAccount.getDebit(), accountInDb.getDebit(),0.001);
        assertEquals((long) createdAccount.getId(), (long) accountInDb.getId());
        assertTrue(createdAccount.getOpenDate().equals(accountInDb.getOpenDate()));

        List<Transaction> relatedTransactions = basicOperationsController.getTransactionsOfAccount(createdAccount.getId());
        assertEquals(1, relatedTransactions.size());
        Transaction transaction = relatedTransactions.get(0);
        assertEquals(1L, (long) transaction.getId());
        assertEquals(25.5, transaction.getAmount(), 0.001);
        assertEquals(1L, (long) transaction.getDestination().getId());
        assertEquals(null, transaction.getSource());
        assertEquals("Initial money placement", transaction.getDescription());
        assertTrue(transaction.getTime().before(new Date()));
        assertTrue(transaction.getTime().after(testStartupTime));

        Transaction transactionById = basicOperationsController.getTransactionById(1L);
        assertEquals(transaction, transactionById);
    }

    @Test
    public void getAccountByIdTest() {
        BasicOperationsController basicOperationsController = QuasiBeanManager.getBasicOperationsController();
        Account createdAccount = basicOperationsController.createNewAccount(0.0);
        Account accountInDb = basicOperationsController.getAccountById(createdAccount.getId());
        assertEquals(createdAccount, accountInDb);
    }

    @Test
    public void getTransactionByIdTest() {
        BasicOperationsController basicOperationsController = QuasiBeanManager.getBasicOperationsController();
        basicOperationsController.createNewAccount(2.34);
        Transaction transaction = basicOperationsController.getTransactionById(1L);
        assertEquals(1L, (long) transaction.getId());
    }
}
