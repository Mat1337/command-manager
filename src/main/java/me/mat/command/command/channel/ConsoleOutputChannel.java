package me.mat.command.command.channel;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */
public class ConsoleOutputChannel implements OutputChannel {

    @Override
    public void print(String msg, Object... params) {
        System.out.println(parse(msg, params));
    }

    @Override
    public void warn(String msg, Object... params) {
        print("[WARN]: " + msg, params);
    }

    @Override
    public void error(String msg, Object... params) {
        print("[ERR]: " + msg, params);
    }

}
