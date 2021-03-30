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
        usage = "test <test/test/set> [name]",
        aliases = {
                "t"
        })
public class TestCommand extends Command {

    @Default
    boolean handle() {
        getChannel().print("handle()V");
        return true;
    }

    @Default
    boolean handle(int number) {
        getChannel().print("handle(I)V");
        return true;
    }

    @Argument("test")
    boolean test() {
        getChannel().print("test()V");
        return true;
    }

    @Argument("test")
    boolean test(String name, float l) {
        getChannel().print("test(Ljava/lang/String;F)V");
        return true;
    }

    @Argument("test")
    boolean test(String name, int l) {
        getChannel().print("test(Ljava/lang/String;I)V");
        return true;
    }

    @Argument("set")
    boolean set(String name, boolean flag) {
        getChannel().print("set(Ljava/lang/String;Z)V");
        return true;
    }

}
