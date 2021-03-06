/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.diorite.sender;

import org.diorite.chat.MessageOutput;
import org.diorite.chat.MessageReceiver;
import org.diorite.message.MessageData;

/**
 * Represent source of command.
 */
public interface CommandSender extends MessageReceiver, MessageData<CommandSender>
{
    /**
     * Returns name of command sender.
     *
     * @return name of command sender.
     */
    String getName();

    /**
     * Returns message output for this sender.
     *
     * @return message output for this sender.
     */
    MessageOutput getMessageOutput();

    /**
     * Sets message output for this command sender. <br>
     * Message output only need implement {@link MessageOutput} interface, it don't need to implement/extends any implementation classes.
     * So you can create own message output without any problems. <br>
     * Message output can't be null, use {@link MessageOutput#IGNORE} instead.
     *
     * @param messageOutput
     *         new message output.
     */
    void setMessageOutput(MessageOutput messageOutput);

    @Override
    default String getMessageKey() {return "sender";}
}
