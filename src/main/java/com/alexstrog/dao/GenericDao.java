package com.alexstrog.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {

    T save(T entity);

    Optional<T> getById(ID id);

    T update(T entity);

    Optional<T> delete(ID id);

    Optional<List<T>> getAll();
}