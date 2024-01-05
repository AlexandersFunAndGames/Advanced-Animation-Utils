package advanced_animation_utils.animation_utils.model_animations;

import java.util.Map;

import advanced_animation_utils.animation_utils.model_animations.AdvancedAnimationInstance.AnimationValueConsumer;

public record AdvancedAnimationInstance(AnimationValueConsumer x, AnimationValueConsumer y, AnimationValueConsumer z) {
	
	@FunctionalInterface
	public interface AnimationValueConsumer
	{
	    double accept(Map<String, Float> modifiers);
	}
	
}
