package me.mat.command.command.channel;

/**
 * Created for command-manager
 *
 * @author mat
 * @since 3/28/2021
 */
public interface OutputChannel {

    void print(String msg, Object... params);

    void warn(String msg, Object... params);

    void error(String msg, Object... params);

    default String parse(String msg, Object... params) {
        for (Object param : params) {
            msg = msg.replaceFirst("\\{}", param.toString());
        }

        return msg;
    }

}
