package org.diorite.message.loader;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

import org.diorite.message.Messages;

public interface LocaleMessagesLoader
{
    String getName();

    String getExtension();

    void load(Messages messages, Locale locale, Reader reader) throws IOException;
}
