package me.mat.command;

import me.mat.command.commands.TestCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    private final CommandManager commandManager;

    private Test() {
        commandManager = new CommandManager(".");

        commandManager.addCommand(
                new TestCommand("test", "test [arg1] [arg2]", "Test command", "t")
        );

        run();
    }

    public void run() {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            commandManager.parse(reader.readLine());
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Test();
    }

}
