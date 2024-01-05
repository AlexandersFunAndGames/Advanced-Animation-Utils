package advanced_animation_utils.animation_utils.animation_trackers;

import java.util.function.Supplier;

import advanced_animation_utils.animation_utils.animation_trackers.entity.AdvancedAnimatableEntity;
import advanced_animation_utils.animation_utils.animation_trackers.entity.EntityAdvancedAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class SyncAdvancedAnimationToClient {

		private final TargetType targetType;
	    private final int targetID;
	    private final String animationName;
	    private final AdvancedAnimation animation;
	    private final FriendlyByteBuf readBuf;
	    
	    public SyncAdvancedAnimationToClient(AdvancedAnimation animation, TargetType targetType, int targetID) {
	    	this.targetID = targetID;
	    	this.animationName = animation.getName();
	    	this.animation = animation;
	    	this.targetType = targetType;
	    	readBuf = null;
	    }

	    public SyncAdvancedAnimationToClient(FriendlyByteBuf buf) {
	    	targetID = buf.readInt();
	    	animationName = buf.readUtf();
	    	targetType = TargetType.byOrdinal(buf.readInt());
	    	animation = null;
	    	readBuf = buf;
	    }

	    public void toBytes(FriendlyByteBuf buf) {
	    	buf.writeInt(targetID);
	    	buf.writeUtf(animationName);
	    	buf.writeInt(targetType.ordinal());
	    	animation.write(buf);
	    }

		public boolean handle(Supplier<NetworkEvent.Context> supplier) {
			NetworkEvent.Context ctx = supplier.get();
			ctx.enqueueWork(() -> {
				if (targetType == TargetType.ENTITY) {
					Entity entity = Minecraft.getInstance().player.level.getEntity(targetID);
					if (entity instanceof AdvancedAnimatableEntity animatable) {
						AdvancedAnimation animation = animatable.getAnimationTracker().getAnimation(animationName);
						if (animation instanceof EntityAdvancedAnimation advancedAnimation) {
							advancedAnimation.read(readBuf);
						}
					}
				} else if (targetType == TargetType.BLOCK_ENTITY) {
					/*Entity entity = Minecraft.getInstance().player.level.getEntity(targetID);
					if (entity instanceof AdvancedAnimatableEntity animatable) {
						AdvancedAnimation animation = animatable.getAnimationTracker().getAnimation(animationName);
						if (animation instanceof EntityAdvancedAnimation advancedAnimation) {
							advancedAnimation.read(readBuf);
						}
					}*/
				} else if (targetType == TargetType.ITEM) {
					
				}
			});
			return true;
		}
		
		public enum TargetType {
			ENTITY,
			BLOCK_ENTITY,
			ITEM;
			
			public static TargetType byOrdinal(int ordinal) {
				for (TargetType type : TargetType.values()) {
					if (type.ordinal() == ordinal) {
						return type;
					}
				}
				return null;
			}
		}
	}