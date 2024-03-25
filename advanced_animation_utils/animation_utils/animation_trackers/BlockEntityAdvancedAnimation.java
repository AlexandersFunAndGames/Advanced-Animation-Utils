package advanced_animation_utils.animation_utils.animation_trackers;

import advanced_animation_utils.animation_utils.AdvancedAnimationPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityAdvancedAnimation extends AbstractAdvancedAnimation {

	public BlockEntity blockEntity;	
	
	public BlockEntityAdvancedAnimation(BlockEntity blockEntity, boolean holdOnLastFrame) {
		super(holdOnLastFrame);
		this.blockEntity = blockEntity;
	}			
	
	public void syncToClient() {
		if (serverSide()) {
			AdvancedAnimationPacketHandler.sendToAllPlayers(new SyncAdvancedAnimationToClient(this, this.blockEntity.getBlockPos()));
		}
	}

	@Override
	public boolean serverSide() {
		return !this.blockEntity.getLevel().isClientSide;
	}
}
