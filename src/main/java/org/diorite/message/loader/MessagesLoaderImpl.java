package org.diorite.message.loader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

import org.diorite.message.Messages;

class MessagesLoaderImpl implements MessagesLoader
{
    private static final int BUFFERSIZE = 4096;
    private final LocaleReaderProvider localeLoader;
    private final LocaleReaderProvider defaultLoader;
    private final LocaleWriterProvider writerProvider;

    MessagesLoaderImpl(LocaleReaderProvider localeLoader, LocaleReaderProvider defaultLoader, LocaleWriterProvider writerProvider)
    {
        this.localeLoader = localeLoader;
        this.defaultLoader = defaultLoader;
        this.writerProvider = writerProvider;
    }

    @Override
    public Messages load(String name, Charset charset, Collection<? extends Locale> locales) throws IOException
    {
        Messages messages = Messages.create(name, locales);
        for (Locale locale : locales)
        {
            boolean done = false;
            try (LocaleReader localeReader = this.localeLoader.resolve(this, charset, name, locale))
            {
                if (localeReader == null)
                {
                    this.copyDefault(name, charset, locale);
                }
                else
                {
                    done = true;
                    this.loadFromReader(localeReader, messages, locale);
                }
            }
            if (done)
            {
                continue;
            }
            try (LocaleReader localeReader = this.localeLoader.resolve(this, charset, name, locale))
            {
                if (localeReader == null)
                {
                    new RuntimeException("Can't load locale: " + name + "/" + locale + ". Skipping...").printStackTrace();
                    continue;
                }
                this.loadFromReader(localeReader, messages, locale);
            }
        }
        return messages;
    }

    private void loadFromReader(LocaleReader localeReader, Messages messages, Locale locale) throws IOException
    {
        localeReader.getLocaleMessagesLoader().load(messages, locale, localeReader);
    }

    private void copyDefault(String name, Charset charset, Locale locale) throws IOException
    {
        try (LocaleReader reader = this.defaultLoader.resolve(this, charset, name, locale))
        {
            if (reader == null)
            {
                return;
            }
            try (Writer writer = this.writerProvider.get(this, charset, name, locale))
            {
                pipe(reader, writer, BUFFERSIZE);
            }
        }
    }

    public static void pipe(Reader reader, Writer writer, int buffersize) throws IOException
    {
        char[] buffer = new char[buffersize];
        int read = 0;
        while ((read = reader.read(buffer)) != - 1)
        {
            writer.write(buffer, 0, read);
        }
    }
}
