package me.mat.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */

@Getter
@AllArgsConstructor
public class CommandArgument {

    private final Method method;
    private final String label;
    private final Class<?>[] argumentTypes;

}
