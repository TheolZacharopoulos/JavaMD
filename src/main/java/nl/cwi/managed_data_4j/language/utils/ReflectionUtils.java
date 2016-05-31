package nl.cwi.managed_data_4j.language.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Comparator;

public class ReflectionUtils {

    public static Object getValueFromFieldSafe(Object instance, String fieldName, Class<?> fieldType) {
        try {
            return ReflectionUtils.getValueFromField(instance, fieldName, fieldType);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getValueFromField(Object instance, String fieldName, Class<?> fieldType) throws Throwable {

        Method method;
        try {
            // try with a method without params
            method = instance.getClass().getMethod(fieldName);
        } catch (NoSuchMethodException e) {

            if (fieldType.isArray()) {
                // if it does not work, get the params.
                method = instance.getClass().getMethod(fieldName, fieldType);
            } else {
                // if it does not work, get the params.
                Class<?> parameterType = Array.newInstance(fieldType, 0).getClass();
                method = instance.getClass().getMethod(fieldName, parameterType);
            }
        }

        // needs to be accessible in order to invoke it
        method.setAccessible(true);

        // in case the method needs parameters, we need to construct the parameter type
        if (method.getParameters().length > 0) {

            // We need the following in order to invoke the method with this kind of parameter
            // Get the parameter type, and get the first one
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Class<?> firstParameterType = parameterTypes[0];

            // In case of vargs, make it from array to single type
            // after, create an empty array of that type, this way we can invoke methods that need
            // empty vargs as parameters.
            Object emptyArrayOfFirstParameterType;
            if (firstParameterType.isArray()) {
                emptyArrayOfFirstParameterType = Array.newInstance(firstParameterType.getComponentType(), 0);
            } else {
                emptyArrayOfFirstParameterType = Array.newInstance(firstParameterType, 0);
            }

            return method.invoke(instance, emptyArrayOfFirstParameterType);
        } else {
            return method.invoke(instance);
        }
    }

    /**
     * Helper method, Compare based on the field's name
     */
    public static Comparator<Object> fieldNameComparison() {
        return (Object o1, Object o2) -> {
            String o1Name = (String) getValueFromFieldSafe(o1, "name", String.class);
            String o2Name = (String) getValueFromFieldSafe(o2, "name", String.class);

            if (o1Name != null && o2Name != null) {
                return o1Name.compareTo(o2Name);
            }
            return 0;
        };
    }

    public static void setValueToFieldSafe(Object instance, String fieldName, Class<?> typeClass, Object value) {
        try {
            ReflectionUtils.setValueToField(instance, fieldName, typeClass, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets a value to a field reflectively
     * @param instance the instance of the object to set the field to
     * @param fieldName the field name of the field
     * @param typeClass the class of the field
     * @param value the value to set
     * @throws Throwable in case of invocation error.
     */
    public static void setValueToField(Object instance, String fieldName, Class<?> typeClass, Object value)  throws Throwable {
        Method method;

        // Get params method params.
        Class<?> parameterType = Array.newInstance(typeClass, 0).getClass();
        method = instance.getClass().getMethod(fieldName, parameterType);

        // needs to be accessible in order to invoke it
        method.setAccessible(true);

        // We need the following in order to invoke the method with this kind of parameter
        // Get the parameter type, and get the first one
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<?> firstParameterType = parameterTypes[0];

        // In case of vargs, make it from array to single type
        // after, create an empty array of that type, this way we can invoke methods that need
        // empty vargs as parameters.
        Object arrayOfParameterType;
        if (firstParameterType.isArray()) {
            arrayOfParameterType = Array.newInstance(firstParameterType.getComponentType(), 1);
        } else {
            arrayOfParameterType = Array.newInstance(firstParameterType, 1);
        }

        // push the value to the parameter type array.
        Array.set(arrayOfParameterType, 0, value);

        // invoke the method to set the field value
        method.invoke(instance, arrayOfParameterType);
    }
}
