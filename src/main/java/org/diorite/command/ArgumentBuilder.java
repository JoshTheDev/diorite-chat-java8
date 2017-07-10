package org.diorite.command;

import javax.annotation.Nullable;

import java.lang.reflect.Type;

import org.apache.commons.lang3.builder.Builder;

import org.diorite.command.parser.ArgumentParseResult;
import org.diorite.command.parser.TypeParser;

public class ArgumentBuilder<T> implements Builder<Argument<T>>
{
    private final     Type          type;
    private @Nullable TypeParser<T> parser;

    ArgumentBuilder(Type type)
    {
        this.type = type;
    }

    public ArgumentBuilder<T> setParser(TypeParser<T> parser)
    {
        this.parser = parser;
        return this;
    }

    @Override
    public Argument<T> build()
    {
        if (this.parser == null)
        {
            throw new IllegalStateException("Parser must be provided!");
        }
        return (context, endConsumer) -> {
            ArgumentParseResult<? extends T> result = this.parser.checkAndParse(context, TypeParser.SPACE_PREDICATE);
            if (result.isSuccess())
            {
                endConsumer.accept(result.getResult());
            }
            return result;
        };
    }
}
