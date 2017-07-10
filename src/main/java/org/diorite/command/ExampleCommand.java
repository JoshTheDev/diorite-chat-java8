package org.diorite.command;

import javax.annotation.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.function.UnaryOperator;

import org.diorite.chat.ChatMessage;
import org.diorite.chat.ChatMessageType;
import org.diorite.chat.ChatService;
import org.diorite.chat.MessageOutput;
import org.diorite.command.annotation.Command;
import org.diorite.command.annotation.arguments.Sender;
import org.diorite.message.Messages;
import org.diorite.message.MessagesController;
import org.diorite.message.loader.MessagesLoader;
import org.diorite.sender.CommandSender;
import org.diorite.sender.ConsoleCommandSender;

public class ExampleCommand implements CommandHolder
{
    public static class DummyConsole implements ConsoleCommandSender
    {
        private final DummyMessageOutput out = new DummyMessageOutput();

        @Override
        public MessageOutput getMessageOutput() { return this.out; }

        @Override
        public void setMessageOutput(MessageOutput messageOutput) { }
    }

    public static class DummyMessageOutput implements MessageOutput
    {
        private @Nullable Locale preferredLocale;

        @Override
        public void addFilter(UnaryOperator<ChatMessage> filter) { }

        @Override
        public boolean removeFilter(Object filter) { return false; }

        @Override
        public void sendMessage(ChatMessageType type, ChatMessage component)
        {
            System.out.println("Got message! Message: " + component);
        }

        @Nullable
        @Override
        public Locale getPreferredLocale()
        {
            return this.preferredLocale;
        }

        @Override
        public void setPreferredLocale(@Nullable Locale locale)
        {
            this.preferredLocale = locale;
        }
    }

    static Messages messages;

    // Proof of concept
    public static void main(String[] args) throws Exception
    {
        ChatService.setInstance((type, messageReceiver, message) -> messageReceiver.getMessageOutput().sendMessage(type, message));

        MessagesController messagesController = new MessagesController();
        MessagesLoader langTest = messagesController.simpleLoader(new File("LangTest"), ExampleCommand.class, "/lang/");
        messages = langTest.load("test", StandardCharsets.UTF_8, Locale.UK, Locale.US);


        ExampleCommand exampleCommand = new ExampleCommand();
        SimpleCommandManager simpleCommandManager = new SimpleCommandManager();
        simpleCommandManager.registerCommandHolder(exampleCommand);

        simpleCommandManager.execute(new DummyConsole(), "something 12.23 1,2,3 1.2,1.3 'more words'");

    }

    @Command
    public void something(@Sender CommandSender sender, double value, int[] valueInts, Collection<Double> doubleCollection, String string)
    {
        System.out.println("value: " + value);
        System.out.println("valueInts: " + Arrays.toString(valueInts));
        System.out.println("doubleCollection: " + doubleCollection);
        System.out.println("string: `" + string + "`");

        // NOTE: json is a bit big, but this is due to not ready chat message simplifier, as I found some bug and didn't want to use broken one.
        sender.getMessageOutput().setPreferredLocale(Locale.UK);
        messages.sendMessage("test", sender);
        sender.getMessageOutput().setPreferredLocale(Locale.US);
        messages.sendMessage("test", sender);

        // yey, different messages!
    }
}
