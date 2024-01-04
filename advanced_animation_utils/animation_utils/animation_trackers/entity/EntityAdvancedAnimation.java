public class EntityAdvancedAnimation extends AbstractAdvancedAnimation {

	public Entity entity;
	
	public EntityAdvancedAnimation(Entity entity) {
		this.entity = entity;
	}			
	
	public void syncToClient() {
		if (!this.entity.level.isClientSide) {
			AdvancedAnimationPacketHandler.sendToAllPlayers(new SyncAdvancedAnimationToClient(this, SyncAdvancedAnimationToClient.TargetType.ENTITY, this.entity.getId()));
		}
	}
}
