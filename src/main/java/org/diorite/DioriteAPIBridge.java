package org.diorite;

import java.util.Collection;

import org.diorite.chat.MessageReceiver;

/**
 * Bridge between implementation and API, used to provide methods like to fetch all available MessageReceivers etc.
 */
public interface DioriteAPIBridge
{
    static DioriteAPIBridge getAPIBridge()
    {
        return DioriteAPIHolder.getInstance();
    }

    static void getAPIBridge(DioriteAPIBridge api)
    {
        DioriteAPIHolder.setInstance(api);
    }

    /**
     * Returns all available message receivers, like all players in bukkit api.
     *
     * @return all available message receivers.
     */
    Collection<? extends MessageReceiver> getAllReceivers();
}
