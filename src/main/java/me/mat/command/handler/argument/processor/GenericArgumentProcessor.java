package me.mat.command.handler.argument.processor;

import lombok.Setter;
import me.mat.command.handler.argument.GenericArgumentHandler;

@Setter
public abstract class GenericArgumentProcessor {

    private GenericArgumentHandler genericArgumentHandler;

    public abstract Object process(String argument, Class<?> parameter);

    public void print(String message, Object... params) {
        genericArgumentHandler.getCommandManager().print(message, params);
    }

    public void printWarning(String warning, Object... params) {
        genericArgumentHandler.getCommandManager().printWarning(warning, params);
    }

    public void printError(String error, Object... params) {
        genericArgumentHandler.getCommandManager().printError(error, params);
    }

    public void invalidArgumentType(Object... params) {
        printError("Invalid argument type (Expected: %s, Received: %s)", params);
    }

}