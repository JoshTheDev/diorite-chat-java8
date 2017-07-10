package org.diorite.command.executor;

import org.diorite.command.ParsedArguments;
import org.diorite.command.RegisteredCommand;
import org.diorite.sender.CommandSender;

public interface ArgumentCommandExecutor
{
    void execute(CommandSender sender, RegisteredCommand command, String alias, ParsedArguments arguments);
}
