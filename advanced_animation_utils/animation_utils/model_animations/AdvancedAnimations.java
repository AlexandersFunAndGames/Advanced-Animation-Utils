package advanced_animation_utils.animation_utils.model_animations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.math.Vector3f;

import advanced_animation_utils.animation_utils.animation_trackers.entity.EntityAdvancedAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedAnimations {
	private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
	
	public static float getTick(float originalTick) {
		return (((originalTick + Minecraft.getInstance().getFrameTime())) * (Mth.PI / 180)) / 20;
	}
	
	public static void animateEntity(HierarchicalModel<?> model, EntityAdvancedAnimation animation, AdvancedAnimationDefinition animationDefinition, float tick) {
		animation.updateModifiers();
		modifiedAmountAnimate(model, animation.state, animationDefinition, tick, animation.lerpAmount(), animation.modifiers);
	}
	
	public static void animate(HierarchicalModel<?> model, AnimationState animationState, AdvancedAnimationDefinition animationDefinition, float tick, Map<String, Float> modifiers) {
		modifiedSpeedAnimate(model, animationState, animationDefinition, tick, 1.0F, modifiers);
	}

	public static void modifiedSpeedAnimate(HierarchicalModel<?> model, AnimationState animationState, AdvancedAnimationDefinition animationDefinition, float tick, float speedMultiplier, Map<String, Float> modifiers) {
		animationState.updateTime(tick, speedMultiplier);
		animationState.ifStarted((state) -> {
			animate(model, animationDefinition, state.getAccumulatedTime(), 1.0F, 1.0F, modifiers, ANIMATION_VECTOR_CACHE);
		});
	}
	   
	public static void modifiedAmountAnimate(HierarchicalModel<?> model, AnimationState animationState, AdvancedAnimationDefinition animationDefinition, float tick, float amountMultiplier, Map<String, Float> modifiers) {
		animationState.updateTime(tick, 1.0F);
		animationState.ifStarted((state) -> {
			animate(model, animationDefinition, state.getAccumulatedTime(), 1.0F, amountMultiplier, modifiers, ANIMATION_VECTOR_CACHE);
		});
	}
	
	public static void modifiedAnimate(HierarchicalModel<?> model, AnimationState animationState, AdvancedAnimationDefinition animationDefinition, float tick, float speedMultiplier, float amountMultiplier, Map<String, Float> modifiers) {
		animationState.updateTime(tick, speedMultiplier);
		animationState.ifStarted((state) -> {
			animate(model, animationDefinition, state.getAccumulatedTime(), 1.0F, amountMultiplier, modifiers, ANIMATION_VECTOR_CACHE);
		});
	}
	
   public static void animate(HierarchicalModel<?> model, AdvancedAnimationDefinition definition, double accumulatedTime, float speedMultiplier, float amountMultiplier, Map<String, Float> modifiers, Vector3f cache) {
      float f = getElapsedSeconds(definition, accumulatedTime);
      
      for(Map.Entry<String, List<AdvancedAnimationChannel>> entry : definition.boneAnimations().entrySet()) {
         Optional<ModelPart> optional = model.getAnyDescendantWithName(entry.getKey());
         List<AdvancedAnimationChannel> list = entry.getValue();
         optional.ifPresent((modelPart) -> {
            list.forEach((channel) -> {
               AdvancedKeyframe[] akeyframe = channel.keyframes();
               int i = Math.max(0, Mth.binarySearch(0, akeyframe.length, (p_232315_) -> {
                  return f <= akeyframe[p_232315_].timestamp();
               }) - 1);
               int j = Math.min(akeyframe.length - 1, i + 1);
               AdvancedKeyframe keyframe = akeyframe[i];
               AdvancedKeyframe keyframe1 = akeyframe[j];
               float f1 = f - keyframe.timestamp();
               float f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
               keyframe1.interpolation().apply(cache, f2, getVectorsForAdvancedKeyframes(akeyframe, channel.target(), modifiers), i, j, speedMultiplier);
               cache.mul(amountMultiplier);
               channel.target().apply(modelPart, cache);
            });
         });
      }

   }

   private static float getElapsedSeconds(AdvancedAnimationDefinition p_232317_, double p_232318_) {
      float f = (float)p_232318_ / 1000.0F;
      return p_232317_.looping() ? f % p_232317_.lengthInSeconds() : f;
   }
   
   public static Vector3f[] getVectorsForAdvancedKeyframes(AdvancedKeyframe[] keyframes, AdvancedAnimationChannel.Target target, Map<String, Float> modifiers) {
	   Vector3f[] value = new Vector3f[keyframes.length];
	   
	   for (int i = 0; i < keyframes.length; i++) {
		   value[i] = getVectorsForAdvancedKeyframe(keyframes[i], target, modifiers);
	   }
	   
	   return value;
   }
   
   public static Vector3f getVectorsForAdvancedKeyframe(AdvancedKeyframe keyframe, AdvancedAnimationChannel.Target target, Map<String, Float> modifiers) {
	   float x = 0;
	   float y = 0;
	   float z = 0;
	   
	   x = (float) keyframe.animationInstance().x().accept(modifiers);
	   y = (float) keyframe.animationInstance().y().accept(modifiers);
	   z = (float) keyframe.animationInstance().z().accept(modifiers);
	   
	   if (target == AdvancedAnimationChannel.Targets.POSITION) {
		   return posVec(x, y, z);
	   } else if (target == AdvancedAnimationChannel.Targets.SCALE) {
		   return scaleVec(x, y, z);
	   } else {
		   return degreeVec(x, y, z);
	   }
   }

   public static Vector3f posVec(float p_232303_, float p_232304_, float p_232305_) {
      return new Vector3f(p_232303_, -p_232304_, p_232305_);
   }

   public static Vector3f degreeVec(float p_232332_, float p_232333_, float p_232334_) {
      return new Vector3f(p_232332_ * ((float)Math.PI / 180F), p_232333_ * ((float)Math.PI / 180F), p_232334_ * ((float)Math.PI / 180F));
   }

   public static Vector3f scaleVec(double p_232299_, double p_232300_, double p_232301_) {
      return new Vector3f((float)(p_232299_ - 1.0D), (float)(p_232300_ - 1.0D), (float)(p_232301_ - 1.0D));
   }
}