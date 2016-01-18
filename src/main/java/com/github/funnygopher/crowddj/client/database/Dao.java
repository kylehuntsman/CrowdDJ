package com.github.funnygopher.crowddj.client.database;

public interface Dao<T extends Entity> {
    T create(T entity);
    T get(Long id);
    void update(T entity);
    void delete(T entity);
}
