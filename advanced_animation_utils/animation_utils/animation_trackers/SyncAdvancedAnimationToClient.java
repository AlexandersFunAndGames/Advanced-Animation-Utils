package advanced_animation_utils.animation_utils.animation_trackers;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class SyncAdvancedAnimationToClient {

		private final TargetType targetType;
	    private final int targetEntityID;
	    private final BlockPos targetBlockEntityPos;
	    private final String animationName;
	    private final AdvancedAnimation animation;
	    private final FriendlyByteBuf readBuf;
	    
	    public SyncAdvancedAnimationToClient(AdvancedAnimation animation, int targetEntityID) {
	    	this.targetBlockEntityPos = BlockPos.ZERO;
	    	this.targetEntityID = targetEntityID;
	    	this.animationName = animation.getName();
	    	this.animation = animation;
	    	this.targetType = TargetType.ENTITY;
	    	readBuf = null;
	    }
	    
	    public SyncAdvancedAnimationToClient(AdvancedAnimation animation, BlockPos targetBlockEntityPos) {
	    	this.targetBlockEntityPos = targetBlockEntityPos;
	    	this.targetEntityID = 0;
	    	this.animationName = animation.getName();
	    	this.animation = animation;
	    	this.targetType = TargetType.BLOCK_ENTITY;
	    	readBuf = null;
	    }

	    public SyncAdvancedAnimationToClient(FriendlyByteBuf buf) {
	    	targetBlockEntityPos = buf.readBlockPos();
	    	targetEntityID = buf.readInt();
	    	animationName = buf.readUtf();
	    	targetType = TargetType.byOrdinal(buf.readInt());
	    	animation = null;
	    	readBuf = buf;
	    }

	    public void toBytes(FriendlyByteBuf buf) {
	    	buf.writeBlockPos(targetBlockEntityPos);
	    	buf.writeInt(targetEntityID);
	    	buf.writeUtf(animationName);
	    	buf.writeInt(targetType.ordinal());
	    	animation.write(buf);
	    }

		public boolean handle(Supplier<NetworkEvent.Context> supplier) {
			NetworkEvent.Context ctx = supplier.get();
			ctx.enqueueWork(() -> {
				if (targetType == TargetType.ENTITY) {
					Entity entity = Minecraft.getInstance().player.level.getEntity(targetEntityID);
					if (entity instanceof AdvancedAnimatable animatable) {
						AdvancedAnimation animation = animatable.getAnimationTracker().getAnimation(animationName);
						if (animation instanceof EntityAdvancedAnimation advancedAnimation) {
							advancedAnimation.read(readBuf);
						}
					}
				} else if (targetType == TargetType.BLOCK_ENTITY) {
					BlockEntity blockEntity = Minecraft.getInstance().player.level.getBlockEntity(targetBlockEntityPos);
					if (blockEntity instanceof AdvancedAnimatable animatable) {
						AdvancedAnimation animation = animatable.getAnimationTracker().getAnimation(animationName);
						if (animation instanceof BlockEntityAdvancedAnimation advancedAnimation) {
							advancedAnimation.read(readBuf);
						}
					}
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