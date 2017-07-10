package org.diorite.message.holders;

import org.diorite.message.messages.Message;

/**
 * Store single simple message
 */
public final class SingleMessageHolder<T extends Message> implements MessageHolder<T>
{
    private final T message;

    public SingleMessageHolder(T message)
    {
        this.message = message;
    }

    @Override
    public T getMessage()
    {
        return this.message;
    }
}
