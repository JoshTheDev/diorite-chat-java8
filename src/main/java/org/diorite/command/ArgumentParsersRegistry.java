package org.diorite.command;

import javax.annotation.Nullable;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.diorite.command.parser.TypeParser;
import org.diorite.command.parser.basic.ArrayParser;
import org.diorite.command.parser.basic.DoubleParser;
import org.diorite.command.parser.basic.IntegerParser;
import org.diorite.command.parser.basic.StringParser;
import org.diorite.commons.arrays.DioriteArrayUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ArgumentParsersRegistry
{
    // g(@Sender CommandSender sender, double value, int[] valueInts, Collection<Double> doubleCollection, String string)
    private final Collection<Function<Type, TypeParser<?>>>          parsers           = Collections.synchronizedList(new ArrayList<>());
    private final Collection<Function<Parameter, TypeParser<?>>>     paramParsers      = Collections.synchronizedList(new ArrayList<>());
    private final Map<Type, Function<Type, TypeParser<?>>>           parsersCache      = new ConcurrentHashMap<>();
    private final Map<Parameter, Function<Parameter, TypeParser<?>>> paramParsersCache = new ConcurrentHashMap<>();

    {
        // TODO: add more parser, this is very dummy configuration just to support example
        this.registerParser(int.class, IntegerParser.DECIMAL);
        this.registerParser(double.class, DoubleParser.INSTANCE);
        this.registerParser(Integer.class, IntegerParser.DECIMAL);
        this.registerParser(Double.class, DoubleParser.INSTANCE);
        this.registerParser(String.class, new StringParser());
        this.parsers.add(t -> {
            if (! int[].class.equals(t))
            {
                return null;
            }
            ArrayParser<Integer> arrayParser = new ArrayParser<>(IntegerParser.DECIMAL);
            return (context, endPredicate) -> arrayParser.checkAndParse(context, endPredicate)
                                                  .map(col -> DioriteArrayUtils.toIntArray(col.toArray(new Integer[col.size()])));
        });
        this.parsers.add(t -> {
            if (! (t instanceof ParameterizedType))
            {
                return null;
            }
            ParameterizedType c = (ParameterizedType) t;
            Type collectionType = this.getCollectionType(c);
            if (collectionType == null)
            {
                return null;
            }
            TypeParser elementParser = this.parserFor(collectionType);
            if (elementParser == null)
            {
                return null;
            }
            return new ArrayParser<>(elementParser);
        });
        this.parsers.add(t -> {
            if (! (t instanceof Class))
            {
                return null;
            }
            Class c = (Class) t;
            if (! c.isArray())
            {
                return null;
            }
            TypeParser elementParser = this.parserFor(c.getComponentType());
            if (elementParser == null)
            {
                return null;
            }
            ArrayParser<?> arrayParser = new ArrayParser<>(elementParser);
            return (context, endPredicate) ->
                       arrayParser.checkAndParse(context, endPredicate).map(r -> r.toArray((Object[]) DioriteArrayUtils.newArrayByArrayClass(c, r.size())));
        });
    }

    @Nullable
    private Type getCollectionType(Type genericType)
    {
        if (genericType instanceof ParameterizedType)
        {
            Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            while (true)
            {
                if (type instanceof WildcardType)
                {
                    Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                    type = (upperBounds.length == 0) ? null : upperBounds[0];
                }
                if (type instanceof ParameterizedType)
                {
                    return type;
                }
                if (type instanceof Class)
                {
                    return type;
                }
            }
        }
        return null;
    }

    public <T> void registerParser(Class<T> type, TypeParser<T> parser)
    {
        this.parsers.add(t -> type.equals(t) ? parser : null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> TypeParser<T> parserFor(Type type)
    {
        Function<Type, TypeParser<?>> cached = this.parsersCache.get(type);
        if (cached != null)
        {
            return (TypeParser<T>) cached.apply(type);
        }
        synchronized (this.parsers)
        {
            for (Function<Type, TypeParser<?>> paramParser : this.parsers)
            {
                TypeParser<?> parser = paramParser.apply(type);
                if (parser == null)
                {
                    continue;
                }
                this.parsersCache.put(type, paramParser);
                return (TypeParser<T>) parser;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> TypeParser<T> parserFor(Parameter parameter)
    {
        Type type = parameter.getParameterizedType();
        Function<Parameter, TypeParser<?>> cached = this.paramParsersCache.get(parameter);
        if (cached != null)
        {
            return (TypeParser<T>) cached.apply(parameter);
        }
        synchronized (this.paramParsers)
        {
            for (Function<Parameter, TypeParser<?>> paramParser : this.paramParsers)
            {
                TypeParser<?> parser = paramParser.apply(parameter);
                if (parser == null)
                {
                    continue;
                }
                this.paramParsersCache.put(parameter, paramParser);
                return (TypeParser<T>) parser;
            }
        }
        return this.parserFor(type);
    }

    public <T> ArgumentBuilder<T> of(Parameter type)
    {
        TypeParser<T> parser = this.parserFor(type);
        if (parser == null)
        {
            throw new IllegalStateException("Missing parser for: " + type);
        }
        ArgumentBuilder<T> argumentBuilder = new ArgumentBuilder<>(type.getType());
        argumentBuilder.setParser(parser);
        return argumentBuilder;

    }

//    public <T> ArgumentBuilder<T> of(Type type)
//    {
//        ArgumentBuilder<Object> argumentBuilder = new ArgumentBuilder<>(type);
//        return new ArgumentBuilder<>(type);
//    }
}
