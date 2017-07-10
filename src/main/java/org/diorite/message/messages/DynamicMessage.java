package org.diorite.message.messages;

import javax.annotation.Nullable;

import java.util.Locale;
import java.util.Map;

import org.diorite.chat.ChatMessage;

import groovy.lang.Closure;
import groovy.lang.GString;

/**
 * Represent message that dynamically produce message based on given data.
 */
public final class DynamicMessage extends AbstractMessage
{
    private final Closure<?> closure;

    public DynamicMessage(String name, Locale locale, Closure<?> closure)
    {
        super(name, locale);
        this.closure = closure;
    }

    @Nullable
    @Override
    public ChatMessage prepare(Map<? extends String, ?> data)
    {
        try
        {
            Object call = this.closure.rehydrate(this, data, data).call();
            if (call == null)
            {
                return null;
            }
            if (call instanceof ChatMessage)
            {
                return (ChatMessage) call;
            }
            if (call instanceof GString)
            {
                // TODO: possible additional operations
                call = call.toString();
            }
            return ChatMessage.parse(call.toString());
        }
        catch (Exception e)
        {
            throw new MessagePrepareException(this, e);
        }
    }
}
