package ru.revolut.testtask.controllers;

import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.persistence.EntityManager;
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

    public TransferOperationsController(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public Transaction transferMoney(Long sourceId, Long destinationId, double amount, String description) {
        if (Objects.equals(sourceId, destinationId)) {
            throw new IllegalArgumentException("Source and destination account are the same");
        }

        if (sourceId == null) {
            return depositing(destinationId, amount);
        } else if (destinationId == null) {
            return withdraw(sourceId, amount);
        } else {
            return remittance(sourceId, destinationId, amount, description);
        }

    }
     private Transaction remittance(Long sourceId, Long destinationId, double amount, String description) {
        final Lock firstLock;
        final Lock secondLock;
        if(sourceId > destinationId)  { // Prevent deadlock, add order
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
            if(source.getDebit() < amount) {
                throw new IllegalStateException("Not enough money on account with id " + sourceId);
            }

            Account destination = entityManager.find(Account.class, destinationId);

            source.setDebit(source.getDebit() - amount);
            destination.setDebit(destination.getDebit() + amount);

            Transaction transaction = new Transaction(source, destination, amount, description);

            DatabaseController.doInTransaction(entityManager, () -> {
                entityManager.persist(source);
                entityManager.persist(destination);
                entityManager.persist(transaction);
            });
            return transaction;
        } finally {
            firstLock.unlock();
            secondLock.unlock();
            entityManager.close();
        }
    }

    private Transaction depositing(Long destinationId, double amount) {
        Lock lock = getLockForAccount(destinationId);

        EntityManager entityManager = databaseController.createEntityManager();
        lock.lock();
        try {
            Account destination = entityManager.find(Account.class, destinationId);

            destination.setDebit(destination.getDebit() - amount);
            Transaction transaction = new Transaction(null, destination, amount, "Depositing money");
            DatabaseController.doInTransaction(entityManager, () -> {
                entityManager.persist(destination);
                entityManager.persist(transaction);
            });
            return transaction;
        } finally {
            lock.unlock();
            entityManager.close();
        }
    }

    private Transaction withdraw(Long sourceId, double amount) {
        Lock lock = getLockForAccount(sourceId);

        EntityManager entityManager = databaseController.createEntityManager();
        lock.lock();
        try {
            Account source = entityManager.find(Account.class, sourceId);
            if(source.getDebit() < amount) {
                throw new IllegalStateException("Not enough money on account with id " + sourceId);
            }

            source.setDebit(source.getDebit() - amount);
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
        if(syncObject == null) {
            synchronized (syncObjectModificationMonitor) {
                syncObject = accountOperationMonitorsMap.get(accountId); // Check that no other thread have created it
                if(syncObject != null) {
                    syncObject = new ReentrantLock();
                    accountOperationMonitorsMap.put(accountId, syncObject);
                }
            }
        }

        return syncObject;
    }

}
