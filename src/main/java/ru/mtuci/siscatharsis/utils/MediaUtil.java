package ru.mtuci.siscatharsis.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: form the mime types for multipart/mixed
public class MediaUtil {
        public static byte[] longToByte(long val) {
                byte[] bytes = new byte[Long.BYTES];

                for (int i = 0; i < Long.BYTES; i++) {
                        bytes[i] = ((byte) (val >>> (Byte.SIZE * i)));
                }

                return bytes;
        }

        public static byte[] intToByte(int val) {
                byte[] bytes = new byte[Integer.BYTES];

                for (int i = 0; i < Integer.BYTES; i++) {
                        bytes[i] = ((byte) (val >>> (Byte.SIZE * i)));
                }

                return bytes;
        }

        // memcpy: remember me?
        public static byte[] uuidToByte(UUID val) {
                byte[] most = longToByte(val.getMostSignificantBits());
                byte[] least = longToByte(val.getLeastSignificantBits());

                byte[] bytes = new byte[most.length + least.length];
                System.arraycopy(most, 0, bytes, 0, most.length);
                System.arraycopy(least, 0, bytes, most.length, least.length);

                return bytes;
        }

        public static byte[] stringToByte(String val) {
                return val.getBytes();
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
                                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                                }
                        }
                }
                return updatedFields;
        }
}
