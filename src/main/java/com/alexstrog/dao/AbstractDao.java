package com.alexstrog.dao;

import com.alexstrog.service.QueryBuilder;
import com.alexstrog.utill.StringHelper;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements GenericDao<T, ID> {

    private final Logger logger = Logger.getLogger(AbstractDao.class);
    private final Connection connection = ConnectionUtil.connect();

    @Override
    public T save(T entity) {
        String query = QueryBuilder.getSaveQuery(entity);
        try {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                try {
                    Field idField = entity.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(entity, keys.getObject(1));
                } catch (NoSuchFieldException e) {
                    logger.error("Can't get id field from entity", e);
                } catch (IllegalAccessException e) {
                    logger.error("Can't set id to entity", e);
                }
            }
        } catch (SQLException e) {
            logger.error("Can't save entity to DB", e);
        }
        return entity;
    }

    @Override
    public Optional<T> getById(ID id) {
        Class<T> entityClass = getGenericTypeClass();
        String query = QueryBuilder.getSelectQuery(entityClass, id);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            T entity = entityClass.newInstance();
            if (resultSet.next()) {
                for (Field field : entityClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(entity, resultSet.getObject(StringHelper.toSnakeCase(field.getName())));
                }
                return Optional.of(entity);
            }
        } catch (Exception e) {
            logger.error("Can't get entity from DB", e);
        }
        return Optional.empty();
    }

    @Override
    public T update(T entity) {
        String query = QueryBuilder.getUpdateQuery(entity);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.error("Can't update entity from DB", e);
        }
        return entity;
    }

    @Override
    public Optional<T> delete(ID id) {
        String query = QueryBuilder.getDeleteQuery(getGenericTypeClass() ,id);
        Optional<T> optionalEntity = getById(id);
        if (optionalEntity.isPresent()) {
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                return optionalEntity;
            } catch (SQLException e) {
                logger.error("Can't delete entity from DB", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<T>> getAll() {
        List<T> entityList = new ArrayList<>();
        Class<T> entityClass = getGenericTypeClass();
        String query = QueryBuilder.getSelectAllQuery(getGenericTypeClass());
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                T entity = entityClass.newInstance();
                for (Field field : entityClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(entity, resultSet.getObject(StringHelper.toSnakeCase(field.getName())));
                }
                entityList.add(entity);
            }
        } catch (Exception e) {
            logger.error("Can't get all entities from DB", e);
        }
        return Optional.of(entityList);
    }

    private Class<T> getGenericTypeClass() {
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0]);
    }
}
