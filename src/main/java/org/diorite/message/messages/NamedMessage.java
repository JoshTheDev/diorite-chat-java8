package org.diorite.message.messages;

/**
 * Represents message that have some name, that can be a key in config etc.
 */
public interface NamedMessage extends Message
{
    /**
     * Returns name/key of this message.
     *
     * @return name/key of this message.
     */
    String getName();
}
