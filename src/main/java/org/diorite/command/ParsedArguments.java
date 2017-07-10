package org.diorite.command;

public class ParsedArguments
{
    private final Object[] data;

    public ParsedArguments(Object[] data)
    {
        this.data = data.clone();
    }

    public Object get(int i)
    {
        return this.data[i];
    }
}
