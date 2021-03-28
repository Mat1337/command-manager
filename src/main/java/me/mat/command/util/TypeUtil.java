package me.mat.command.util;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */
public class TypeUtil {

    private static final String NUMBER_REGEX = "^-?\\d*(\\.\\d+)?$";

    private TypeUtil() {
    }

    public static boolean isNumber(String str) {
        return str.matches(NUMBER_REGEX);
    }

    public static boolean isDecimalNumber(String str) {
        return isNumber(str) && str.contains(".");
    }

    public static boolean isNumber(Class<?> aClass) {
        return aClass.equals(byte.class) || aClass.equals(short.class)
                || aClass.equals(int.class) || aClass.equals(double.class)
                || aClass.equals(float.class) || aClass.equals(long.class);
    }

}
