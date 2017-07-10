package org.diorite.message;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Locale;

import org.diorite.message.holders.MessageHolder;
import org.diorite.message.messages.TranslatableMessage;

class MessagesNode implements Messages
{
    private final RootMessages root;
    private final Messages     parent;
    private final String       nodeKey;

    MessagesNode(RootMessages root, Messages parent, String nodeKey)
    {
        this.root = root;
        this.parent = parent;
        this.nodeKey = nodeKey;
    }

    @Override
    public Collection<? extends Locale> getSupportedLocales()
    {
        return this.root.getSupportedLocales();
    }

    @Override
    public Locale getDefaultLocale()
    {
        return this.root.getDefaultLocale();
    }

    @Override
    public String getSectionPath()
    {
        return this.parent.getAbsolutePath(this.nodeKey);
    }

    @Nullable
    @Override
    public Messages getParent()
    {
        return this.parent;
    }

    @Override
    public void addMessage(String path, MessageHolder messageHolder)
    {
        this.root.addMessage(this.getAbsolutePath(path), messageHolder);
    }

    @Override
    public Messages getSection(String path)
    {
        return this.root.getSection(this.getAbsolutePath(path));
    }

    @Nullable
    @Override
    public TranslatableMessage getMessage(String message)
    {
        return this.root.getMessage(this.getAbsolutePath(message));
    }

    @Override
    public String getName()
    {
        return this.root.getName();
    }
}
