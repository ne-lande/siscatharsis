package ru.mtuci.siscatharsis.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ObjectUtils {

        public static <T> Map<String, Object> image(T before, T after) {
                Class<?> clazz = before.getClass();

                Map<String, Object> capturedFields = new HashMap<>();
                for (Field field : clazz.getDeclaredFields()) {
                        field.setAccessible(true);
                        try {
                                Object beforeValue = field.get(before);
                                Object afterValue = field.get(after);

                                if (!Objects.equals(beforeValue, afterValue)) {
                                        capturedFields.put(field.getName(), afterValue); // or beforeValue, or both
                                }
                        } catch (IllegalAccessException e) {
                                continue;
                        }
                }
                return capturedFields;
        }

        public static <T> Map<String, Object> merge(T original, T updates) throws IllegalAccessException {
                Class<?> entityClass = original.getClass();
                Class<?> updatesClass = updates.getClass();

                Map<String, Object> updatedFields = new HashMap<>();

                for (Field field : updatesClass.getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(updates);

                        if (value != null) {
                                try {
                                        String fieldName = field.getName();
                                        Field entityField = entityClass.getDeclaredField(fieldName);
                                        entityField.setAccessible(true);

                                        if (!value.equals(entityField.get(original))) {
                                                entityField.set(original, value);
                                                updatedFields.put(fieldName, value);
                                        }
                                } catch (NoSuchFieldException | IllegalAccessException e) {
                                        continue;
                                }
                        }
                }
                return updatedFields;
        }
}

