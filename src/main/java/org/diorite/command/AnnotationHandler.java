package org.diorite.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import org.diorite.commons.reflections.MethodInvoker;

public interface AnnotationHandler<T extends Annotation>
{
    void apply(ArgumentBuilder<?> argumentBuilder, T annotation, Parameter param, MethodInvoker method);
}
