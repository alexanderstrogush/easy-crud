package com.alexstrog.service;

import com.alexstrog.utill.StringHelper;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;

public class QueryBuilder {

    private static Logger logger = Logger.getLogger(QueryBuilder.class);

    public static String getSaveQuery(Object entity) {
        Class entityClass = entity.getClass();
        Field[] classFields = entityClass.getDeclaredFields();
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(" (");
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

    public static String getSelectQuery(Class entityClass, Object id) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(" WHERE id = ")
                .append(id)
                .append(';');
        return query.toString();
    }

    public static String getUpdateQuery(Object entity) {
        Class entityClass = entity.getClass();
        Field[] classFields = entityClass.getDeclaredFields();
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
                            .append(" = ")
                            .append(StringHelper.getFieldValue(field, entity))
                            .append(", ");
                }
            } catch (IllegalAccessException e) {
                logger.error("Can't get entity's value", e);
            }
        }
        changes = StringHelper.cutString(query);
        query.append(changes)
                .append(condition)
                .append(';');
        return query.toString();
    }

    public static String getDeleteQuery(Class entityClass, Object id) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(" WHERE id = ")
                .append(id)
                .append(';');
        return query.toString();
    }

    public static String getSelectAllQuery(Class entityClass) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(StringHelper.toSnakeCase(entityClass.getSimpleName()))
                .append(';');
        return query.toString();
    }
}
