package org.diorite.message.loader;

import javax.annotation.Nullable;
import javax.script.ScriptException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.CharUtils;
import org.yaml.snakeyaml.Yaml;

import org.diorite.chat.ChatMessage;
import org.diorite.commons.math.DioriteMathUtils;
import org.diorite.message.Messages;
import org.diorite.message.MessagesController;
import org.diorite.message.holders.MessageHolder;
import org.diorite.message.holders.RandomMessageHolder;
import org.diorite.message.holders.SingleMessageHolder;
import org.diorite.message.holders.WeightedRandomMessageHolder;
import org.diorite.message.messages.DynamicMessage;
import org.diorite.message.messages.LocalizedMessage;
import org.diorite.message.messages.Message;
import org.diorite.message.messages.StaticMessage;

import groovy.lang.Closure;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

@SuppressWarnings("unchecked")
public class YamlMessageLoader implements LocaleMessagesLoader
{
    private final MessagesController controller;

    public YamlMessageLoader(MessagesController controller)
    {
        this.controller = controller;
    }

    @Override
    public String getName()
    {
        return "yaml";
    }

    @Override
    public String getExtension()
    {
        return "yml";
    }

    @Override
    public void load(Messages messages, Locale locale, Reader reader) throws IOException
    {
        Yaml yaml = new Yaml();
        Map<?, ?> map = yaml.loadAs(reader, LinkedHashMap.class);
        if (map == null)
        {
            return;
        }
        this.load(messages, locale, map);
    }

    private void load(Messages messages, Locale locale, Map<?, ?> map)
    {
        for (Entry<?, ?> entry : map.entrySet())
        {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof Map)
            {
                Map<?, ?> v = (Map<?, ?>) value;
                MessageHolder<? extends LocalizedMessage> messageHolder = this.loadHolder(messages, locale, key, v);
                if (messageHolder != null)
                {
                    messages.addMessage(key, messageHolder);
                    continue;
                }
                Messages section = messages.getSection(key);
                this.load(section, locale, v);
            }
            else if (value instanceof Collection)
            {
                MessageHolder<? extends LocalizedMessage> messageHolder = this.loadHolder(messages, locale, key, (Collection<?>) value);
                messages.addMessage(key, messageHolder);
            }
            else
            {
                MessageHolder<? extends LocalizedMessage> messageHolder =
                    (MessageHolder<? extends LocalizedMessage>) this.loadHolder(messages, locale, key, value.toString());
                messages.addMessage(key, messageHolder);
            }
        }
    }

    private <T extends Message> MessageHolder<T> loadHolder(Messages messages, Locale locale, String key, Object value)
    {
        if (value instanceof Map)
        {
            MessageHolder<T> messageHolder = this.loadHolder(messages, locale, key, (Map<?, ?>) value);
            if (messageHolder == null)
            {
                throw new IllegalStateException("Can't load chance map at: " + messages.getAbsolutePath(key) + ", map: " + value);
            }
            return messageHolder;
        }
        else if (value instanceof Collection)
        {
            return this.loadHolder(messages, locale, key, (Collection<?>) value);
        }
        else
        {
            return (MessageHolder<T>) this.loadHolder(messages, locale, key, value.toString());
        }
    }

    @Nullable
    private <T extends Message> MessageHolder<T> loadHolder(Messages messages, Locale locale, String key, Map<?, ?> map)
    {
        // TODO: implement selector message
        Double2ObjectMap<Object> chanceMap = new Double2ObjectOpenHashMap<>(map.size());
        for (Entry<?, ?> entry : map.entrySet())
        {
            Double chance = DioriteMathUtils.asDouble(entry.getKey().toString());
            if (chance == null)
            {
                chanceMap = null;
                break;
            }
            chanceMap.put(chance, entry.getValue());
        }
        if (chanceMap != null)
        {
            Object2DoubleMap<MessageHolder<T>> weightedChances = new Object2DoubleOpenHashMap<>();
            for (Double2ObjectMap.Entry<Object> entry : chanceMap.double2ObjectEntrySet())
            {
                double chance = entry.getDoubleKey();
                MessageHolder<T> messageHolder = this.loadHolder(messages, locale, key, entry.getValue());
                weightedChances.put(messageHolder, chance);
            }
            return new WeightedRandomMessageHolder<>(weightedChances);
        }
        return null;
//        throw new IllegalStateException("Can't load chance map at: " + messages.getAbsolutePath(key) + ", map: " + map);
    }

    private <T extends Message> MessageHolder<T> loadHolder(Messages messages, Locale locale, String key, Collection<?> list)
    {
        Collection<MessageHolder<T>> holders = new ArrayList<>();
        for (Object o : list)
        {
            holders.add(this.loadHolder(messages, locale, key, o));
        }
        return RandomMessageHolder.fromHolders(holders);
    }

    private MessageHolder<?> loadHolder(Messages messages, Locale locale, String key, String value)
    {
        String absolutePath = messages.getAbsolutePath(key);
        if (! this.mightBeInterpolated(value))
        {
            return new SingleMessageHolder<>(new StaticMessage(absolutePath, locale, ChatMessage.parse(value)));
        }
        try
        {
            Closure<?> interpolatedClosure = this.controller.createInterpolatedClosure(value);
            return new SingleMessageHolder<>(new DynamicMessage(absolutePath, locale, interpolatedClosure));
        }
        catch (ScriptException e)
        {
            throw new IllegalStateException("Can't load message at '" + locale.toLanguageTag() + "'/'" + absolutePath + "', message: " + value, e);
        }
    }

    private boolean mightBeInterpolated(String value)
    {
        int indexOf = value.indexOf('$');
        if ((indexOf == - 1) || (indexOf == (value.length() - 1)))
        {
            return false;
        }
        if (value.contains("${") && value.contains("}"))
        {
            return true;
        }
        return (CharUtils.isAsciiAlpha(value.charAt(indexOf + 1)));
    }
}
