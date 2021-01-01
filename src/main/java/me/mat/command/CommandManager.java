package me.mat.command;

import lombok.Setter;
import lombok.val;
import me.mat.command.channel.MessageOutputChannel;
import me.mat.command.channel.StandardOutputChannel;
import me.mat.command.commands.HelpCommand;
import me.mat.command.data.ArgumentData;
import me.mat.command.data.GenericArgumentData;
import me.mat.command.exceptions.*;
import me.mat.command.handler.TypeHandler;
import me.mat.command.handler.TypeProcessor;
import me.mat.command.handler.argument.ArgumentHandler;
import me.mat.command.handler.argument.processor.ArgumentProcessor;
import me.mat.command.handler.argument.processor.GenericArgumentProcessor;
import me.mat.command.manifest.Argument;
import me.mat.command.util.CustomContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends CustomContainer<Command> {

    private final ArgumentHandler argumentHandler;
    private final TypeHandler typeHandler;

    private final String prefix;

    @Setter
    private MessageOutputChannel messageOutputChannel;

    public CommandManager(MessageOutputChannel messageOutputChannel, String prefix) {
        this.messageOutputChannel = messageOutputChannel;
        this.prefix = prefix;

        this.argumentHandler = new ArgumentHandler(this);
        this.typeHandler = new TypeHandler();

        this.addCommand(new HelpCommand("help", "help [command]", "Shows you information about other commands"));
    }

    public CommandManager(MessageOutputChannel messageOutputChannel) {
        this(messageOutputChannel, null);
    }

    public CommandManager(String prefix) {
        this(new StandardOutputChannel(), prefix);
    }

    public CommandManager() {
        this(new StandardOutputChannel(), null);
    }

    /**
     * Add a custom type to the parser
     *
     * @param type              that you want to add
     * @param typeProcessor     that will be used to parse the type
     * @param argumentProcessor that will be used to parse the argument
     * @throws InvalidProcessorTypeException
     * @throws InvalidTypeException
     */

    public void addType(Class<?> type, TypeProcessor typeProcessor, ArgumentProcessor<?> argumentProcessor) throws InvalidProcessorTypeException, InvalidTypeException {
        if (!typeHandler.hasKey(type)) {
            typeHandler.put(type, typeProcessor);

            try {
                val targetClass = Class.forName(((ParameterizedType) argumentProcessor.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
                if (ArgumentData.class.isAssignableFrom(targetClass)) {
                    argumentHandler.put((Class<? extends ArgumentData>) targetClass, argumentProcessor);
                } else {
                    throw new InvalidProcessorTypeException(argumentProcessor, targetClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            throw new InvalidTypeException(type);
        }
    }

    /**
     * Adds a generic argument type parser
     *
     * @param type      that the processor will parse
     * @param processor and the processor that will be used
     * @throws InvalidTypeException
     */

    public void addType(Class<?> type, GenericArgumentProcessor processor) throws InvalidTypeException {
        val genericArgumentHandler = argumentHandler.getGenericArgumentHandler();
        if (!genericArgumentHandler.hasKey(type)) {
            genericArgumentHandler.put(type, processor);
        } else {
            throw new InvalidTypeException(type);
        }
    }

    /**
     * Parses a message into command arguments
     * and executes any commands matching the arguments
     *
     * @param message that you want to parse
     * @return true/false depending if the parsing was successful
     */

    public boolean parse(String message) {
        if (prefix != null) {
            if (message.toLowerCase().startsWith(prefix.toLowerCase())) {
                message = message.substring(prefix.length());
            } else {
                return false;
            }
        }

        val baseArgs = message.split(" ");
        if (baseArgs.length > 0) {
            val label = baseArgs[0];
            if (label.isEmpty()) {
                print("Please type %shelp for more information", prefix != null ? prefix : "");
                return true;
            }

            val search = find(command -> command.isLabel(label));
            if (!search.isPresent()) {
                printError("\"%s\" is not a valid command", label);
                return true;
            }

            search.ifPresent(command -> {
                val args = new String[baseArgs.length - 1];
                System.arraycopy(baseArgs, 1, args, 0, args.length);
                val argsLength = args.length;

                try {
                    if (!command.hasOnlyDefaults()) {
                        if (argsLength > 0) {
                            val argLabel = args[0];
                            if (command.isArgument(argLabel)) {
                                val subArgs = new String[args.length - 1];
                                System.arraycopy(args, 1, subArgs, 0, subArgs.length);

                                List<ArgumentData> data;
                                if (command.hasKey(argLabel)) {
                                    data = command.get(argLabel);
                                } else {
                                    data = new ArrayList<>();
                                }

                                if (!invoke(command, subArgs, data, command.getLookUp().get(argLabel))) {
                                    print(command.getUsage().isEmpty() ? command.getDescription() : command.getUsage());
                                }
                            } else {
                                invokeDefault(command, args);
                            }
                        } else {
                            invokeDefault(command, args);
                        }
                    } else {
                        invokeDefault(command, args);
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

        return true;
    }

    /**
     * Invokes the default method
     *
     * @param command that you want to invoke the default method from
     * @param args    that will be used
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    private void invokeDefault(Command command, String[] args) throws InvocationTargetException, IllegalAccessException {
        if (!invoke(command, args, getDefaultData(command, args.length), getDefaultMethod(command, args.length))) {
            print(command.getUsage().isEmpty() ? command.getDescription() : command.getUsage());
        }
    }

    /**
     * Invokes the method based on the parameters
     *
     * @param command that the method originated from
     * @param args    that will be used
     * @param data    a list of ArgumentData
     * @param method  that will be invoked
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    private boolean invoke(Command command, String[] args, List<ArgumentData> data, Method method) throws InvocationTargetException, IllegalAccessException {
        if (method != null && data != null) {
            if (!data.isEmpty()) {
                val parsedData = new ArrayList<>();
                if (processArgs(command, args, data, parsedData)) {
                    return (boolean) method.invoke(command, parsedData.toArray());
                } else {
                    return true;
                }
            } else {
                return (boolean) method.invoke(command);
            }
        }

        return false;
    }

    /**
     * Gets the default method for a command
     *
     * @param command    that you want to get the default method from
     * @param argsLength length of the arguments
     * @return a default method
     */

    private Method getDefaultMethod(Command command, int argsLength) {
        val lookUp = command.getLookUp();

        if (lookUp.containsKey(Command.DEFAULT_ARGS_KEY) && argsLength > 0) {
            return lookUp.get(Command.DEFAULT_ARGS_KEY);
        } else if (lookUp.containsKey(Command.DEFAULT_NO_ARGS_KEY)) {
            return lookUp.get(Command.DEFAULT_NO_ARGS_KEY);
        }

        return null;
    }

    /**
     * Gets the default argument data for a command
     *
     * @param command    that you want to get the default data from
     * @param argsLength length of the arguments
     * @return a list filled with default argument data
     */

    private List<ArgumentData> getDefaultData(Command command, int argsLength) {
        val lookUp = command.getLookUp();

        if (lookUp.containsKey(Command.DEFAULT_ARGS_KEY) && argsLength > 0) {
            return command.get(Command.DEFAULT_ARGS_KEY);
        } else if (lookUp.containsKey(Command.DEFAULT_NO_ARGS_KEY)) {
            return command.get(Command.DEFAULT_NO_ARGS_KEY);
        }

        return new ArrayList<>();
    }

    /**
     * Runs the arguments through the processor and checks if their
     * types are matching up with the generated ones
     *
     * @param args       of the message that will be checked against
     * @param data       a list of all the ArgumentData objects that were generated
     * @param parsedData a list of all the processed data ready to be sent to the method
     * @return true/false depending if the processing has finished with no errors
     */

    private boolean processArgs(Command command, String[] args, List<ArgumentData> data, List<Object> parsedData) {
        if (args.length != data.size()) {
            print(command.getUsage().isEmpty() ? command.getDescription() : command.getUsage());
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            val argumentData = data.get(i);
            val object = argumentHandler.process(args[i], argumentData, argumentData.getParameter());

            if (object != null) {
                parsedData.add(object);
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Adds a command to the system
     *
     * @param commands an array of commands that you want to add
     */

    public void addCommand(Command... commands) {
        Arrays.stream(commands).forEach(command -> {
            try {
                registerCommand(command);
            } catch (InvalidDefaultMethodException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Registers command into the factory and makes it usable
     *
     * @param command that you want to register
     */

    private void registerCommand(Command command) throws InvalidDefaultMethodException {
        command.setCommandManager(this);
        command.findDefaults();

        if (!command.hasKey(Command.DEFAULT_ARGS_KEY) && !command.hasKey(Command.DEFAULT_NO_ARGS_KEY)) {
            throw new InvalidDefaultMethodException(getClass());
        }

        Arrays.stream(command.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Argument.class))
                .filter(method -> method.getReturnType() == boolean.class)
                .forEach(method -> {
                    String argument = method.getAnnotation(Argument.class).value();
                    if (!argument.isEmpty()) {
                        command.getLookUp().put(argument, method);

                        try {
                            generateArgumentData(command, argument, method);
                        } catch (UnsupportedParameterException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            throw new InvalidArgAnnotationException("\"" + method.getName() + "\" has Argument annotation but name is empty");
                        } catch (InvalidArgAnnotationException e) {
                            e.printStackTrace();
                        }
                    }
                });

        add(command);
    }

    /**
     * Generates argument data for the given argument
     *
     * @param command  that the argument originated from
     * @param argument that you want to generate the data for
     * @param method   the method that the argument originated from
     * @throws UnsupportedParameterException
     */

    public void generateArgumentData(Command command, String argument, Method method) throws UnsupportedParameterException {
        val parameters = method.getParameterTypes();

        command.putIfAbsent(argument, new ArrayList<>());

        method.setAccessible(true);

        if (parameters.length > 0) {

            val data = command.get(argument);
            for (val aClass : parameters) {
                val argumentData = typeHandler.process(aClass, getAnnotation(aClass, method));
                if (argumentData != null) {
                    if (argumentData instanceof GenericArgumentData) {
                        val genericArgumentData = (GenericArgumentData) argumentData;
                        if (!argumentHandler.getGenericArgumentHandler().hasKey(aClass)) {
                            throw new UnsupportedParameterException(aClass);
                        }
                        data.add(genericArgumentData);
                    } else {
                        data.add(argumentData);
                    }
                }
            }

            if (!data.isEmpty()) {
                command.put(argument, data);
            }
        }
    }

    /**
     * Prints a formatted message into the output channel
     *
     * @param message the message you want to print out
     * @param params  parameters for the message
     */

    public void print(String message, Object... params) {
        messageOutputChannel.print(String.format(message, params));
    }

    /**
     * Prints a formatted message warning message into the output channel
     *
     * @param warning the message you want to print out
     * @param params  parameters for the message
     */

    public void printWarning(String warning, Object... params) {
        messageOutputChannel.printWarning(String.format(warning, params));
    }

    /**
     * Prints a formatted message error message into the output channel
     *
     * @param error  the message you want to print out
     * @param params parameters for the message
     */

    public void printError(String error, Object... params) {
        messageOutputChannel.printError(String.format(error, params));
    }

    /**
     * Get's an annotation from a parameter type in a given method
     *
     * @param parameter class that you want to target
     * @param method    that you want to search in
     * @return java.lang.annotation.Annotation that was found
     */

    private Annotation getAnnotation(Class<?> parameter, Method method) {
        val annotations = method.getParameterAnnotations();
        val index = Arrays.asList(method.getParameterTypes()).indexOf(parameter);

        if (annotations.length >= index) {
            val paramAnnotations = annotations[index];
            if (paramAnnotations.length > 0) {
                return paramAnnotations[0];
            } else {
                return null;
            }
        }
        return null;
    }

}
