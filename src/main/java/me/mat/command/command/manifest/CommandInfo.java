package me.mat.command.command.manifest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {

    String label();

    String usage() default "";

    String description();

    String[] aliases() default {};

}
