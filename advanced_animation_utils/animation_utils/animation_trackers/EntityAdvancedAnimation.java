package advanced_animation_utils.animation_utils.animation_trackers;

import advanced_animation_utils.animation_utils.AdvancedAnimationPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EntityAdvancedAnimation extends AbstractAdvancedAnimation {

	public Entity entity;	
	
	public EntityAdvancedAnimation(Entity entity, boolean holdOnLastFrame) {
		super(holdOnLastFrame);
		this.entity = entity;
	}			
	
	public void syncToClient() {
		if (serverSide()) {
			AdvancedAnimationPacketHandler.sendToAllPlayers(new SyncAdvancedAnimationToClient(this, this.entity.getId()));
		}
	}	
	
	@Override
	public void clientUpdate() {
		super.clientUpdate();
		Minecraft minecraft = Minecraft.getInstance();
		modifiers.put("distance_from_camera", (float)minecraft.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));
		modifiers.put("is_on_ground", entity.isOnGround() ? 1F : 0F);
		modifiers.put("is_in_water", entity.isInWater() ? 1F : 0F);
		modifiers.put("is_in_water_or_rain", entity.isInWaterRainOrBubble() ? 1F : 0F);

		if (entity instanceof LivingEntity livingEntity) {
			modifiers.put("health", livingEntity.getHealth());
			modifiers.put("max_health", livingEntity.getMaxHealth());
			modifiers.put("is_on_fire", livingEntity.isOnFire() ? 1F : 0F);
		}
	}

	@Override
	public boolean serverSide() {
		return !this.entity.level.isClientSide;
	}
}
