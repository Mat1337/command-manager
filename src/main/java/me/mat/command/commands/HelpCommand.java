package me.mat.command.commands;

import me.mat.command.Command;
import me.mat.command.manifest.Default;

import java.util.Arrays;

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