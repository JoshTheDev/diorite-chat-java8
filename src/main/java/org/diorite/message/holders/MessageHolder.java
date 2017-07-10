package org.diorite.message.holders;

import org.diorite.message.messages.Message;

/**
 * Represent message holder, message holders can be used to randomize/edit message that will be returned by message registry.
 */
public interface MessageHolder<T extends Message>
{
    /**
     * Returns message held by this holder.
     *
     * @return message held by this holder.
     */
    T getMessage();
}
