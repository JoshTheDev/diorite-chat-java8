package org.diorite.command;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.NotImplementedException;

import org.diorite.command.executor.ArgumentCommandExecutor;
import org.diorite.command.executor.CommandExecutor;
import org.diorite.command.parser.CommandParserContext;
import org.diorite.command.parser.ParserResult;

class ArgumentRegisteredCommand implements RegisteredCommand
{
    private final String                  name;
    private final Collection<String>      aliases;
    private final ArgumentCommandExecutor commandExecutor;
    private final Collection<Argument<?>> arguments;

    ArgumentRegisteredCommand(String name, Collection<String> aliases, ArgumentCommandExecutor commandExecutor, Collection<Argument<?>> arguments)
    {
        this.name = name;
        this.aliases = new ArrayList<>(aliases);
        this.commandExecutor = commandExecutor;
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Collection<? extends String> getAliases()
    {
        return new ArrayList<>(this.aliases);
    }

    @Override
    public CommandExecutor getExecutor()
    {
        return (sender, command, alias, commandLine) -> {
            CommandParserContext parserContext = CommandParserContext.create(commandLine, this.arguments);
            ParserResult parse = parserContext.parse();
            if (! parse.isSuccess())
            {
                // TODO: should send some message to player depending on parser context.
                throw new NotImplementedException("should send some message to player depending on parser context.");
            }

            this.commandExecutor.execute(sender, command, alias, new ParsedArguments(parse.getParsed()));
        };
    }
}
