package me.mat.command.data;

import lombok.Getter;

@Getter
public class StringArgumentData extends ArgumentData {

    private final int minLength;
    private final int maxLength;

    public StringArgumentData(Class<?> parameter, int minLength, int maxLength) {
        super(parameter);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public StringArgumentData(Class<?> parameter) {
        this(parameter, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

}
