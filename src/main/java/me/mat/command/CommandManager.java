package me.mat.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mat.command.command.Command;
import me.mat.command.command.CommandArgument;
import me.mat.command.command.channel.OutputChannel;
import me.mat.command.command.exception.CommandCreationException;
import me.mat.command.command.parser.Parser;
import me.mat.command.command.parser.TypeParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */

@AllArgsConstructor
public class CommandManager {

    @Getter
    private final OutputChannel outputChannel;

    private final Set<Command> commands;
    private final TypeParser typeParser;

    public CommandManager(OutputChannel outputChannel) {
        this(outputChannel, new HashSet<>(), new TypeParser());
    }

    /**
     * Add a new type parser
     *
     * @param parser that you want to add
     */

    public void addParser(Class<?> type, Parser parser) {
        typeParser.addParser(type, parser);
    }

    /**
     * Registers the command into
     * the command manager
     *
     * @param aClass {@link Command}
     *
     * @throws CommandCreationException if command creation fails it will throw the exception
     */

    public void register(Class<? extends Command> aClass) throws CommandCreationException {
        try {
            // get the constructor
            Constructor<?> constructor = aClass.getConstructor();

            // check that the constructor has no parameters
            if (constructor.getParameterCount() == 0) {

                // if the constructor has private access
                if (!constructor.isAccessible()) {

                    // update its access to public
                    constructor.setAccessible(true);
                }

                try {

                    // create the new command instance
                    Object obj = constructor.newInstance();

                    // if the create object is an instance of the command class
                    if (obj instanceof Command) {

                        // cast the object to the command
                        Command command = (Command) obj;

                        // update its command manager
                        command.commandManager = this;

                        // add the command to the set
                        commands.add(command);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {

                // throw a new command creation exception
                throw new CommandCreationException(aClass.getName() + ".<init>() constructor can not have any parameters");
            }
        } catch (NoSuchMethodException e) {

            // throw a new command creation exception
            throw new CommandCreationException(aClass.getName() + ".<init>() constructor can not have any parameters");
        }
    }

    /**
     * Parses the message and executes
     * the target command
     *
     * @param message that you want to parse
     *
     * @return true/false depending if the message was parsed or not
     */

    public boolean parse(String message) {
        // split the message into arguments
        String[] args = message.split(" ");

        // if there is no arguments
        if (args.length == 0) {

            // return out of the method
            return false;
        }

        // try to find the command
        Command command = commands.stream().filter(c -> c.is(args[0])).findFirst().orElse(null);

        // if the command was not found
        if (command == null) {

            // alert the user that an invalid command was inputed
            outputChannel.warn("\"{}\" is not a valid command", args[0]);

            // return out of the method
            return true;
        }

        // define a new array of strings
        String[] arguments = new String[Math.max(0, args.length - 1)];

        // copy over the arguments
        System.arraycopy(args, 1, arguments, 0, arguments.length);

        if (arguments.length == 0) {

            // try to find the default method with no arguments
            CommandArgument commandArgument = command.find(null, new String[0], typeParser);

            // if the command argument was found
            if (commandArgument != null) {

                // if the invoke was successful
                if (command.invoke(commandArgument, null, typeParser)) {

                    // return out of the method
                    return true;
                }
            }

            // get the usage of the command
            String usage = command.getUsage();

            // if the usage is not empty
            if (!usage.isEmpty()) {

                // print the usage
                outputChannel.print(usage);
            }

            // return out of the command
            return true;
        }

        // define the final arguments
        String[] finalArgs = new String[Math.max(0, arguments.length - 1)];

        // copy over the arguments
        System.arraycopy(arguments, 1, finalArgs, 0, finalArgs.length);

        // find the command argument
        CommandArgument commandArgument = command.find(arguments[0], finalArgs, typeParser);

        // if the argument was not found
        if (commandArgument == null) {

            // get the usage of the command
            String usage = command.getUsage();

            // if the usage is not empty
            if (!usage.isEmpty()) {

                // print the usage
                outputChannel.print(usage);
            }

            // return out of the method
            return true;
        }

        if (finalArgs.length == 0 && commandArgument.getLabel().equalsIgnoreCase(Command.DEFAULT_KEY)) {
            finalArgs = new String[]{
                    arguments[0]
            };
        }

        // if the invoke was successful
        if (command.invoke(commandArgument, finalArgs, typeParser)) {

            // return out of the method
            return true;
        }

        // get the usage of the command
        String usage = command.getUsage();

        // if the usage is not empty
        if (!usage.isEmpty()) {

            // print the usage
            outputChannel.print(usage);
        }

        // return out of the method
        return true;
    }

}
