package org.diorite.message.loader;

import java.io.IOException;
import java.io.Reader;

import org.jetbrains.annotations.NotNull;

public class LocaleReader extends Reader
{
    private final Reader reader;
    private final LocaleMessagesLoader localeMessagesLoader;

    public LocaleReader(Reader reader, LocaleMessagesLoader localeMessagesLoader)
    {
        this.reader = reader;
        this.localeMessagesLoader = localeMessagesLoader;
    }

    public LocaleMessagesLoader getLocaleMessagesLoader()
    {
        return this.localeMessagesLoader;
    }

    @Override
    public int read(@NotNull char[] cbuf, int off, int len) throws IOException
    {
        return this.reader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException
    {
        this.reader.close();
    }
}
