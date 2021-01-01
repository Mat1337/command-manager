package me.mat.command.handler;

import me.mat.command.data.ArgumentData;

import java.lang.annotation.Annotation;

public abstract class TypeProcessor {

    public abstract ArgumentData process(Class<?> parameter, Annotation annotation);

}
