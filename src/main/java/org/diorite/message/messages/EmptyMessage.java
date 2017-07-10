package org.diorite.message.messages;

import javax.annotation.Nullable;

import java.util.Map;

import org.diorite.chat.ChatMessage;
import org.diorite.message.data.MessageData;

final class EmptyMessage implements NamedMessage
{
    @Override
    public String getName()
    {
        return "";
    }

    @Nullable
    @Override
    public ChatMessage prepareWith(MessageData<?>... data)
    {
        return null;
    }

    @Nullable
    @Override
    public ChatMessage prepare(Map<? extends String, ?> data)
    {
        return null;
    }
}
