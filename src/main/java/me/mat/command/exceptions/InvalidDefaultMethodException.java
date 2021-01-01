package me.mat.command.exceptions;

public class InvalidDefaultMethodException extends Exception {

    public InvalidDefaultMethodException(Class<?> aClass) {
        super(aClass.getName() + " has no methods that are annotated with @Default");
    }
}
