package me.mat.command.handler.argument;

import lombok.Getter;
import me.mat.command.Command;
import me.mat.command.CommandManager;
import me.mat.command.handler.argument.processor.GenericArgumentProcessor;
import me.mat.command.util.MapContainer;

@Getter
public class GenericArgumentHandler extends MapContainer<Class<?>, GenericArgumentProcessor> {

    private final CommandManager commandManager;

    public GenericArgumentHandler(CommandManager commandManager) {
        this.commandManager = commandManager;

        put(boolean.class, new GenericArgumentProcessor() {
            @Override
            public Object process(String argument, Class<?> parameter) {
                if (argument.equalsIgnoreCase("true")
                        || argument.equalsIgnoreCase("false")) {
                    return Boolean.parseBoolean(argument);
                } else {
                    invalidArgumentType(parameter, argument);
                }
                return null;
            }
        });

        put(Command.class, new GenericArgumentProcessor() {
            @Override
            public Object process(String argument, Class<?> parameter) {
                return commandManager.find(command -> command.isLabel(argument)).orElse(null);
            }
        });
    }

    @Override
    public void put(Class<?> key, GenericArgumentProcessor value) {
        value.setGenericArgumentHandler(this);

        super.put(key, value);
    }

    public Object process(String argument, Class<?> parameter) {
        if (!hasKey(parameter)) {
            return null;
        }
        return get(parameter).process(argument, parameter);
    }

}
