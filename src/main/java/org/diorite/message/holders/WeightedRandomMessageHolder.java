package org.diorite.message.holders;

import java.util.Map;

import org.diorite.commons.math.DioriteRandomUtils;
import org.diorite.message.messages.Message;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

/**
 * Store multiple messages where one is randomly chosen on each call of {@link #getMessage()}
 */
public final class WeightedRandomMessageHolder<T extends Message> implements MessageHolder<T>
{
    private final Object2DoubleMap<MessageHolder<T>> messages;

    public WeightedRandomMessageHolder(Map<? extends MessageHolder<T>, ? extends Double> messages)
    {
        if (messages.isEmpty())
        {
            throw new IllegalStateException("Can't create WeightedRandomMessageHolder without messages.");
        }
        this.messages = new Object2DoubleOpenHashMap<>(messages);
    }

    @Override
    public T getMessage()
    {
        MessageHolder<T> random = DioriteRandomUtils.getWeightedRandom(this.messages);
        assert random != null;
        return random.getMessage();
    }
}
