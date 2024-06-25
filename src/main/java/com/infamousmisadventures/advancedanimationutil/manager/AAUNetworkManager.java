package com.infamousmisadventures.advancedanimationutil.manager;

import com.infamousmisadventures.advancedanimationutil.AdvancedAnimationUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class AAUNetworkManager {
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(AdvancedAnimationUtil.prefix("messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();
    private static int PACKET_ID = 0;

    @SubscribeEvent
    private static void registerPackets(FMLCommonSetupEvent event) {
        registerC2SPackets();
        registerS2CPackets();
    }

    private static void registerC2SPackets() {
    }

    private static void registerS2CPackets() {
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToPlayersNearby(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), message);
    }

    public static <MSG> void sendToPlayersNearbyAndSelf(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), message);
    }

    public static <MSG> void sendToAll(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
