package com.alexstrog.dao.implementation;

import com.alexstrog.dao.ConnectionUtil;
import com.alexstrog.dao.GenericDao;
import com.alexstrog.utill.StringHelper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbstractDao<T, ID> implements GenericDao<T, ID> {

    private Class<T> entityClass;
    private Field[] classFields;

    private final Connection connection = ConnectionUtil.connect();

    public AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
        classFields = this.entityClass.getDeclaredFields();
    }

    @Override
    public T save(T entity) {
        String query = getSaveQuery(entity);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
//            ResultSet keys = statement.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Optional<T> getById(ID id) {
        String query = getSelectQuery(id);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            T entity = entityClass.newInstance();
            if (resultSet.next()) {
                for (Field field : classFields) {
                    field.setAccessible(true);
                    field.set(entity, resultSet.getObject(StringHelper.toSnakeCase(field.getName())));
                }
                return Optional.of(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public T update(T entity) {
        String query = getUpdateQuery(entity);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Optional<T> delete(ID id) {
        String query = getDeleteQuery(id);
        Optional<T> optionalEntity = getById(id);
        if (optionalEntity.isPresent()) {
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                return optionalEntity;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<T>> getAll() {
        List<T> entityList = new ArrayList<>();
        String query = getSelectAllQuery();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                T entity = entityClass.newInstance();
                for (Field field : classFields) {
                    field.setAccessible(true);
                    field.set(entity, resultSet.getObject(StringHelper.toSnakeCase(field.getName())));
                }
                entityList.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.of(entityList);
    }

    private String getSaveQuery(T entity) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append("(");
        StringBuilder fieldsNames = new StringBuilder();
        for (Field field : classFields) {
            fieldsNames.append(StringHelper.toSnakeCase(field.getName()))
                    .append(", ");
        }
        fieldsNames = new StringBuilder(fieldsNames.substring(0, fieldsNames.length() - 2));
        query.append(fieldsNames)
                .append(") VALUES (");
        for (Field field : classFields) {
            field.setAccessible(true);
            query.append(StringHelper.getFieldValue(field, entity))
                    .append(", ");
        }
        query = StringHelper.cutString(query);
        query.append(");");
        return query.toString();
    }

    private String getSelectQuery(ID id) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(" WHERE id = ")
                .append(id)
                .append(';');
        return query.toString();
    }

    private String getUpdateQuery(T entity) {
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(" SET ");
        StringBuilder changes = new StringBuilder();
        String condition = "";
        for (Field field : classFields) {
            try {
                if (field.getName().equals("id")) {
                    condition = " WHERE id = " + field.get(entity);
                } else {
                    changes.append(StringHelper.toSnakeCase(field.getName()))
                            .append(" = ");
                    changes.append(StringHelper.getFieldValue(field, entity))
                            .append(", ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        changes = StringHelper.cutString(query);
        query.append(changes)
                .append(condition)
                .append(';');
        return query.toString();
    }

    private String getDeleteQuery(ID id) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(" WHERE id = ")
                .append(id)
                .append(';');
        return query.toString();
    }

    private String getSelectAllQuery() {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(';');
        return query.toString();
    }
}
