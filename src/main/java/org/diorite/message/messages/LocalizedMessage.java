package org.diorite.message.messages;

import java.util.Locale;

/**
 * Represent message in some language/locale.
 */
public interface LocalizedMessage extends Message
{
    /**
     * Returns language of this message if available.
     *
     * @return language of this message if available.
     */
    Locale getLocale();

    static LocalizedMessage empty(String name, Locale locale)
    {
        return new StaticMessage("name", locale, null);
    }
}
