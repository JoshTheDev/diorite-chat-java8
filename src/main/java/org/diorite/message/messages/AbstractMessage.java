package org.diorite.message.messages;

import java.util.Locale;

/**
 * Abstract message type for simpler implementation, already implements getName/Locale methods.
 */
public abstract class AbstractMessage implements LocalizedMessage, NamedMessage
{
    private final String name;
    private final Locale locale;

    public AbstractMessage(String name, Locale locale)
    {
        this.name = name;
        this.locale = locale;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Locale getLocale()
    {
        return this.locale;
    }
}
