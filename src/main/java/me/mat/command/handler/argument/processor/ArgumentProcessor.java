package me.mat.command.handler.argument.processor;

import lombok.Setter;
import me.mat.command.data.ArgumentData;
import me.mat.command.handler.argument.ArgumentHandler;

@Setter
public abstract class ArgumentProcessor<T extends ArgumentData> {

    private ArgumentHandler argumentHandler;

    public abstract Object process(T data, String argument, Class<?> parameter);

    public void print(String message, Object... params) {
        argumentHandler.getCommandManager().print(message, params);
    }

    public void printWarning(String warning, Object... params) {
        argumentHandler.getCommandManager().printWarning(warning, params);
    }

    public void printError(String error, Object... params) {
        argumentHandler.getCommandManager().printError(error, params);
    }

    public void invalidArgumentType(Object... params) {
        printError("Invalid argument type (Expected: %s, Received: %s)", params);
    }

}