package com.github.funnygopher.crowddj.client.database;

public abstract class AbstractJooqDao<T extends Entity> implements Dao<T> {

    protected Class<T> mEntityClass;
    protected DatabaseManager mDatabaseManager;

    public AbstractJooqDao(Class<T> entityClass, DatabaseManager databaseManager) {
        mEntityClass = entityClass;
        mDatabaseManager = databaseManager;
    }
}
