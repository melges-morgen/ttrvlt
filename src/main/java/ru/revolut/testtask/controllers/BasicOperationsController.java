package ru.revolut.testtask.controllers;

import com.querydsl.jpa.impl.JPAQueryFactory;
import ru.revolut.testtask.controllers.exceptions.EntityNotExistException;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.QAccount;
import ru.revolut.testtask.dbmodel.QTransaction;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class BasicOperationsController {
    private final DatabaseController databaseController;

    public BasicOperationsController(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public Account getAccountById(Long id) {
        return databaseController.createEntityManager().find(Account.class, id);
    }

    public List<Transaction> getTransactionsOfAccount(Long accountId) {
        if(!isAccountExist(accountId)) {
            throw new EntityNotExistException(accountId);
        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(databaseController.createEntityManager());
        QTransaction transaction = QTransaction.transaction;
        QAccount account = QAccount.account;

        return queryFactory.selectFrom(transaction).from(transaction, account)
                .where(account.id.eq(accountId)
                        .and(transaction.destination.eq(account).or(transaction.source.eq(account)))
                ).fetch();
    }

    public Transaction getTransactionById(Long id) {
        return databaseController.createEntityManager().find(Transaction.class, id);
    }

    public Account createNewAccount(BigDecimal placement) {
        if(placement.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial placement can't be below zero");
        }

        EntityManager entityManager = databaseController.createEntityManager();
        entityManager.getTransaction().begin();

        Account account = new Account();
        account.setDebit(placement);
        if(placement.compareTo(BigDecimal.ZERO) > 0) {
            Transaction placementTransaction = new Transaction(null, account, placement,
                    "Initial money placement");
            entityManager.persist(account);
            entityManager.persist(placementTransaction);
        } else {
            entityManager.persist(account);
        }

        entityManager.getTransaction().commit();
        entityManager.close();

        return account;
    }

    public boolean isAccountExist(Long accountId) {
        EntityManager entityManager = databaseController.createEntityManager();
        return entityManager.find(Account.class, accountId) != null;
    }
}
