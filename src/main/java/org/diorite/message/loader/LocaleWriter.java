package org.diorite.message.loader;

import java.io.IOException;
import java.io.Writer;

import org.jetbrains.annotations.NotNull;

public class LocaleWriter extends Writer
{
    private final Writer               writer;
    private final LocaleMessagesLoader localeMessagesLoader;

    public LocaleWriter(Writer writer, LocaleMessagesLoader localeMessagesLoader)
    {
        this.writer = writer;
        this.localeMessagesLoader = localeMessagesLoader;
    }

    public LocaleMessagesLoader getLocaleMessagesLoader()
    {
        return this.localeMessagesLoader;
    }

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) throws IOException
    {
        this.writer.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException
    {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException
    {
        this.writer.close();
    }
}
