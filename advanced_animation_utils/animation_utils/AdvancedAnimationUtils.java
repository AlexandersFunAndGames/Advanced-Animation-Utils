package advanced_animation_utils.animation_utils;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import advanced_animation_utils.animation_utils.animation_trackers.AdvancedAnimatable;
import advanced_animation_utils.animation_utils.animation_trackers.AdvancedAnimation;
import advanced_animation_utils.animation_utils.animation_trackers.EntityAdvancedAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AdvancedAnimationUtils {

	public static float getTick(float originalTick) {
		return (((originalTick + Minecraft.getInstance().getFrameTime())) * (Mth.PI / 180)) / 20;
	}
	
	public static void addWalkModifiers(EntityAdvancedAnimation animation, LivingEntity entity, float groundSpeedMultiplier, float walkProgressMultiplier) {
		double xDiff = entity.getX() - entity.xo;
		double yDiff = 0.0D;
		double zDiff = entity.getZ() - entity.zo;
		float distanceTravelled = (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
		animation.getModifiers().put("walk_progress", modifierIfPresent(animation, "walk_progress") + ((distanceTravelled / 1000) * walkProgressMultiplier));
		animation.getModifiers().put("ground_speed", distanceTravelled * groundSpeedMultiplier);
	}
	
	public static void addLegHeightOffsetModifier(LivingEntity entity, Vec3 footPos, int maxSearchRange, String animationName, String modifierName, float lerpSpeed, float minLegHeight, float maxLegHeight, float modelScale) {
		if (entity instanceof AdvancedAnimatable animatable) {
			animatable.getAnimationTracker().getAnimation(animationName).getModifiers().put(modifierName, 
					Mth.lerp(Minecraft.getInstance().getDeltaFrameTime() * lerpSpeed, 
							modifierIfPresent(animatable.getAnimationTracker().getAnimation(animationName), modifierName),
								Mth.clamp(AdvancedAnimationUtils.getLegHeightOffset(entity, footPos, maxSearchRange), minLegHeight, maxLegHeight) / modelScale));
		}
	}
	
	public static float getLegHeightOffset(LivingEntity entity, Vec3 footPos, int maxSearchRange) {
		BlockPos blockpos = new BlockPos(footPos.x, footPos.y, footPos.z);
		boolean setCurrentHeight = false;
		float currentHeight = -maxSearchRange;
		int foundBlocks = 0;
		for (int i = -maxSearchRange; i < maxSearchRange; i++) {
			BlockPos offsetPos = blockpos.offset(0, i - 1, 0);
			if (Mth.abs(i) < Mth.abs(currentHeight) && !entity.level.isEmptyBlock(offsetPos) && !entity.level.getBlockState(offsetPos).getCollisionShape(entity.level, offsetPos).isEmpty() && 
					(entity.level.isEmptyBlock(offsetPos.above()) || entity.level.getBlockState(offsetPos.above()).getCollisionShape(entity.level, offsetPos.above()).isEmpty())) {
				currentHeight = i + (float)entity.level.getBlockState(offsetPos).getCollisionShape(entity.level, offsetPos).max(Direction.Axis.Y) - 1;
				setCurrentHeight = true;
			}
			if (!entity.level.isEmptyBlock(offsetPos) && !entity.level.getBlockState(offsetPos).getCollisionShape(entity.level, offsetPos).isEmpty()) {
				foundBlocks++;
			}
		}
		if (!setCurrentHeight) {
			if (foundBlocks > maxSearchRange) {
				currentHeight = maxSearchRange;
			} else {
				currentHeight = -maxSearchRange;
			}
		}
		return (float) (currentHeight - (entity.getY() - entity.getBlockY()));
	}
	
	public static float modifierIfPresent(AdvancedAnimation animation, String modifier) {
		return animation.getModifiers().containsKey(modifier) ? animation.getModifiers().get(modifier) : 0;
	}
	
	public static Vec3 getWorldPosition(LivingEntity entity, List<ModelPart> parts, float modelScale) {
		return getModelPosition(parts, modelScale).yRot(entity.yBodyRot * (Mth.PI / 180)).add(entity.position());
	}
	
	public static Vec3 getModelPosition(List<ModelPart> parts, float modelScale) {
		Vec3 returnVec = Vec3.ZERO;
		for (ModelPart part : parts) {
			returnVec = returnVec.add(
					new Vec3((double)(part.x / 16.0F) * modelScale, (double)(part.y / 16.0F) * modelScale, (double)(part.z / 16.0F) * modelScale)
					.multiply(part.xScale, part.yScale, part.zScale)
					.zRot(part.xRot)
					.zRot(part.yRot)
					.zRot(part.zRot));
		}
		return returnVec;
	}
	
	public static void translateAndRotateForAllParts(PoseStack stack, List<ModelPart> parts) {
		parts.forEach((part) -> {
			part.translateAndRotate(stack);
		});
	}
	
	public static List<ModelPart> allParts(HierarchicalModel<?> model, String... partNames) {
		List<ModelPart> returnList = Lists.newArrayList();
		
		for (String name : partNames) {
			Optional<ModelPart> part = model.getAnyDescendantWithName(name);
			if (part.isPresent()) {
				returnList.add(part.get());
			}
		}
		
		return returnList;
	}
}
