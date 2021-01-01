package me.mat.command.exceptions;


import me.mat.command.data.ArgumentData;
import me.mat.command.handler.argument.processor.ArgumentProcessor;

public class InvalidProcessorTypeException extends Exception {

    public InvalidProcessorTypeException(ArgumentProcessor<?> argumentProcessor, Class<?> targetClass) {
        super("\"" + argumentProcessor.getClass() + "\" must have a " + ArgumentData.class.getName() + " type (found: " + targetClass.getName() + ")");
    }
}