package org.diorite.command.executor;

import org.diorite.command.RegisteredCommand;
import org.diorite.sender.CommandSender;

public interface CommandExecutor
{
    void execute(CommandSender sender, RegisteredCommand command, String alias, String commandLine);
}
