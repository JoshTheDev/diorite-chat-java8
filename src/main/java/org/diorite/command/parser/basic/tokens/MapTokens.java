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

package org.diorite.command.parser.basic.tokens;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.diorite.command.annotation.arguments.MapArg;

/**
 * Implementation of {@link MapArg.Tokens}
 */
public final class MapTokens implements MapArg.Tokens
{
    private final char[] start;
    private final char[] end;
    private final char[] separator;
    private final char[] entrySeparator;

    public MapTokens(char[] start, char[] end, char[] separator, char[] entrySeparator)
    {
        this.start = start;
        this.end = end;
        this.separator = separator;
        this.entrySeparator = entrySeparator;
    }

    @Override
    public char[] start()
    {
        return this.start;
    }

    @Override
    public char[] end()
    {
        return this.end;
    }

    @Override
    public char[] separator()
    {
        return this.separator;
    }

    @Override
    public char[] entrySeparator()
    {
        return this.entrySeparator;
    }

    @Override
    public boolean equals(@Nullable Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (! (o instanceof MapTokens))
        {
            return false;
        }
        MapTokens that = (MapTokens) o;
        return new EqualsBuilder().append(this.start, that.start).append(this.end, that.end).append(this.separator, that.separator)
                                  .append(this.entrySeparator, that.entrySeparator).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(this.start).append(this.end).append(this.separator).append(this.entrySeparator).toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("start", this.start).append("end", this.end).append("separator", this.separator)
                                        .append("entrySeparator", this.entrySeparator).toString();
    }

    @Override
    public Class<MapArg.Tokens> annotationType()
    {
        return MapArg.Tokens.class;
    }
}
