public class AdvancedAnimationTracker {

	public Map<String, AdvancedAnimation> animations = Maps.newHashMap();
	public UUID id;
	
	public AdvancedAnimation getAnimation(String name) {
		return animations.get(name);
	}
	
	public void addAnimation(String name, AdvancedAnimation newAnimation) {
		newAnimation.setName(name);
		newAnimation.setTracker(this);
		animations.put(name, newAnimation);
	}
}
