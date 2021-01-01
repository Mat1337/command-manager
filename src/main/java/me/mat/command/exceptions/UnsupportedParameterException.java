package me.mat.command.exceptions;

public class UnsupportedParameterException extends Exception {

    public UnsupportedParameterException(Class<?> parameter) {
        super(parameter.getName() + " is not a supported parameter");
    }
}
