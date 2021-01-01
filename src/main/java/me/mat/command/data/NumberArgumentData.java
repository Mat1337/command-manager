package me.mat.command.data;

import lombok.Getter;

@Getter
public class NumberArgumentData extends ArgumentData {

    private double min;
    private double max;

    public NumberArgumentData(Class<?> parameter, float min, float max) {
        super(parameter);
        this.min = min;
        this.max = max;
    }

    public NumberArgumentData(Class<?> parameter) {
        this(parameter, 0, 0);

        if (parameter == byte.class) {
            min = Byte.MIN_VALUE;
            max = Byte.MAX_VALUE;
        } else if (parameter == short.class) {
            min = Short.MIN_VALUE;
            max = Short.MAX_VALUE;
        } else if (parameter == int.class) {
            min = Integer.MIN_VALUE;
            max = Integer.MAX_VALUE;
        } else if (parameter == float.class) {
            min = Float.MIN_VALUE;
            max = Float.MAX_VALUE;
        } else if (parameter == double.class) {
            min = Double.MIN_VALUE;
            max = Double.MAX_VALUE;
        } else if (parameter == long.class) {
            min = Long.MIN_VALUE;
            max = Long.MAX_VALUE;
        }
    }

}