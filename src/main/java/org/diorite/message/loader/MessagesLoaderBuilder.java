package org.diorite.message.loader;

import javax.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.lang3.builder.Builder;

import org.diorite.message.MessagesController;

public class MessagesLoaderBuilder implements Builder<MessagesLoader>
{
    private final MessagesController messagesController;

    MessagesLoaderBuilder(MessagesController messagesController)
    {
        this.messagesController = messagesController;
    }

    private @Nullable LocaleReaderProvider localeLoader;
    private @Nullable LocaleReaderProvider defaultLoader;
    private @Nullable LocaleWriterProvider writerProvider;

    public MessagesLoaderBuilder localeLoaderFromFunction(LocaleReaderProvider localeLoader)
    {
        this.localeLoader = localeLoader;
        return this;
    }

    public MessagesLoaderBuilder setWriterProvider(LocaleWriterProvider writerProvider)
    {
        this.writerProvider = writerProvider;
        return this;
    }

    public MessagesLoaderBuilder localeLoaderFromFolder(File file)
    {
        if (file.exists() && ! file.isDirectory())
        {
            throw new IllegalStateException("expected directory not a file: " + file);
        }
        this.writerProvider = (loader, charset, name, locale) -> {
            file.mkdirs();
            File langFile = new File(file, name + "_" + locale.toLanguageTag() + ".yml");
            if (! langFile.exists())
            {
                langFile.getAbsoluteFile().getParentFile().mkdirs();
                langFile.createNewFile();
            }
            return new OutputStreamWriter(new FileOutputStream(langFile), charset.newEncoder());
        };
        return this.localeLoaderFromFunction((loader, charset, name, l) -> {
            if (! file.exists() || ! file.isDirectory())
            {
                return null;
            }
            for (LocaleMessagesLoader messagesLoader : this.messagesController.getLocaleMessagesLoaders())
            {
                File langFile = new File(file, name + "_" + l.toLanguageTag() + "." + messagesLoader.getExtension());
                if (! langFile.exists() || ! langFile.isFile())
                {
                    continue;
                }
                try
                {
                    return new LocaleReader(new InputStreamReader(new FileInputStream(langFile), charset.newDecoder()), messagesLoader);
                }
                catch (IOException e)
                {
                    throw new MessagesLoadException(loader, langFile, e);
                }
            }
            return null;
        });
    }

    public MessagesLoaderBuilder defaultLoaderFromFunction(LocaleReaderProvider defaultLoader)
    {
        this.defaultLoader = defaultLoader;
        return this;
    }

    public MessagesLoaderBuilder defaultLoaderFromResourcePath(Class<?> resourceSource, String basePath_)
    {
        if (! basePath_.endsWith("/") && ! basePath_.trim().isEmpty())
        {
            basePath_ += "/";
        }
        String basePath = basePath_;
        return this.defaultLoaderFromFunction((loader, charset, name, l) -> {
            for (LocaleMessagesLoader messagesLoader : this.messagesController.getLocaleMessagesLoaders())
            {
                InputStream resourceAsStream =
                    resourceSource.getResourceAsStream(basePath + name + "_" + l.toLanguageTag() + "." + messagesLoader.getExtension());
                if (resourceAsStream == null)
                {
                    continue;
                }
                return new LocaleReader(new InputStreamReader(resourceAsStream, charset.newDecoder()), messagesLoader);
            }
            return null;
        });
    }

    @Override
    public MessagesLoader build()
    {
        if (this.localeLoader == null)
        {
            throw new IllegalStateException("Can't create message loader without locale loader!");
        }
        if (this.defaultLoader == null)
        {
            throw new IllegalStateException("Can't create message loader without default loader!");
        }
        if (this.writerProvider == null)
        {
            throw new IllegalStateException("Can't create message loader without writer provider!");
        }
        return new MessagesLoaderImpl(this.localeLoader, this.defaultLoader, this.writerProvider);
    }
}
