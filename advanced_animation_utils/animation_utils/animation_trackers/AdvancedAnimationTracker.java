package advanced_animation_utils.animation_utils.animation_trackers;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

public class AdvancedAnimationTracker {

	public Map<String, AdvancedAnimation> animations = Maps.newHashMap();
	public UUID id;
	
	public AdvancedAnimation getAnimation(String name) {
		return animations.get(name);
	}
	
	public void tick() {
		animations.forEach((name, animation) -> {
			animation.tick();
		});
	}
	
	public void addAnimation(String name, AdvancedAnimation newAnimation) {
		newAnimation.setName(name);
		newAnimation.setTracker(this);
		animations.put(name, newAnimation);
	}
}
