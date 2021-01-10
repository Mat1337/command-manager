package me.mat.command.commands;

import me.mat.command.Command;
import me.mat.command.manifest.Argument;
import me.mat.command.manifest.Default;

public class TestCommand extends Command {

    public TestCommand(String label, String usage, String description, String... aliases) {
        super(label, usage, description, aliases);
    }

    @Default
    boolean handle() {
        return false;
    }

    @Argument("test")
    boolean test(String one) {
        print("TestValue: %s", one);
        return true;
    }

    @Argument("test")
    boolean test(String one, String two) {
        print("TestValue#1: %s, TestValue#2: %s", one, two);
        return true;
    }

}
