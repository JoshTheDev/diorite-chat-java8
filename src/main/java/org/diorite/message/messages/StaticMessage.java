package org.diorite.message.messages;

import javax.annotation.Nullable;

import java.util.Locale;
import java.util.Map;

import org.diorite.chat.ChatMessage;

/**
 * Represents static message that does not use any data to process message.
 */
public final class StaticMessage extends AbstractMessage
{
    private final @Nullable ChatMessage message;

    public StaticMessage(String name, Locale locale, @Nullable ChatMessage message)
    {
        super(name, locale);
        this.message = message;
    }

    @Override
    @Nullable
    public ChatMessage prepare(Map<? extends String, ?> data)
    {
        return this.message;
    }
}
