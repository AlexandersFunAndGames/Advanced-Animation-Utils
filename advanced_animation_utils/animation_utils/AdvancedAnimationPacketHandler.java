package advanced_animation_utils.animation_utils;

import com.alexander.mutantmore.MutantMore;

import advanced_animation_utils.animation_utils.animation_trackers.SyncAdvancedAnimationToClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class AdvancedAnimationPacketHandler {

	public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MutantMore.MOD_ID, "advanced_animation_packets")).clientAcceptedVersions("1"::equals)
			.serverAcceptedVersions("1"::equals).networkProtocolVersion(() -> "1").simpleChannel();

	private static int packetId = 0;

	private static int id() {
		return packetId++;
	}

	public static void register() {
		INSTANCE.messageBuilder(SyncAdvancedAnimationToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
        .decoder(SyncAdvancedAnimationToClient::new)
        .encoder(SyncAdvancedAnimationToClient::toBytes)
        .consumerNetworkThread(SyncAdvancedAnimationToClient::handle)
        .add();
    }

    public static <MSG> void sendToServer(MSG message) {
    	INSTANCE.sendToServer(message);
    }
    
    public static <MSG> void sendToAllPlayers(MSG message) {
    	INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}