package com.alexstrog.utill;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;

public class StringHelper {

    private static final Logger logger = Logger.getLogger(StringHelper.class);

    public static String toSnakeCase(String camelCaseString) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";

        return camelCaseString
                .replaceAll(regex, replacement)
                .toLowerCase();
    }

    public static String getFieldValue(Field field, Object object) {
        String value = "";
        try {
            if (field.getName().equals("id")) {
                value = "NULL";
            } else if (field.getType().isPrimitive()) {
                value = field.get(object).toString();
            } else {
                value = '\'' + field.get(object).toString() + '\'';
            }
        } catch (IllegalAccessException e) {
            logger.error("Can't get entity's value", e);
        }
        return value;
    }

    public static StringBuilder cutString(StringBuilder stringBuilder) {
        return new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 2));
    }
}
