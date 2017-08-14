package ru.revolut.testtas.tests.unittests;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.revolut.testtask.QuasiBeanManager;
import ru.revolut.testtask.controllers.BasicOperationsController;
import ru.revolut.testtask.controllers.TransferOperationsController;
import ru.revolut.testtask.dbmodel.Account;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class StressTest {
    private static BasicOperationsController basicOperationsController;
    private static TransferOperationsController transferOperationsController;

    @BeforeClass
    public static void initialize() {
        QuasiBeanManager.init("revolut-test-task-test-stress");
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
    public void stressTest() throws InterruptedException {
        final int threadNum = 64;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for(int i = 0; i < threadNum; ++i) {
            final int tNum = i;
            executorService.execute(() -> {
                if((tNum % 2) == 0) {
                    for(int j = 0; j < 100; ++j) {
                        transferOperationsController.transferMoney(1L, 2L, BigDecimal.valueOf(0.01), "");
                    }
                } else {
                    transferOperationsController.transferMoney(2L, 1L, BigDecimal.valueOf(0.02), "");
                }
            });
        }

        executorService.awaitTermination(2L, TimeUnit.MINUTES);
        Account accountOne = basicOperationsController.getAccountById(1L);
        Account accountTwo = basicOperationsController.getAccountById(2L);
        assertThat(
                accountOne.getDebit().add(accountTwo.getDebit()),
                Matchers.comparesEqualTo(BigDecimal.valueOf(255.5).add(BigDecimal.valueOf(255.5)))
        );
    }
}
