package me.mat.command.channel;

public interface MessageOutputChannel {

    void print(String message);

    void printWarning(String warning);

    void printError(String error);

}
