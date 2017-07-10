package org.diorite.message.holders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.diorite.commons.math.DioriteRandomUtils;
import org.diorite.message.messages.LocalizedMessage;
import org.diorite.message.messages.Message;

/**
 * Store multiple messages where one is randomly chosen on each call of {@link #getMessage()}
 */
public final class RandomMessageHolder<T extends Message> implements MessageHolder<T>
{
    private final List<? extends MessageHolder<T>> messageHolders;

    RandomMessageHolder(List<? extends MessageHolder<T>> messageHolders)
    {
        if (messageHolders.isEmpty())
        {
            throw new IllegalStateException("Can't create RandomMessageHolder without messages.");
        }
        this.messageHolders = messageHolders;
    }

    @Override
    public T getMessage()
    {
        MessageHolder<T> random = DioriteRandomUtils.getRandom(this.messageHolders);
        assert random != null;
        return random.getMessage();
    }

    public static <T extends LocalizedMessage> RandomMessageHolder<T> fromMessages(Collection<? extends T> messages)
    {
        List<MessageHolder<T>> holders = new ArrayList<>(messages.size());
        for (T message : messages)
        {
            holders.add(new SingleMessageHolder<>(message));
        }
        return new RandomMessageHolder<>(holders);
    }

    public static <T extends Message> RandomMessageHolder<T> fromHolders(Collection<? extends MessageHolder<T>> messageHolders)
    {
        return new RandomMessageHolder<>(new ArrayList<>(messageHolders));
    }
}
