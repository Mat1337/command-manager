package me.mat.command.command;

import me.mat.command.command.exception.CommandCreationException;
import me.mat.command.command.manifest.Argument;
import me.mat.command.command.manifest.CommandInfo;
import me.mat.command.command.manifest.Default;
import me.mat.command.command.parser.TypeParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */
public class Command {

    public static final String DEFAULT_KEY = "@DEFAULT_METHOD@";

    private final CommandInfo commandInfo;
    private final List<CommandArgument> commandArguments;

    public Command() {
        // define the command info
        CommandInfo commandInfo = null;
        try {

            // try to get the command info from the class
            commandInfo = getCommandInfo();
        } catch (CommandCreationException e) {

            // if it fails print the stack trace
            e.printStackTrace();
        }

        // else assign the command info to the correct field
        this.commandInfo = commandInfo;

        // define the list that will contain all the arguments for the command
        this.commandArguments = new ArrayList<>();

        // load all the command arguments
        this.loadArguments();
    }

    /**
     * Invokes the method with the given parameters
     *
     * @param commandArgument argument that will be executed
     * @param args            arguments of the call
     * @param typeParser      type parser used for parsing the arguments
     *
     * @return true/false depending if the the method prints usage or not
     */

    public boolean invoke(CommandArgument commandArgument, String[] args, TypeParser typeParser) {
        try {
            return (boolean) commandArgument.getMethod().invoke(
                    this,
                    typeParser.parse(commandArgument.getArgumentTypes(), args)
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Finds the correct argument
     * based on the given parameters
     *
     * @param label      name of the argument
     * @param args       arguments to test against
     * @param typeParser type parser used for parsing the arguments
     *
     * @return {@link CommandArgument}
     */

    public CommandArgument find(String label, String[] args, TypeParser typeParser) {

        if (args.length == 0) {
            for (CommandArgument commandArgument : commandArguments) {
                if (commandArgument.getLabel().equalsIgnoreCase(DEFAULT_KEY)) {
                    Class<?>[] types = commandArgument.getArgumentTypes();
                    if (types.length == 1
                            && label != null && !label.isEmpty()
                            && typeParser.match(types, new String[]{label})) {
                        return commandArgument;
                    } else if (commandArgument.getArgumentTypes().length == 0 && (label == null || label.isEmpty())) {
                        return commandArgument;
                    }
                }
            }
        }

        for (CommandArgument ca : commandArguments) {
            String caLabel = ca.getLabel();
            Class<?>[] caTypes = ca.getArgumentTypes();

            if (caLabel != null && caLabel.equalsIgnoreCase(label) && caTypes.length == args.length && typeParser.match(caTypes, args)) {
                return ca;
            }
        }
        return null;
    }

    /**
     * Checks if the label is matching
     * this command
     *
     * @param label that you want to check with
     *
     * @return true/false depending if its matching or not
     */

    public boolean is(String label) {
        return getLabel().equalsIgnoreCase(label)
                || Stream.of(getAliases()).filter(s -> s.equalsIgnoreCase(label)).findAny().orElse(null) != null;
    }

    /**
     * Loads all the command arguments
     */

    private void loadArguments() {
        // loop through all the methods with default annotation and add them
        filter(Default.class).forEach(this::build);

        // loop through all the methods with the argument annotation and add them
        filter(Argument.class).forEach(this::build);
    }

    /**
     * Builds the argument based
     * on the provided method
     *
     * @param method that you want to build the argument for
     */

    private void build(Method method) {
        // define the label
        String label = null;

        // check if the method has the argument annotation
        if (method.isAnnotationPresent(Argument.class)) {

            // if it does set the label to the argument name
            label = method.getAnnotation(Argument.class).value();
        } else if (method.isAnnotationPresent(Default.class)) {

            // if the method has the default annotation set it to the default key
            label = DEFAULT_KEY;
        }

        // define the parameter types array
        Class<?>[] parameterTypes = new Class<?>[0];

        // check if the methods parameter types array is not empty
        if (method.getParameterTypes().length > 0) {

            // if its not update the array with the parameter types
            parameterTypes = method.getParameterTypes();
        }

        // if the method is not accessible
        if (!method.isAccessible()) {

            // set it accessible
            method.setAccessible(true);
        }

        // create the command argument
        CommandArgument commandArgument =
                new CommandArgument(method, label, parameterTypes);

        // add the command argument
        commandArguments.add(commandArgument);
    }

    /**
     * Loops through all the methods in the class
     * and filters them with valid methods that
     * contain the given annotation
     *
     * @param annotation {@link Annotation}
     *
     * @return {@link Stream<Method>}
     */

    private Stream<Method> filter(Class<? extends Annotation> annotation) {
        return Stream.of(getClass().getDeclaredMethods())
                .filter(method -> method.getReturnType().equals(boolean.class))
                .filter(method -> method.isAnnotationPresent(annotation));
    }

    /**
     * Returns the label
     * of the command
     *
     * @return {@link String}
     */

    public String getLabel() {
        return commandInfo.label();
    }

    /**
     * Returns the usage
     * of the command
     *
     * @return {@link String}
     */

    public String getUsage() {
        return commandInfo.usage();
    }

    /**
     * Returns the description
     * of the command
     *
     * @return {@link String}
     */

    public String getDescription() {
        return commandInfo.description();
    }

    /**
     * Returns an array of
     * aliases for the command
     *
     * @return {@link String}
     */

    public String[] getAliases() {
        return commandInfo.aliases();
    }

    /**
     * Gets the command info for
     * the current class
     *
     * @return {@link CommandInfo}
     */

    private CommandInfo getCommandInfo() throws CommandCreationException {
        // get the current class
        Class<?> aClass = getClass();

        // check that the class has a CommandInfo annotation
        if (aClass.isAnnotationPresent(CommandInfo.class)) {

            // if so return the annotation
            return aClass.getAnnotation(CommandInfo.class);
        }

        // else throw a command creation exception
        throw new CommandCreationException(aClass.getName() + " does not have a " + CommandInfo.class.getName());
    }

}
