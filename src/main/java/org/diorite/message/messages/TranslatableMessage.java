package org.diorite.message.messages;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.diorite.DioriteAPIBridge;
import org.diorite.chat.ChatMessage;
import org.diorite.chat.ChatMessageType;
import org.diorite.chat.MessageReceiver;
import org.diorite.message.data.MessageData;

/**
 * Represent message that might support more than one language.
 */
public interface TranslatableMessage extends Message, NamedMessage
{
    /**
     * Returns default locale to use if requested one isn't available.
     *
     * @return default locale to use if requested one isn't available.
     */
    Locale getDefaultLocale();

    /**
     * Returns message for given language, if given language is not available it will try to get this same message in default language and if it also not
     * available then empty value will be returned.
     *
     * @param lang
     *         language to use if possible.
     *
     * @return message object, empty if message isn't enabled or can't be used.
     */
    LocalizedMessage get(Locale lang);

    /**
     * Prepare message for given data.
     *
     * @param locale
     *         locale of message to use.
     * @param data
     *         placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepare(@Nullable Locale locale, Map<? extends String, ?> data)
    {
        return this.get((locale == null) ? this.getDefaultLocale() : locale).prepare(data);
    }

    /**
     * Prepare message for given data.
     *
     * @param locale
     *         locale of message to use.
     * @param data
     *         placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepareWith(@Nullable Locale locale, MessageData<?>... data)
    {
        Map<String, Object> dataMap = new HashMap<>(data.length);
        for (MessageData<?> d : data)
        {
            dataMap.putIfAbsent(d.getMessageKey(), d.getMessageValue());
        }
        return this.prepare(locale, dataMap);
    }

    @Override
    @Nullable
    default ChatMessage prepare(Map<? extends String, ?> data)
    {
        return this.prepare(this.getDefaultLocale(), data);
    }

    @Override
    @Nullable
    default ChatMessage prepare(MessageReceiver receiver, Map<? extends String, ?> data)
    {
        Map<String, Object> dataMap = new HashMap<>(data);
        Message.addReceiverToDataMap(dataMap, receiver);
        return this.prepare(receiver.getMessageOutput().getPreferredLocale(), data);
    }

    @Override
    @Nullable
    default ChatMessage prepareWith(MessageReceiver receiver, MessageData<?>... data)
    {
        Map<String, Object> dataMap = new HashMap<>(data.length + 4);
        for (MessageData<?> d : data)
        {
            dataMap.putIfAbsent(d.getMessageKey(), d.getMessageValue());
        }
        Message.addReceiverToDataMap(dataMap, receiver);
        return this.prepare(receiver.getMessageOutput().getPreferredLocale(), dataMap);
    }

    @Override
    @Nullable
    default ChatMessage prepareWith(MessageData<?>... data)
    {
        Map<String, Object> dataMap = new HashMap<>(data.length);
        for (MessageData<?> d : data)
        {
            dataMap.putIfAbsent(d.getMessageKey(), d.getMessageValue());
        }
        return this.prepare(dataMap);
    }

    @Override
    default boolean sendMessage(MessageReceiver target, MessageData<?>... data)
    {
        return this.sendMessage(ChatMessageType.SYSTEM, target, data);
    }

    @Override
    default boolean sendMessage(ChatMessageType type, MessageReceiver target, MessageData<?>... data)
    {
        ChatMessage prepare = this.prepareWith(target, data);
        if (prepare == null)
        {
            return false;
        }
        target.sendMessage(type, prepare);
        return true;
    }

    @Override
    default boolean broadcastMessage(MessageData<?>... data)
    {
        return this.broadcastMessage(ChatMessageType.SYSTEM, DioriteAPIBridge.getAPIBridge().getAllReceivers(), data);
    }

    @Override
    default boolean broadcastMessage(ChatMessageType type, MessageData<?>... data)
    {
        return this.broadcastMessage(type, DioriteAPIBridge.getAPIBridge().getAllReceivers(), data);
    }

    @Override
    default boolean broadcastMessage(Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        return this.broadcastMessage(ChatMessageType.SYSTEM, targets, data);
    }

    @Override
    default boolean broadcastMessage(ChatMessageType type, Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        Map<String, Object> dataMap = new HashMap<>(data.length);
        for (MessageData<?> d : data)
        {
            dataMap.putIfAbsent(d.getMessageKey(), d.getMessageValue());
        }

        Map<Locale, ChatMessage> chatMessages = new HashMap<>();
        boolean result = false;
        for (MessageReceiver target : targets)
        {
            Locale locale = target.getMessageOutput().getPreferredLocale();
            if (locale == null)
            {
                locale = this.getDefaultLocale();
            }
            ChatMessage chatMessage = chatMessages.computeIfAbsent(locale, l -> this.prepare(l, dataMap));
            if (chatMessage == null)
            {
                continue;
            }
            target.sendMessage(type, chatMessage);
            result = true;
        }
        return result;
    }
}
