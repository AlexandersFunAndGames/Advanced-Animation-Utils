package com.infamousmisadventures.advancedanimationutil.manager;

import com.infamousmisadventures.advancedanimationutil.client.events.AAUClientMiscEvents;
import com.infamousmisadventures.advancedanimationutil.client.events.AAUClientSetupEvents;
import com.infamousmisadventures.advancedanimationutil.common.events.AAUCommonMiscEvents;
import com.infamousmisadventures.advancedanimationutil.common.events.AAUCommonSetupEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class AAUEventManager {

    static void registerEvents(IEventBus modBus, IEventBus forgeBus) {
        registerClientEvents(modBus, forgeBus);
        registerCommonEvents(modBus, forgeBus);
        registerServerEvents(modBus, forgeBus);
    }

    private static void registerClientEvents(IEventBus modBus, IEventBus forgeBus) {
        if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
            modBus.register(AAUClientSetupEvents.class);

            forgeBus.register(AAUClientMiscEvents.class);
        }
    }

    private static void registerCommonEvents(IEventBus modBus, IEventBus forgeBus) {
        modBus.register(AAUCommonSetupEvents.ModSetupEvents.class);
        modBus.register(AAUNetworkManager.class);

        forgeBus.register(AAUCommonSetupEvents.ForgeSetupEvents.class);
        forgeBus.register(AAUCommonMiscEvents.class);
    }

    private static void registerServerEvents(IEventBus modBus, IEventBus forgeBus) {
        if (FMLEnvironment.dist.equals(Dist.DEDICATED_SERVER)) {

        }
    }
}
