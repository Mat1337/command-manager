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
        print("handle()V");
        return true;
    }

    @Default
    boolean handle(int number) {
        print("handle(I) > num: {}", number);
        return true;
    }

    @Argument("test")
    boolean test() {
        print("test()V");
        return true;
    }

    @Argument("test")
    boolean test(String name, float l) {
        print("test(Ljava/lang/String;L)V > name: {}, f: {}", name, l);
        return true;
    }

    @Argument("test")
    boolean test(String name, int l) {
        print("test(Ljava/lang/String;L)V > name: {}, f: {}", name, l);
        return true;
    }

}
