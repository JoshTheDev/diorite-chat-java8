package org.diorite.message.messages;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import org.diorite.DioriteAPIBridge;
import org.diorite.chat.ChatMessage;
import org.diorite.chat.ChatMessageType;
import org.diorite.chat.MessageReceiver;
import org.diorite.message.data.MessageData;

/**
 * Represent something that can be send to player as message with custom data.
 */
public interface Message
{
    /**
     * Prepare message for given data.
     *
     * @param data
     *         placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    ChatMessage prepare(Map<? extends String, ?> data);

    /**
     * Prepare message for given data.
     *
     * @param receiver
     *         receiver of message, will be added as additional MessageData with "receiver","player","target" and "sender" key if not already present in map
     * @param data
     *         placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepare(MessageReceiver receiver, Map<? extends String, ?> data)
    {
        Map<String, Object> dataMap = new HashMap<>(data);
        addReceiverToDataMap(dataMap, receiver);
        return this.prepare(dataMap);
    }

    /**
     * Prepare message for given data.
     *
     * @param data
     *         placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
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

    /**
     * Prepare message for given data.
     *
     * @param receiver
     *         receiver of message, will be added as additional MessageData with "receiver","player","target" and "sender" key if not already present in map
     * @param data
     *         placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepareWith(MessageReceiver receiver, MessageData<?>... data)
    {
        Map<String, Object> dataMap = new HashMap<>(data.length + 4);
        for (MessageData<?> d : data)
        {
            dataMap.putIfAbsent(d.getMessageKey(), d.getMessageValue());
        }
        addReceiverToDataMap(dataMap, receiver);
        return this.prepare(dataMap);
    }

    /**
     * Try send this message to given {@link MessageReceiver}, if message is disabled method will just return false.
     *
     * @param target
     *         target of message.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send.
     */
    default boolean sendMessage(MessageReceiver target, MessageData<?>... data)
    {
        return this.sendMessage(ChatMessageType.SYSTEM, target, data);
    }

    /**
     * Try send this message to given {@link MessageReceiver}, if message is disabled method will just return false.
     *
     * @param type
     *         type of message to send.
     * @param target
     *         target of message.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send.
     */
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

    /**
     * Try broadcast this message (to all players, resolving placeholders once), if message is disabled method will just return false.
     *
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(MessageData<?>... data)
    {
        return this.broadcastMessage(ChatMessageType.SYSTEM, DioriteAPIBridge.getAPIBridge().getAllReceivers(), data);
    }

    /**
     * Try broadcast this message (to all players, resolving placeholders once), if message is disabled method will just return false.
     *
     * @param type
     *         type of message to send.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(ChatMessageType type, MessageData<?>... data)
    {
        return this.broadcastMessage(type, DioriteAPIBridge.getAPIBridge().getAllReceivers(), data);
    }

    /**
     * Try broadcast this message to selected command senders, if message is disabled method will just return false.
     *
     * @param targets
     *         targets of message.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        return this.broadcastMessage(ChatMessageType.SYSTEM, targets, data);
    }

    /**
     * Try broadcast this message to selected command senders, if message is disabled method will just return false.
     *
     * @param type
     *         type of message to send.
     * @param targets
     *         targets of message.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(ChatMessageType type, Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        ChatMessage chatMessage = this.prepareWith(data);
        if (chatMessage == null)
        {
            return false;
        }
        boolean result = false;
        for (MessageReceiver target : targets)
        {
            target.sendMessage(type, chatMessage);
            result = true;
        }
        return result;
    }

    /**
     * Try broadcast this message (to all players), if message is disabled method will just return false.
     *
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(MessageData<?>... data)
    {
        return this.broadcastPersonalizedMessage(ChatMessageType.SYSTEM, DioriteAPIBridge.getAPIBridge().getAllReceivers());
    }

    /**
     * Try broadcast this message (to all players), if message is disabled method will just return false.
     *
     * @param type
     *         type of message to send.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(ChatMessageType type, MessageData<?>... data)
    {
        return this.broadcastPersonalizedMessage(ChatMessageType.SYSTEM, DioriteAPIBridge.getAPIBridge().getAllReceivers());
    }

    /**
     * Try broadcast this message to selected command senders, if message is disabled method will just return false.
     *
     * @param targets
     *         targets of message.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        return this.broadcastPersonalizedMessage(ChatMessageType.SYSTEM, targets, data);
    }

    /**
     * Try broadcast this message to selected command senders, if message is disabled method will just return false.
     *
     * @param type
     *         type of message to send.
     * @param targets
     *         targets of message.
     * @param data
     *         placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(ChatMessageType type, Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        boolean result = false;
        for (MessageReceiver target : targets)
        {
            result |= this.sendMessage(type, target, data);
        }
        return result;
    }

    /**
     * Helper method that add (if absent) receiver to given map on keys: receiver, player, target and sender
     *
     * @param dataMap
     *         map with message data.
     * @param receiver
     *         receiver to add.
     */
    static void addReceiverToDataMap(Map<? super String, ? super Object> dataMap, MessageReceiver receiver)
    {
        dataMap.putIfAbsent("receiver", receiver);
        dataMap.putIfAbsent("player", receiver);
        dataMap.putIfAbsent("target", receiver);
        dataMap.putIfAbsent("sender", receiver);
    }
}
