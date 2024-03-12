package advanced_animation_utils.animation_utils.animation_registry;

import java.util.List;
import java.util.Map;

import com.mojang.math.Vector3f;

import advanced_animation_utils.animation_utils.animation_registry.AdvancedAnimationRegistration.BoneAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record AdvancedAnimationRegistration(float lengthInSeconds, boolean looping,
		Map<String, List<BoneAnimation>> boneAnimations) {

	@OnlyIn(Dist.CLIENT)
	public record BoneAnimation(Target target, StringKeyframe... keyframes) {

	}

	@OnlyIn(Dist.CLIENT)
	public record StringKeyframe(float timestamp, String xValue, String yValue, String zValue,
			Interpolation interpolation) {
	}

	@OnlyIn(Dist.CLIENT)
	public interface Interpolation {
		Vector3f apply(Vector3f p_232223_, float p_232224_, Vector3f[] p_232225_, int p_232226_, int p_232227_,
				float p_232228_);
	}

	@OnlyIn(Dist.CLIENT)
	public static enum InterpolationType {
		LINEAR("linear", (p_232241_, p_232242_, p_232243_, p_232244_, p_232245_, p_232246_) -> {
			Vector3f vector3f = p_232243_[p_232244_];
			Vector3f vector3f1 = p_232243_[p_232245_];
			p_232241_.set(
					Mth.lerp(p_232242_, vector3f.x(), vector3f1.x()) * p_232246_, 
					Mth.lerp(p_232242_, vector3f.y(), vector3f1.y()) * p_232246_, 
					Mth.lerp(p_232242_, vector3f.z(), vector3f1.z()) * p_232246_);
			return p_232241_;
		}),
		CATMULLROM("catmullrom", (p_232234_, p_232235_, p_232236_,
				p_232237_, p_232238_, p_232239_) -> {
			Vector3f vector3f = p_232236_[Math.max(0, p_232237_ - 1)];
			Vector3f vector3f1 = p_232236_[p_232237_];
			Vector3f vector3f2 = p_232236_[p_232238_];
			Vector3f vector3f3 = p_232236_[Math.min(p_232236_.length - 1, p_232238_ + 1)];
			p_232234_.set(
					Mth.catmullrom(p_232235_, vector3f.x(), vector3f1.x(), vector3f2.x(), vector3f3.x()) * p_232239_,
					Mth.catmullrom(p_232235_, vector3f.y(), vector3f1.y(), vector3f2.y(), vector3f3.y()) * p_232239_,
					Mth.catmullrom(p_232235_, vector3f.z(), vector3f1.z(), vector3f2.z(), vector3f3.z()) * p_232239_);
			return p_232234_;
		});
		
		public String name;
		public Interpolation interpolation;
		
		InterpolationType(String name, Interpolation interpolation) {
			this.name = name;
			this.interpolation = interpolation;
		}
		
		public static InterpolationType byName(String name) {
			for (InterpolationType type : values()) {
				if (type.name == name) {
					return type;
				}
			}
			return LINEAR;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public interface Target {
		void apply(ModelPart p_232248_, Vector3f p_232249_);
	}

	@OnlyIn(Dist.CLIENT)
	public static enum TargetType {
		POSITION("position", ModelPart::offsetPos),
		ROTATION("rotation", ModelPart::offsetRotation),
		SCALE("scale", ModelPart::offsetScale);
		
		public String name;
		public Target target;
		
		TargetType(String name, Target target) {
			this.name = name;
			this.target = target;
		}
		
		public static TargetType byName(String name) {
			for (TargetType type : values()) {
				if (type.name == name) {
					return type;
				}
			}
			return ROTATION;
		}
	}
}