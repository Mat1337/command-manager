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

    public abstract boolean parse(Class<?> aClass, String argument);

}
