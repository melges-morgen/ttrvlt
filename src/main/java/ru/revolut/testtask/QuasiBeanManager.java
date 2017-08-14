package ru.revolut.testtask;

import ru.revolut.testtask.controllers.BasicOperationsController;
import ru.revolut.testtask.controllers.DatabaseController;
import ru.revolut.testtask.controllers.TransferOperationsController;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class QuasiBeanManager {
    private static DatabaseController databaseController;
    private static BasicOperationsController basicOperationsController;
    private static TransferOperationsController operationsController;

    public static void init(String persistenceUnit) {
        QuasiBeanManager.databaseController = new DatabaseController(persistenceUnit);
        QuasiBeanManager.basicOperationsController = new BasicOperationsController(databaseController);
        QuasiBeanManager.operationsController = new TransferOperationsController(
                databaseController, basicOperationsController);
    }

    protected static void setDatabaseController(DatabaseController databaseController) {
        QuasiBeanManager.databaseController = databaseController;
    }

    protected static void setBasicOperationsController(BasicOperationsController basicOperationsController) {
        QuasiBeanManager.basicOperationsController = basicOperationsController;
    }

    protected static void setOperationsController(TransferOperationsController operationsController) {
        QuasiBeanManager.operationsController = operationsController;
    }

    public static DatabaseController getDatabaseController() {
        return databaseController;
    }

    public static BasicOperationsController getBasicOperationsController() {
        return basicOperationsController;
    }

    public static TransferOperationsController getTransferOperationsController() {
        return operationsController;
    }
}
