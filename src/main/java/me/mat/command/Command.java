package me.mat.command;

import lombok.Getter;
import lombok.Setter;
import me.mat.command.data.ArgumentData;
import me.mat.command.exceptions.UnsupportedParameterException;
import me.mat.command.manifest.Default;
import me.mat.command.util.MapContainer;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class Command extends MapContainer<String, List<ArgumentData>> {

    public static final String DEFAULT_NO_ARGS_KEY = "##default##no##args";
    public static final String DEFAULT_ARGS_KEY = "##default##args";

    private final Map<String, Method> lookUp;
    private final String label;
    private final String usage;
    private final String description;
    private final String[] aliases;

    @Setter
    protected CommandManager commandManager;

    public Command(String label, String usage, String description, String... aliases) {
        this.lookUp = new HashMap<>();
        this.label = label;
        this.usage = usage;
        this.description = description;
        this.aliases = aliases;
    }

    public Command(String label, String description, String... aliases) {
        this(label, "", description, aliases);
    }

    public void findDefaults() {
        Arrays.stream(getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Default.class))
                .filter(method -> method.getReturnType() == boolean.class)
                .forEach(method -> {
                    String key = method.getParameterTypes().length > 0 ? DEFAULT_ARGS_KEY : DEFAULT_NO_ARGS_KEY;

                    putIfAbsent(key, new ArrayList<>());

                    lookUp.put(key, method);

                    method.setAccessible(true);

                    try {
                        commandManager.generateArgumentData(this, key, method);
                    } catch (UnsupportedParameterException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void print(String warning, Object... params) {
        commandManager.print(String.format(warning, params));
    }

    public void printWarning(String warning, Object... params) {
        commandManager.printWarning(String.format(warning, params));
    }

    public void printError(String error, Object... params) {
        commandManager.printError(String.format(error, params));
    }

    public boolean hasOnlyDefaults() {
        int size = lookUp.size();

        if (lookUp.containsKey(Command.DEFAULT_ARGS_KEY)
                && lookUp.containsKey(Command.DEFAULT_NO_ARGS_KEY)
                && size == 2) {
            return true;
        } else if (lookUp.containsKey(Command.DEFAULT_ARGS_KEY)
                && !lookUp.containsKey(Command.DEFAULT_NO_ARGS_KEY)
                && size == 1) {
            return true;
        } else if (!lookUp.containsKey(Command.DEFAULT_ARGS_KEY)
                && lookUp.containsKey(Command.DEFAULT_NO_ARGS_KEY)
                && size == 1) {
            return true;
        }

        return false;
    }

    public boolean isArgument(String label) {
        return lookUp.containsKey(label);
    }

    public boolean isLabel(String label) {
        return this.label.equalsIgnoreCase(label) || Arrays.stream(aliases).filter(alias -> alias.equalsIgnoreCase(label)).findFirst().orElse(null) != null;
    }

}