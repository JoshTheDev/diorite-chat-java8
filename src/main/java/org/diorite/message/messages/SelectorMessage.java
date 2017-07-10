package org.diorite.message.messages;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import org.diorite.chat.ChatMessage;

import groovy.lang.Closure;

/**
 * Represent message that use multiple messages and send valid one based on selectors
 */
public final class SelectorMessage extends AbstractMessage
{
    private final Collection<SelectorMessageNode> nodes;

    public SelectorMessage(String name, Locale locale, Collection<? extends SelectorMessageNode> nodes)
    {
        super(name, locale);
        this.nodes = new ArrayList<>(nodes);
    }

    public SelectorMessage(String name, Locale locale, SelectorMessageNode... nodes)
    {
        super(name, locale);
        this.nodes = Arrays.asList(nodes.clone());
    }

    @Nullable
    @Override
    public ChatMessage prepare(Map<? extends String, ?> data)
    {
        try
        {
            for (SelectorMessageNode node : this.nodes)
            {
                LocalizedMessage message = node.getIfValid(data);
                if (message != null)
                {
                    return message.prepare(data);
                }
            }
        }
        catch (Exception e)
        {
            throw new MessagePrepareException(this, e);
        }
        return null;
    }

    /**
     * Single message node that store predicate and message that will be used if predicate matches.
     */
    public static class SelectorMessageNode
    {
        final Predicate<Map<? extends String, ?>> predicate;
        final LocalizedMessage                    message;

        public SelectorMessageNode(Predicate<Map<? extends String, ?>> predicate, LocalizedMessage message)
        {
            this.predicate = predicate;
            this.message = message;
        }

        /**
         * Tries to get this message if predicate is matched, return null if it failed.
         *
         * @param data
         *         map with message data.
         *
         * @return message or null if it failed.
         */
        @Nullable
        public LocalizedMessage getIfValid(Map<? extends String, ?> data)
        {
            if (! this.predicate.test(data))
            {
                return null;
            }
            return this.message;
        }

        public static SelectorMessageNode ofClosure(Closure<? extends Boolean> closure, LocalizedMessage message)
        {
            Predicate<Map<? extends String, ?>> predicate = map -> {
                if (closure.getMaximumNumberOfParameters() == 0)
                {
                    return closure.rehydrate(closure.getDelegate(), map, map).call();
                }
                return closure.rehydrate(closure.getDelegate(), map, map).call(map);
            };
            return new SelectorMessageNode(predicate, message);
        }
    }
}
