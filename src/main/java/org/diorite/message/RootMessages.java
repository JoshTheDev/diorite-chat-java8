package org.diorite.message;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import org.diorite.message.holders.MessageHolder;
import org.diorite.message.holders.SingleMessageHolder;
import org.diorite.message.messages.LocalizedMessage;
import org.diorite.message.messages.TranslatableMessage;

class RootMessages implements Messages
{
    private final String             name;
    private final Collection<Locale> locales;
    private final Locale             defaultLocale;

    private final Map<String, MessagesNode>        cachedNodes = new ConcurrentHashMap<>();
    private final Map<String, TranslatableMessageImpl> messages    = new ConcurrentHashMap<>();

    RootMessages(String name, Collection<? extends Locale> locales)
    {
        if (locales.isEmpty())
        {
            throw new IllegalStateException("Locales can not be empty!");
        }
        this.name = name;
        this.locales = new ArrayList<>(locales);
        this.defaultLocale = locales.iterator().next();
    }

    RootMessages(String name, Collection<? extends Locale> locales, Locale defaultLocale)
    {
        this.name = name;
        this.locales = new ArrayList<>(locales);
        if (! this.locales.contains(defaultLocale))
        {
            this.locales.add(defaultLocale);
        }
        this.defaultLocale = defaultLocale;
    }

    @Override
    public Collection<? extends Locale> getSupportedLocales()
    {
        return Collections.unmodifiableCollection(this.locales);
    }

    @Override
    public Locale getDefaultLocale()
    {
        return this.defaultLocale;
    }

    @Override
    public String getSectionPath()
    {
        return "";
    }

    @Nullable
    @Override
    public Messages getParent()
    {
        return null; // root does not have parent
    }

    @Override
    public Messages getSection(String path)
    {
        return this.getSection(StringUtils.splitPreserveAllTokens(path, PATH_SEPARATOR));
    }

    private Messages getSection(String[] path)
    {
        String key = path[path.length - 1];
        String fullPath = StringUtils.join(path, PATH_SEPARATOR);
        String parentPath = StringUtils.join(path, PATH_SEPARATOR, 0, path.length - 1);

        MessagesNode messagesNode = this.cachedNodes.get(fullPath);
        if (messagesNode != null)
        {
            return messagesNode;
        }
        synchronized (this.cachedNodes)
        {
            messagesNode = this.cachedNodes.get(fullPath);
            if (messagesNode != null)
            {
                return messagesNode;
            }
            MessagesNode node = new MessagesNode(this, this.getSection(parentPath), key);
            this.cachedNodes.put(fullPath, node);
            return node;
        }
    }

    @Override
    public void addMessage(String path, MessageHolder<? extends LocalizedMessage> messageHolder)
    {
        Locale locale = messageHolder.getMessage().getLocale();
        this.messages.computeIfAbsent(path, TranslatableMessageImpl::new).messages.put(locale, messageHolder);
    }

    @Nullable
    @Override
    public TranslatableMessage getMessage(String message)
    {
        return this.messages.get(message);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    class TranslatableMessageImpl implements TranslatableMessage
    {
        private final Map<Locale, MessageHolder<? extends LocalizedMessage>> messages = new ConcurrentHashMap<>();
        private final String name;

        TranslatableMessageImpl(String name)
        {
            this.name = name;
        }

        @Override
        public Locale getDefaultLocale()
        {
            return RootMessages.this.defaultLocale;
        }

        @Override
        public LocalizedMessage get(Locale lang)
        {
            MessageHolder<? extends LocalizedMessage> messageHolder = this.messages.get(lang);
            if (messageHolder == null)
            {
                messageHolder = this.messages.get(this.getDefaultLocale());
            }
            if (messageHolder == null)
            {
                LocalizedMessage empty = LocalizedMessage.empty(this.name, lang);
                this.messages.put(lang, new SingleMessageHolder<>(empty));
                return empty;
            }
            return messageHolder.getMessage();
        }

        @Override
        public String getName()
        {
            return this.name;
        }
    }
}
