package org.diorite.message.loader;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;

public interface LocaleWriterProvider
{
    Writer get(MessagesLoader loader, Charset charset, String name, Locale locale) throws IOException;
}
