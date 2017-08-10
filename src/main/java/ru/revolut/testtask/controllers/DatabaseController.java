package ru.revolut.testtask.controllers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class DatabaseController {
    private final String persistenceUnitName;
    private EntityManagerFactory entityManagerFactory;

    public DatabaseController(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void reconnect() {
        entityManagerFactory.close();
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public void doInTransaction(EntityManager entityManager, Runnable function) {
        try {
            entityManager.getTransaction().begin();
            function.run();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;
        }
    }
}
