package advanced_animation_utils.animation_utils.model_animations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.math.Vector3f;

import advanced_animation_utils.animation_utils.animation_trackers.AbstractAdvancedAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedAnimator {
	private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

	public static void animate(List<ModelPart> parts, AbstractAdvancedAnimation animation,
			AdvancedAnimationDefinition definition) {
		animation.clientUpdate();
		if (animation != null && animation.lerpAmount() > 0) {
			float amountMultiplier = animation.lerpAmount();
			Map<String, Float> modifiers = animation.getModifiers();
			Vector3f cache = ANIMATION_VECTOR_CACHE;
			float f = animation.holdOnLastFrame() ? animation.lerpProgress() : animation.lerpProgress() % definition.lengthInSeconds();
			for (Map.Entry<String, List<AdvancedAnimationChannel>> entry : definition.boneAnimations().entrySet()) {
				Optional<ModelPart> optional = getModelPart(parts, entry.getKey());
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
						float f2 = Mth.clamp(keyframe1.timestamp() - keyframe.timestamp() == 0 ? 0 : f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
						keyframe1.interpolation().apply(cache, f2, getVectorsForAdvancedKeyframes(akeyframe, channel.target(), modifiers), i, j, 1);
						cache.mul(amountMultiplier);						
						channel.target().apply(modelPart, cache);
					});
				});
			}
		}
	}

	public static Optional<ModelPart> getModelPart(List<ModelPart> parts, String name) {
		return parts.stream().filter((part) -> {
			return part.hasChild(name);
		}).findFirst().map((part) -> {
			return part.getChild(name);
		});
	}

	public static Vector3f[] getVectorsForAdvancedKeyframes(AdvancedKeyframe[] keyframes,
			AdvancedAnimationChannel.Target target, Map<String, Float> modifiers) {
		Vector3f[] value = new Vector3f[keyframes.length];

		for (int i = 0; i < keyframes.length; i++) {
			value[i] = getVectorsForAdvancedKeyframe(keyframes[i], target, modifiers);
		}

		return value;
	}

	public static Vector3f getVectorsForAdvancedKeyframe(AdvancedKeyframe keyframe,
			AdvancedAnimationChannel.Target target, Map<String, Float> modifiers) {
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
		return new Vector3f(p_232332_ * ((float) Math.PI / 180F), p_232333_ * ((float) Math.PI / 180F),
				p_232334_ * ((float) Math.PI / 180F));
	}

	public static Vector3f scaleVec(double p_232299_, double p_232300_, double p_232301_) {
		return new Vector3f((float) (p_232299_ - 1.0D), (float) (p_232300_ - 1.0D), (float) (p_232301_ - 1.0D));
	}
}