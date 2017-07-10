package org.diorite.message.loader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.diorite.message.Messages;
import org.diorite.message.MessagesController;

/**
 * Represents messages loader that can be used to load messages into object, note that messages can be stored in multiple files
 */
public interface MessagesLoader
{
    default Messages load(String name, Charset charset, Locale... locales) throws IOException
    {
        return this.load(name, charset, Arrays.asList(locales));
    }

    Messages load(String name, Charset charset, Collection<? extends Locale> locales) throws IOException;

    static MessagesLoaderBuilder builder(MessagesController controller)
    {
        return new MessagesLoaderBuilder(controller);
    }
}
