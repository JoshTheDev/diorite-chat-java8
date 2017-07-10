package org.diorite.command;

import java.util.Collection;

import org.diorite.command.executor.CommandExecutor;
import org.diorite.sender.CommandSender;

public interface RegisteredCommand
{
    String getName();
    Collection<? extends String> getAliases();
    CommandExecutor getExecutor();

    default void execute(CommandSender sender, RegisteredCommand command, String alias, String commandLine)
    {
        this.getExecutor().execute(sender, command, alias, commandLine);
    }
}
