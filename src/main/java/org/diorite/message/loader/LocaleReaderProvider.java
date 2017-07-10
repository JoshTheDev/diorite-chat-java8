package org.diorite.message.loader;

import javax.annotation.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public interface LocaleReaderProvider
{
    @Nullable
    LocaleReader resolve(MessagesLoader loader, Charset charset, String name, Locale locale) throws IOException, MessagesLoadException;
}
