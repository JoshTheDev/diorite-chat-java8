package org.diorite.command;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.Builder;

import org.diorite.command.executor.ArgumentCommandExecutor;

public class ArgumentCommandBuilder implements Builder<RegisteredCommand>
{
    private final String name;
    private List<String> aliases = new ArrayList<>();
    private @Nullable ArgumentCommandExecutor argumentCommandExecutor;
    private List<Argument<?>> arguments = new ArrayList<>();

    ArgumentCommandBuilder(String name)
    {
        this.name = name;
    }

    public ArgumentCommandBuilder withArgument(ArgumentBuilder<?> argument)
    {
        return this.withArgument(argument.build());
    }

    public ArgumentCommandBuilder withArgument(Argument<?> argument)
    {
        this.arguments.add(argument);
        return this;
    }

    public ArgumentCommandBuilder withArgumentExecutor(ArgumentCommandExecutor executor)
    {
        this.argumentCommandExecutor = executor;
        return this;
    }

    @Override
    public RegisteredCommand build()
    {
        if (this.argumentCommandExecutor == null)
        {
            throw new IllegalStateException("Executor can't be null.");
        }
        return new ArgumentRegisteredCommand(this.name, this.aliases, this.argumentCommandExecutor, this.arguments);
    }
}
