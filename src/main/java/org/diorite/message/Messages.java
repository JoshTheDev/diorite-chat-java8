package org.diorite.message;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.diorite.DioriteAPIBridge;
import org.diorite.chat.ChatMessage;
import org.diorite.chat.ChatMessageType;
import org.diorite.chat.MessageOutput;
import org.diorite.chat.MessageReceiver;
import org.diorite.commons.objects.Nameable;
import org.diorite.message.data.MessageData;
import org.diorite.message.holders.MessageHolder;
import org.diorite.message.messages.LocalizedMessage;
import org.diorite.message.messages.TranslatableMessage;

/**
 * Represent pack of named messages
 */
public interface Messages extends Nameable
{
    /**
     * Char used to separate path nodes.
     */
    char PATH_SEPARATOR = '.';

    /**
     * Returns collection of supported locales.
     *
     * @return collection of supported locales.
     */
    Collection<? extends Locale> getSupportedLocales();

    /**
     * Returns default locale to use if requested one isn't available.
     *
     * @return default locale.
     */
    Locale getDefaultLocale();

    /**
     * Returns locale of given message receiver.
     *
     * @param receiver
     *     source of preferred locale.
     *
     * @return locale of given message receiver.
     */
    default Locale getLocale(MessageReceiver receiver)
    {
        return this.getLocale(receiver.getMessageOutput());
    }

    /**
     * Returns locale of given message output.
     *
     * @param output
     *     source of preferred locale.
     *
     * @return locale of given message output.
     */
    default Locale getLocale(MessageOutput output)
    {
        Locale locale = output.getPreferredLocale();
        return (locale == null) ? this.getDefaultLocale() : locale;
    }

    /**
     * Returns absolute path of this section, empty string for root.
     *
     * @return absolute path of this section, empty string for root.
     */
    String getSectionPath();

    /**
     * Returns absolute path to given sub-node.
     *
     * @param node
     *     su-node path.
     *
     * @return absolute path to given sub-node.
     */
    default String getAbsolutePath(String node)
    {
        String sectionPath = this.getSectionPath();
        if (sectionPath.isEmpty())
        {
            return node;
        }
        return sectionPath + PATH_SEPARATOR + node;
    }

    /**
     * Returns parent section of this section, null for root.
     *
     * @return parent section of this section, null for root.
     */
    @Nullable
    Messages getParent();

    /**
     * Adds given message on given path.
     *
     * @param path
     *     path of message.
     * @param messageHolder
     *     holder of message.
     */
    void addMessage(String path, MessageHolder<? extends LocalizedMessage> messageHolder);
//
//    /**
//     * Returns section of messages object.
//     *
//     * @param path
//     *     path of section.
//     *
//     * @return section of messages object.
//     */
//    @Nullable
//    Messages getSection(String path);
    /**
     * Returns section of messages object.
     *
     * @param path
     *     path of section.
     *
     * @return section of messages object.
     */
    Messages getSection(String path);

    /**
     * Returns message on given key.
     *
     * @param message
     *     path of message.
     *
     * @return message on given key.
     */
    @Nullable
    TranslatableMessage getMessage(String message);

    /**
     * Returns message on given key, if given language is not available, method will try to get message in default language, if it is also not available then
     * method will return null.
     *
     * @param locale
     *     requested language of message.
     * @param message
     *     key of message.
     *
     * @return message on given key or null.
     */
    @Nullable
    default LocalizedMessage getMessage(Locale locale, String message)
    {
        TranslatableMessage translatableMessage = this.getMessage(message);
        if (translatableMessage == null)
        {
            return null;
        }
        return translatableMessage.get(locale);
    }

    /**
     * Prepare message for given data.
     *
     * @param message
     *     key of message
     * @param data
     *     placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepare(String message, Map<? extends String, ?> data)
    {
        LocalizedMessage localizedMessage = this.getMessage(this.getDefaultLocale(), message);
        if (localizedMessage == null)
        {
            return null;
        }
        return localizedMessage.prepare(data);
    }

    /**
     * Prepare message for given data.
     *
     * @param message
     *     key of message
     * @param receiver
     *     receiver of message, will be added as additional MessageData with "receiver","player","target" and "sender" key if not already present in map
     * @param data
     *     placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepare(String message, MessageReceiver receiver, Map<? extends String, ?> data)
    {
        LocalizedMessage localizedMessage = this.getMessage(this.getLocale(receiver), message);
        if (localizedMessage == null)
        {
            return null;
        }
        return localizedMessage.prepare(data);
    }

    /**
     * Prepare message for given data.
     *
     * @param message
     *     key of message
     * @param data
     *     placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepareWith(String message, MessageData<?>... data)
    {
        LocalizedMessage localizedMessage = this.getMessage(this.getDefaultLocale(), message);
        if (localizedMessage == null)
        {
            return null;
        }
        return localizedMessage.prepareWith(data);
    }

    /**
     * Prepare message for given data.
     *
     * @param message
     *     key of message
     * @param receiver
     *     receiver of message, will be added as additional MessageData with "receiver","player","target" and "sender" key if not already present in map
     * @param data
     *     placeholder objects to use.
     *
     * @return chat message object, null if message isn't enabled or can't be used.
     */
    @Nullable
    default ChatMessage prepareWith(String message, MessageReceiver receiver, MessageData<?>... data)
    {
        LocalizedMessage localizedMessage = this.getMessage(this.getLocale(receiver), message);
        if (localizedMessage == null)
        {
            return null;
        }
        return localizedMessage.prepareWith(receiver, data);
    }

    /**
     * String message, Try send given message to given {@link MessageReceiver}, if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param target
     *     target of message.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send.
     */
    default boolean sendMessage(String message, MessageReceiver target, MessageData<?>... data)
    {
        return this.sendMessage(message, ChatMessageType.SYSTEM, target, data);
    }

    /**
     * Try send given message to given {@link MessageReceiver}, if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param type
     *     type of message to send.
     * @param target
     *     target of message.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send.
     */
    default boolean sendMessage(String message, ChatMessageType type, MessageReceiver target, MessageData<?>... data)
    {
        ChatMessage prepare = this.prepareWith(message, target, data);
        if (prepare == null)
        {
            return false;
        }
        target.sendMessage(type, prepare);
        return true;
    }

    /**
     * Try broadcast given message (to all players, resolving placeholders once), if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(String message, MessageData<?>... data)
    {
        return this.broadcastMessage(message, ChatMessageType.SYSTEM, DioriteAPIBridge.getAPIBridge().getAllReceivers(), data);
    }

    /**
     * Try broadcast given message (to all players, resolving placeholders once), if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param type
     *     type of message to send.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(String message, ChatMessageType type, MessageData<?>... data)
    {
        return this.broadcastMessage(message, type, DioriteAPIBridge.getAPIBridge().getAllReceivers(), data);
    }

    /**
     * Try broadcast given message to selected command senders, if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param targets
     *     targets of message.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(String message, Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        return this.broadcastMessage(message, ChatMessageType.SYSTEM, targets, data);
    }

    /**
     * Try broadcast given message to selected command senders, if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param type
     *     type of message to send.
     * @param targets
     *     targets of message.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastMessage(String message, ChatMessageType type, Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        TranslatableMessage translatableMessage = this.getMessage(message);
        if (translatableMessage == null)
        {
            return false;
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
            ChatMessage chatMessage = chatMessages.computeIfAbsent(locale, lang -> translatableMessage.get(lang).prepareWith(data));
            target.sendMessage(type, chatMessage);
            result = true;
        }
        return result;
    }

    /**
     * Try broadcast given message (to all players), if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(String message, MessageData<?>... data)
    {
        return this.broadcastPersonalizedMessage(message, ChatMessageType.SYSTEM, DioriteAPIBridge.getAPIBridge().getAllReceivers());
    }

    /**
     * Try broadcast given message (to all players), if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param type
     *     type of message to send.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(String message, ChatMessageType type, MessageData<?>... data)
    {
        return this.broadcastPersonalizedMessage(message, ChatMessageType.SYSTEM, DioriteAPIBridge.getAPIBridge().getAllReceivers());
    }

    /**
     * Try broadcast given message to selected command senders, if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param targets
     *     targets of message.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(String message, Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        return this.broadcastPersonalizedMessage(message, ChatMessageType.SYSTEM, targets, data);
    }

    /**
     * Try broadcast given message to selected command senders, if message is disabled method will just return false.
     *
     * @param message
     *     key of message
     * @param type
     *     type of message to send.
     * @param targets
     *     targets of message.
     * @param data
     *     placeholder objects to use.
     *
     * @return true if message was send to at least 1 sender.
     */
    default boolean broadcastPersonalizedMessage(String message, ChatMessageType type, Iterable<? extends MessageReceiver> targets, MessageData<?>... data)
    {
        TranslatableMessage translatableMessage = this.getMessage(message);
        if (translatableMessage == null)
        {
            return false;
        }
        boolean result = false;
        for (MessageReceiver target : targets)
        {
            result |= translatableMessage.sendMessage(type, target, data);
        }
        return result;
    }

    static Messages create(String name, Collection<? extends Locale> locales)
    {
        return new RootMessages(name, locales);
    }

    static Messages create(String name, Collection<? extends Locale> locales, Locale defaultLocale)
    {
        return new RootMessages(name, locales, defaultLocale);
    }
}