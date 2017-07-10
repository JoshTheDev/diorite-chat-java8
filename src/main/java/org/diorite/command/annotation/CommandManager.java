package org.diorite.command.annotation;

import javax.annotation.Nullable;

import java.lang.annotation.Annotation;

import org.diorite.command.AnnotationHandler;
import org.diorite.command.ArgumentParsersRegistry;
import org.diorite.command.CommandHolder;
import org.diorite.command.RegisteredCommand;
import org.diorite.sender.CommandSender;

public interface CommandManager
{
    ArgumentParsersRegistry getArgumentParsersRegistry();

    void registerCommand(RegisteredCommand registerCommand);
    void execute(CommandSender sender, String commandLine);
    void registerCommandHolder(CommandHolder commandHolder);
    <T extends Annotation> void registerAnnotationHandler(Class<? extends T> annotationType, AnnotationHandler<T> handler);
    @Nullable
    <T extends Annotation> AnnotationHandler<T> getAnnotationHandler(Class<? extends T> annotationType);
}
