package advanced_animation_utils.animation_utils.animation_trackers;

import advanced_animation_utils.animation_utils.AdvancedAnimationPacketHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityAdvancedAnimation extends AbstractAdvancedAnimation {

	public BlockEntity blockEntity;	
	
	public BlockEntityAdvancedAnimation(BlockEntity blockEntity) {
		this.blockEntity = blockEntity;
	}			
	
	public void syncToClient() {
		if (!this.blockEntity.getLevel().isClientSide) {
			AdvancedAnimationPacketHandler.sendToAllPlayers(new SyncAdvancedAnimationToClient(this, this.blockEntity.getBlockPos()));
		}
	}	
}
