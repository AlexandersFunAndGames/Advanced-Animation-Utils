
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public record AdvancedAnimationInstance(AnimationValueConsumer x, AnimationValueConsumer y, AnimationValueConsumer z) {
	
	@FunctionalInterface
	public interface AnimationValueConsumer
	{
	    double accept(Map<String, Float> modifiers);
	}
	
}
