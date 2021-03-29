package me.mat.command.command.parser;

import lombok.Getter;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */

@Getter
public abstract class Parser {

    private Object object;

    protected boolean success(Object object) {
        this.object = object;
        return true;
    }

    protected boolean error() {
        return false;
    }

    public abstract boolean parse(Class<?> aClass, String argument);

}
