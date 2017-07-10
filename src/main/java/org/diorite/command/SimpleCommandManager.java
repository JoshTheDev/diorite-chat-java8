package org.diorite.command;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.diorite.command.annotation.Command;
import org.diorite.command.annotation.CommandManager;
import org.diorite.commons.reflections.MethodInvoker;
import org.diorite.sender.CommandSender;

public class SimpleCommandManager implements CommandManager
{
    // TODO subcommands etc.
    private final Map<Class<? extends Annotation>, AnnotationHandler<?>> annotationHandlerMap    = new ConcurrentHashMap<>();
    private final ArgumentParsersRegistry                                argumentParsersRegistry = new ArgumentParsersRegistry();
    private final Map<String, RegisteredCommand>                         commands                = new ConcurrentHashMap<>();

    @Override
    public ArgumentParsersRegistry getArgumentParsersRegistry()
    {
        return this.argumentParsersRegistry;
    }

    @Override
    public void registerCommand(RegisteredCommand registerCommand)
    {
        this.commands.put(registerCommand.getName(), registerCommand);
        for (String s : registerCommand.getAliases())
        {
            this.commands.put(s, registerCommand);
        }
    }

    @Override
    public void execute(CommandSender sender, String commandLine)
    {
        int indexOf = commandLine.indexOf(' ');
        String cmd;
        String args;
        if (indexOf == - 1)
        {
            cmd = commandLine;
            args = "";
        }
        else
        {
            cmd = commandLine.substring(0, indexOf);
            args = commandLine.substring(indexOf + 1);
        }
        RegisteredCommand registeredCommand = this.commands.get(cmd);
        if (registeredCommand == null)
        {
            return;
        }
        registeredCommand.execute(sender, registeredCommand, cmd, args);
    }

    @Override
    public void registerCommandHolder(CommandHolder commandHolder)
    {
        Class<? extends CommandHolder> aClass = commandHolder.getClass();
        for (Method method : aClass.getDeclaredMethods())
        {
            MethodInvoker methodInvoker = new MethodInvoker(method);
            if (methodInvoker.isAnnotationPresent(Command.class))
            {
                if (methodInvoker.isStatic())
                {
                    throw new IllegalStateException("Can't register static command! " + methodInvoker);
                }
                this.parseCommandFromMethod(commandHolder, methodInvoker);
            }
        }
    }

    @Override
    public <T extends Annotation> void registerAnnotationHandler(Class<? extends T> annotationType, AnnotationHandler<T> handler)
    {
        this.annotationHandlerMap.put(annotationType, handler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> AnnotationHandler<T> getAnnotationHandler(Class<? extends T> annotationType)
    {
        return (AnnotationHandler<T>) this.annotationHandlerMap.get(annotationType);
    }

    public ArgumentCommandBuilder createCommandWithArguments(String commandName)
    {
        return new ArgumentCommandBuilder(commandName);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void parseCommandFromMethod(CommandHolder holder, MethodInvoker method)
    {// read command name from method and/or annotations over it. (if there is non-empty name in annotation, that name should be used, but if name is empty,
        // then method name should be used)

        Command commandAnnotation = method.getAnnotation(Command.class);
        String commandName;
        if (commandAnnotation.value().length != 0)
        {
            commandName = commandAnnotation.value()[0];
        }
        else
        {
            commandName = method.getName();
        }
        ArgumentCommandBuilder builder = this.createCommandWithArguments(commandName);
        int parameterCount = method.getParameterCount();
        Parameter[] params = method.getParameters();

        SpecialArguments specialArguments = SpecialArguments.create();
        int paramIndex = 0;
        for (Parameter param : params)
        {
            specialArguments.parse(paramIndex++, param);
        }
        for (int i = 0; i < parameterCount; i++)
        {
            if (specialArguments.isSpecial(i))
            {
                continue;
            }
            Parameter param = params[i];
            ArgumentBuilder<?> argumentBuilder = this.argumentParsersRegistry.of(param);
            for (Annotation annotation : param.getAnnotations())
            {
                AnnotationHandler handler = this.getAnnotationHandler(annotation.annotationType());
                if (handler == null)
                {
                    continue; // as it might be some different annotation that we don't support
                }
                handler.apply(argumentBuilder, annotation, param, method); // and/or other data that might be useful
            }
            builder.withArgument(argumentBuilder);
        }
// and now there is hard part... as we need to create invoker for that method, but provide special arguments manually
        method.ensureAccessible();
        MethodHandle methodHandle = method.getHandle().bindTo(holder);
// not sure about that name tho :D
        builder.withArgumentExecutor((sender, command, alias, args) ->
                                     {
                                         // we also need to check if given sender is of valid type, as if someone use `Player` as method argument, we should
                                         // send some generic error message (from config that does not exist yet :D so you can just place //TODO to add it
                                         // later):

                                         // also we should not reference any annotation stuff here, as that will keep that references loaded.
                                         Object[] arguments = new Object[parameterCount];
                                         specialArguments.provide(arguments, sender, command,
                                                                  alias); // this method will set arguments on valid positions if they exist, provide
                                         // (Object[] arguments, CommandSender sender, Command command, String alias), as this isn't part of API we can later
                                         // add more stuff here.
                                         int arg = 0;
                                         for (int i = 0; i < parameterCount; i++)
                                         {
                                             Object argument = arguments[i];
                                             if (argument != null)
                                             {
                                                 continue; // skip arguments that were provided by SpecialArgument class
                                             }
                                             arguments[i] = args.get(arg++);
                                         }
                                         try
                                         {
                                             methodHandle.invokeWithArguments(arguments);
                                         }
                                         catch (Throwable throwable)
                                         {
                                             // TODO: handle this
                                             throwable.printStackTrace();
                                         }
                                     });
        this.registerCommand(builder.build());
    }
}
