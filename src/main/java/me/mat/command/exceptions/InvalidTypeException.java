package me.mat.command.exceptions;

public class InvalidTypeException extends Exception {

    public InvalidTypeException(Class<?> type) {
        super(type.getName() + " has already been assigned to a processor");
    }

}