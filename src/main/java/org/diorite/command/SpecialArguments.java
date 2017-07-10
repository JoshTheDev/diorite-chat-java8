package org.diorite.command;

import java.lang.reflect.Parameter;

import org.diorite.command.annotation.arguments.Sender;
import org.diorite.sender.CommandSender;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

class SpecialArguments
{
    // TODO: way to register new stuff instead of hard coded fields etc.
    private final IntSet   specialArguments = new IntOpenHashSet();
    private       int      senderArgument   = - 1;
    private       Class<?> senderType       = CommandSender.class;

    public void parse(int i, Parameter param)
    {
        boolean isSender = param.isAnnotationPresent(Sender.class);
        if (isSender)
        {
            if (CommandSender.class.isAssignableFrom(param.getType()))
            {
                this.senderType = param.getType();
                this.senderArgument = i;
                this.specialArguments.add(i);
            }
            else
            {
                throw new IllegalStateException("This param can't be an sender: " + param);
            }
        }
    }

    public boolean isSpecial(int i)
    {
        return this.specialArguments.contains(i);
    }

    public void provide(Object[] arguments, CommandSender sender, RegisteredCommand command, String alias)
    {
        // TODO:
        if (this.senderArgument != - 1)
        {
            if (! this.senderType.isInstance(sender))
            {
                throw new IllegalStateException("Expected: " + this.senderType + " as sender but: " + sender +
                                                " was found"); // TODO: this should throw custom exception that is later cached to display error to player
            }
            arguments[this.senderArgument] = sender;
        }
    }

    static SpecialArguments create()
    {
        return new SpecialArguments();
    }
}
