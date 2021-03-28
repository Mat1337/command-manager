package me.mat.command.test;

import me.mat.command.CommandManager;
import me.mat.command.command.channel.ConsoleOutputChannel;
import me.mat.command.command.exception.CommandCreationException;
import me.mat.command.test.command.TestCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */
public class Test {

    private final CommandManager commandManager
            = new CommandManager(new ConsoleOutputChannel());

    private Test() {
        // add all the test commands
        try {
            commandManager.register(TestCommand.class);
        } catch (CommandCreationException e) {
            e.printStackTrace();
        }

        // run the test application
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
