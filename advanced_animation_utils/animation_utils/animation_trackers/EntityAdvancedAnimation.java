package advanced_animation_utils.animation_utils.animation_trackers;

import advanced_animation_utils.animation_utils.AdvancedAnimationPacketHandler;
import net.minecraft.world.entity.Entity;

public class EntityAdvancedAnimation extends AbstractAdvancedAnimation {

	public Entity entity;	
	
	public EntityAdvancedAnimation(Entity entity) {
		this.entity = entity;
	}			
	
	public void syncToClient() {
		if (!this.entity.level.isClientSide) {
			AdvancedAnimationPacketHandler.sendToAllPlayers(new SyncAdvancedAnimationToClient(this, this.entity.getId()));
		}
	}	
}
