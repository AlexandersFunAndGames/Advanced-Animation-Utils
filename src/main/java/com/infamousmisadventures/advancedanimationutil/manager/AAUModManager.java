package com.infamousmisadventures.advancedanimationutil.manager;

import net.minecraftforge.eventbus.api.IEventBus;

public final class AAUModManager {

    public static void registerAll(IEventBus modBus, IEventBus forgeBus) {
        AAUEventManager.registerEvents(modBus, forgeBus);
    }
}
