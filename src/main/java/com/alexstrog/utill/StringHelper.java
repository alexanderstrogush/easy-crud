package com.alexstrog.utill;

import com.alexstrog.dao.implementation.CarDaoImpl;
import com.alexstrog.model.Car;
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

    public static class DaoTest {
        public static void main(String[] args) {
            CarDaoImpl abstractDao = new CarDaoImpl();
            Car car = new Car("Jaguar", "F", 1200);
            car = abstractDao.save(car);
            System.out.println(car);
            Car car1 = abstractDao.getById(2L).orElse(new Car("AUDI", "R8", 1000));
            System.out.println(car1);
        }
    }
}
