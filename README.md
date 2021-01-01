# command-manager
This is a CommandManager that I use in all my projects

# usage

- Command (This is how a basic command looks with no custom arguments)
```java
public class HelpCommand extends Command {

    public HelpCommand(String label, String usage, String description, String... aliases) {
        super(label, usage, description, aliases);
    }

    @Default
    boolean handle() {
        print("Commands: ");
        commandManager.forEach(command -> print(command.getLabel() + ": " + command.getDescription()));
        return true;
    }

    @Default
    boolean handle(Command command) {
        print("Show information about \"%s\" command: ", command.getLabel());
        print("Label: %s", command.getLabel());
        print("Usage: %s", command.getUsage());
        print("Description: %s", command.getDescription());
        print("Aliases: %s", Arrays.asList(command.getAliases()).toString());
        return true;
    }

}
```
The return value of the method is boolean of true/false depending of the execution was succesuful
# arguments
- If you wish for you command to have custom arguments it can be done like this

```java
    @Argument("set")
    boolean set(float value) {
        return true;
    }
```
# generic type
- Adding generic types goes something like this

```java
    private static final CommandManager COMMAND_MANAGER = new CommandManager();
    
    public void init(){
        COMMAND_MANAGER.addType(boolean.class, new GenericArgumentProcessor() {
            @Override
            public Object process(String argument, Class<?> parameter) {
                    if (argument.equalsIgnoreCase("true")
                        || argument.equalsIgnoreCase("false")) {
                        return Boolean.parseBoolean(argument);
                    } else {
                        invalidArgumentType(parameter, argument);
                    }
                    return null;
            }
        });
    }
```
A generic type is a type that does not have an associated annotation with itself

# custom types
- Adding a custom type goes something like this
```java
    private static final CommandManager COMMAND_MANAGER = new CommandManager();

    public void init(){
        try {
        commandManager.addType(float.class, new TypeProcessor() {
        @Override
        public ArgumentData process(Class<?> parameter, Annotation annotation) {
            return null;
        }
        }, new ArgumentProcessor<ArgumentData>() {
            @Override
            public Object process(ArgumentData data, String argument, Class<?> parameter) {
                return null;
            }
            });
        } catch (InvalidProcessorTypeException | InvalidTypeException e) {
            e.printStackTrace();
        }
    }
```