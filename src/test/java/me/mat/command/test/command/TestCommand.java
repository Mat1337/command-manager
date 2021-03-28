package me.mat.command.test.command;

import me.mat.command.command.Command;
import me.mat.command.command.manifest.Argument;
import me.mat.command.command.manifest.CommandInfo;
import me.mat.command.command.manifest.Default;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */

@CommandInfo(
        label = "test",
        description = "This is a test command",
        usage = "test <test/test> [name]",
        aliases = {
                "t"
        })
public class TestCommand extends Command {

    @Default
    boolean handle() {
        System.out.println("im fucking here");
        return true;
    }

    @Default
    boolean handle(int number) {
        System.out.println("num: " + number);
        return true;
    }

    @Argument("test")
    boolean test() {
        System.out.println("test()V");
        return true;
    }

    @Argument("test")
    boolean test(String name, int l) {
        System.out.println("test(Ljava/lang/String;L)V -> name: " + name + ", l: " + l);
        return true;
    }

}
