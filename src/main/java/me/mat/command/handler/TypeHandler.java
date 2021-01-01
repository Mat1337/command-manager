package me.mat.command.handler;

import me.mat.command.data.ArgumentData;
import me.mat.command.data.GenericArgumentData;
import me.mat.command.data.NumberArgumentData;
import me.mat.command.data.StringArgumentData;
import me.mat.command.manifest.NumberArg;
import me.mat.command.manifest.StringArg;
import me.mat.command.util.CustomMapContainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class TypeHandler extends CustomMapContainer<Class<?>, TypeProcessor> {

    private static final Class<?>[] NUMBERS = {byte.class, short.class, int.class, float.class, double.class, long.class};

    public TypeHandler() {
        Arrays.stream(NUMBERS).forEach(aClass -> put(aClass, new TypeProcessor() {
            @Override
            public ArgumentData process(Class<?> parameter, Annotation annotation) {
                if (annotation instanceof NumberArg) {
                    NumberArg numberArg = (NumberArg) annotation;
                    return new NumberArgumentData(parameter, numberArg.min(), numberArg.max());
                }

                return new NumberArgumentData(parameter);
            }
        }));

        put(String.class, new TypeProcessor() {
            @Override
            public ArgumentData process(Class<?> parameter, Annotation annotation) {
                if (annotation instanceof StringArg) {
                    StringArg stringArg = (StringArg) annotation;
                    return new StringArgumentData(parameter, stringArg.minLength(), stringArg.maxLength());
                }

                return new StringArgumentData(parameter);
            }
        });
    }

    public ArgumentData process(Class<?> parameter, Annotation annotation) {
        if (!hasKey(parameter)) {
            return new GenericArgumentData(parameter);
        }

        return get(parameter).process(parameter, annotation);
    }

}