package org.diorite.message.messages;

import java.util.Locale;

public class MessagePrepareException extends RuntimeException
{
    private static final long serialVersionUID = 0;

    private final Message failedMessage;

    public MessagePrepareException(Message failedMessage)
    {
        this.failedMessage = failedMessage;
    }

    public MessagePrepareException(Message failedMessage, Exception cause)
    {
        super(createMessage(failedMessage), cause);
        this.failedMessage = failedMessage;
    }

    public Message getFailedMessage()
    {
        return this.failedMessage;
    }

    private static String createMessage(Message message)
    {
        Locale locale = (message instanceof LocalizedMessage) ? ((LocalizedMessage) message).getLocale() : null;
        String localeStr = (locale == null) ? "UnknownLocale" : locale.toLanguageTag();
        Class<? extends Message> messageClass = message.getClass();
        String className;
        if (messageClass.getName().startsWith(MessagePrepareException.class.getPackage().getName()))
        {
            className = messageClass.getSimpleName();
        }
        else
        {
            className = messageClass.getName();
        }
        String messageName = (message instanceof NamedMessage) ? ((NamedMessage) message).getName() : "MissingName";
        return "Can't prepare message (" + className + ") `" + localeStr + "/" + messageName + "`!";
    }
}
