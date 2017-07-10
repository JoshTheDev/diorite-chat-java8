package org.diorite;

import javax.annotation.Nullable;

public final class DioriteAPIHolder
{
    @Nullable
    static DioriteAPIBridge instance;

    private DioriteAPIHolder() {}

    public static DioriteAPIBridge getInstance()
    {
        if (instance == null)
        {
            throw new IllegalStateException("API not set yet!");
        }
        return instance;
    }

    public static void setInstance(DioriteAPIBridge instance)
    {
        DioriteAPIHolder.instance = instance;
    }
}
