package ru.revolut.testtask.controllers;

import com.querydsl.jpa.impl.JPAQueryFactory;
import ru.revolut.testtask.dbmodel.Account;
import ru.revolut.testtask.dbmodel.QAccount;
import ru.revolut.testtask.dbmodel.QTransaction;
import ru.revolut.testtask.dbmodel.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class AccountController {
    private static final AccountController instance = new AccountController();
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("revolut-test-task-pu");

    private AccountController() {
    }

    public static AccountController getInstance() {
        return instance;
    }

    public Account createNewAccount(Double placement) {
        Account account = new Account();
        account.setDebit(placement);
        Transaction placementTransaction = new Transaction();
        placementTransaction.setDestination(account);
        placementTransaction.setAmount(placement);
        placementTransaction.setDescription("Initial money placement");

        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(account);
        entityManager.persist(placementTransaction);
        entityManager.getTransaction().commit();
        entityManager.close();

        return account;
    }

    public Account getAccountById(Long id) {
        return emf.createEntityManager().find(Account.class, id);
    }

    public List<Transaction> getTransactionsOfAccount(Long accountId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(emf.createEntityManager());
        QTransaction transaction = QTransaction.transaction;
        QAccount account = QAccount.account;

        return queryFactory.selectFrom(transaction)
                .where(account.id.eq(accountId)
                        .and(transaction.destination.eq(account))
                        .or(transaction.source.eq(account))
                ).fetch();
    }

    public Transaction getTransactionById(Long id) {
        return emf.createEntityManager().find(Transaction.class, id);
    }
}
