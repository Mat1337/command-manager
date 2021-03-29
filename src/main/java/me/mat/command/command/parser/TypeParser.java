package me.mat.command.command.parser;

import me.mat.command.util.TypeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */

public class TypeParser {

    private final Map<Class<?>, Parser> parserMap = new HashMap<>();

    public void addParser(Class<?> type, Parser parser) {
        if (!parserMap.containsKey(type)) {
            parserMap.put(type, parser);
        }
    }

    public Object[] parse(Class<?>[] types, String[] args) {
        if ((types == null || args == null)
                || (types.length != args.length)) {
            return null;
        }

        Object[] objects = new Object[types.length];

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            String arg = args[i];

            if (type.equals(String.class)) {
                objects[i] = args[i];
                continue;
            }

            if (TypeUtil.isBoolean(type) && TypeUtil.isBoolean(arg)) {
                objects[i] = Boolean.parseBoolean(arg);
            } else if (TypeUtil.isNumber(type) && TypeUtil.isNumber(arg)
                    && canParseNumber(type, arg)) {
                objects[i] = parseNumber(type, arg);
            } else {
                if (parserMap.containsKey(type)) {
                    Parser parser;
                    if ((parser = parserMap.get(type)).parse(type, arg)) {
                        objects[i] = parser.getObject();
                    }
                }
            }
        }

        return objects;
    }

    public boolean match(Class<?>[] types, String[] args) {
        if (types.length != args.length) {
            return false;
        }

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            String arg = args[i];

            if (type.equals(String.class)
                    || (type.equals(boolean.class) && TypeUtil.isBoolean(arg))) {
                continue;
            }

            if (TypeUtil.isNumber(type) && TypeUtil.isNumber(arg)) {
                if (!canParseNumber(type, arg)) {
                    return false;
                }
            } else {
                if (parserMap.containsKey(type)) {
                    if (!parserMap.get(type).parse(type, arg)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if an argument can be parsed
     * to a number type
     *
     * @param type of number that you want
     * @param arg  that you want to parse
     *
     * @return true/false depending if it can be parsed or not
     */

    private boolean canParseNumber(Class<?> type, String arg) {
        try {
            return parseNumber(type, arg) != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses an argument into a number
     *
     * @param type of the argument that will be parsed to
     * @param arg  argument that will be parsed
     *
     * @return number
     *
     * @throws NumberFormatException thrown if parsing failed
     */

    private Object parseNumber(Class<?> type, String arg) throws NumberFormatException {
        if (type.equals(byte.class)) {
            return Byte.parseByte(arg);
        } else if (type.equals(short.class)) {
            return Short.parseShort(arg);
        } else if (type.equals(int.class)) {
            return Integer.parseInt(arg);
        } else if (type.equals(double.class)) {
            return Double.parseDouble(arg);
        } else if (type.equals(float.class)) {
            return Float.parseFloat(arg);
        } else if (type.equals(long.class)) {
            return Long.parseLong(arg);
        }
        return null;
    }

}
