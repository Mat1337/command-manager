package me.mat.command.handler.argument;

import lombok.Getter;
import me.mat.command.CommandManager;
import me.mat.command.data.ArgumentData;
import me.mat.command.data.GenericArgumentData;
import me.mat.command.data.NumberArgumentData;
import me.mat.command.data.StringArgumentData;
import me.mat.command.handler.argument.processor.ArgumentProcessor;
import me.mat.command.util.CustomMapContainer;

@Getter
public class ArgumentHandler extends CustomMapContainer<Class<? extends ArgumentData>, ArgumentProcessor> {

    private final CommandManager commandManager;
    private final GenericArgumentHandler genericArgumentHandler;

    public ArgumentHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.genericArgumentHandler = new GenericArgumentHandler(commandManager);

        put(StringArgumentData.class, new ArgumentProcessor<StringArgumentData>() {
            @Override
            public Object process(StringArgumentData data, String argument, Class<?> parameter) {
                if (parameter != String.class) {
                    invalidArgumentType(parameter.getName(), argument);
                    return null;
                }

                int length = argument.length();
                int minLength = data.getMinLength();
                int maxLength = data.getMaxLength();

                if (length > maxLength) {
                    printWarning(commandManager.getConfig().stringBiggerThenExpected, argument, maxLength);
                } else if (length < minLength) {
                    printWarning(commandManager.getConfig().stringSmallerThenExpected, argument, minLength);
                } else {
                    return argument;
                }

                return null;
            }
        });

        put(NumberArgumentData.class, new ArgumentProcessor<NumberArgumentData>() {
            @Override
            public Object process(NumberArgumentData data, String argument, Class<?> parameter) {
                try {
                    double amount = Double.parseDouble(argument);

                    if (inRange(data, amount)) {
                        if (parameter == byte.class) {
                            return Byte.parseByte(argument);
                        } else if (parameter == short.class) {
                            return Short.parseShort(argument);
                        } else if (parameter == int.class) {
                            return Integer.parseInt(argument);
                        } else if (parameter == float.class) {
                            return Float.parseFloat(argument);
                        } else if (parameter == double.class) {
                            return amount;
                        } else if (parameter == long.class) {
                            return Long.parseLong(argument);
                        }
                    }

                    return null;
                } catch (NumberFormatException e) {
                    invalidArgumentType(parameter.getName(), argument);
                    return null;
                }
            }

            private boolean inRange(NumberArgumentData data, double amount) {
                double max = data.getMax();
                double min = data.getMin();

                if (amount > max) {
                    printWarning(commandManager.getConfig().numberBiggerThenExpected, amount, max);
                } else if (amount < min) {
                    printWarning(commandManager.getConfig().numberSmallerThenExpected, amount, min);
                } else {
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void put(Class<? extends ArgumentData> key, ArgumentProcessor value) {
        value.setArgumentHandler(this);

        super.put(key, value);
    }

    public Object process(String argument, ArgumentData argumentData, Class<?> parameter) {
        Class<? extends ArgumentData> aClass = argumentData.getClass();
        if (argumentData instanceof GenericArgumentData) {
            return genericArgumentHandler.process(argument, parameter);
        } else {
            if (!hasKey(aClass)) {
                return null;
            }

            return get(aClass).process(argumentData, argument, parameter);
        }
    }

}
