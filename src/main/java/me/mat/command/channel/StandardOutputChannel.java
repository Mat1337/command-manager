package me.mat.command.channel;

public class StandardOutputChannel implements MessageOutputChannel {

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public void printWarning(String warning) {
        System.out.println(warning);
    }

    @Override
    public void printError(String error) {
        System.err.println(error);
    }

}
