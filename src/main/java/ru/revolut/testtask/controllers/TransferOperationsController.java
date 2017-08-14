package ru.revolut.testtask.controllers;

import ru.revolut.testtask.controllers.exceptions.EntityNotExistException;
import ru.revolut.testtask.controllers.exceptions.InvalidAccountForOperationException;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class TransferOperationsController {
    private Map<Long, Lock> accountOperationMonitorsMap = new ConcurrentHashMap<>();
    private final Object syncObjectModificationMonitor = new Object();

    private final DatabaseController databaseController;
    private final BasicOperationsController basicOperationsController;

    public TransferOperationsController(DatabaseController databaseController,
                                        BasicOperationsController basicOperationsController) {
        this.databaseController = databaseController;
        this.basicOperationsController = basicOperationsController;
    }

    public Transaction transferMoney(Long sourceId, Long destinationId, BigDecimal amount, String description) {
        if (Objects.equals(sourceId, destinationId)) {
            throw new InvalidAccountForOperationException("Source and destination account are the same");
        }

        if (sourceId == null) {
            return depositing(destinationId, amount);
        } else if (destinationId == null) {
            return withdraw(sourceId, amount);
        } else {
            return remittance(sourceId, destinationId, amount, description);
        }

    }

    private Transaction remittance(Long sourceId, Long destinationId, BigDecimal amount, String description) {
        if (!basicOperationsController.isAccountExist(destinationId)) {
            throw new EntityNotExistException(destinationId);
        }

        if(!basicOperationsController.isAccountExist(sourceId)) {
            throw new EntityNotExistException(sourceId);
        }

        final Lock firstLock;
        final Lock secondLock;
        if (sourceId > destinationId) { // Prevent deadlock, add order
            firstLock = getLockForAccount(sourceId);
            secondLock = getLockForAccount(destinationId);
        } else {
            firstLock = getLockForAccount(destinationId);
            secondLock = getLockForAccount(sourceId);
        }

        EntityManager entityManager = databaseController.createEntityManager();
        firstLock.lock();
        secondLock.lock();
        try {
            Account source = entityManager.find(Account.class, sourceId);
            if (source.getDebit().compareTo(amount) < 0) {
                throw new InvalidAccountForOperationException("Not enough money on account with id " + sourceId);
            }

            Account destination = entityManager.find(Account.class, destinationId);

            source.setDebit(source.getDebit().subtract(amount));
            destination.setDebit(destination.getDebit().add(amount));

            Transaction transaction = new Transaction(source, destination, amount, description);

            DatabaseController.doInTransaction(entityManager, () -> {
                entityManager.persist(source);
                entityManager.persist(destination);
                entityManager.persist(transaction);
            });
            return transaction;
        } finally {
            entityManager.close();
            firstLock.unlock();
            secondLock.unlock();
        }
    }

    private Transaction depositing(Long destinationId, BigDecimal amount) {
        if (!basicOperationsController.isAccountExist(destinationId)) {
            throw new EntityNotExistException(destinationId);
        }

        Lock lock = getLockForAccount(destinationId);

        EntityManager entityManager = databaseController.createEntityManager();
        lock.lock();
        try {
            Account destination = entityManager.find(Account.class, destinationId);

            destination.setDebit(destination.getDebit().add(amount));
            Transaction transaction = new Transaction(null, destination, amount, "Depositing money");
            DatabaseController.doInTransaction(entityManager, () -> {
                entityManager.persist(destination);
                entityManager.persist(transaction);
            });
            return transaction;
        } finally {
            entityManager.close();
            lock.unlock();
        }
    }

    private Transaction withdraw(Long sourceId, BigDecimal amount) {
        if (!basicOperationsController.isAccountExist(sourceId)) {
            throw new EntityNotExistException(sourceId);
        }

        Lock lock = getLockForAccount(sourceId);

        EntityManager entityManager = databaseController.createEntityManager();
        lock.lock();
        try {
            Account source = entityManager.find(Account.class, sourceId);
            if (source.getDebit().compareTo(amount) < 0) {
                throw new InvalidAccountForOperationException("Not enough money on account with id " + sourceId);
            }

            source.setDebit(source.getDebit().subtract(amount));
            Transaction transaction = new Transaction(source, null, amount, "Withdraw money");
            DatabaseController.doInTransaction(entityManager, () -> {
                entityManager.persist(source);
                entityManager.persist(transaction);
            });

            return transaction;
        } finally {
            lock.unlock();
        }
    }

    private Lock getLockForAccount(Long accountId) {
        Lock syncObject = accountOperationMonitorsMap.get(accountId);
        if (syncObject == null) {
            synchronized (syncObjectModificationMonitor) {
                syncObject = accountOperationMonitorsMap.computeIfAbsent(accountId, k -> new ReentrantLock());
            }
        }

        return syncObject;
    }
}
