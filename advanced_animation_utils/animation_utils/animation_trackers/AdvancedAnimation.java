package advanced_animation_utils.animation_utils.animation_trackers;

import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;

public interface AdvancedAnimation {

	public abstract AdvancedAnimationTracker getTracker();
	
	public abstract void setTracker(AdvancedAnimationTracker tracker);
	
	public abstract String getName();
	
	public abstract void setName(String name);
	
	public abstract void tick();
	
	public abstract void write(FriendlyByteBuf buf);
	
	public abstract void read(FriendlyByteBuf buf);
	
	public abstract void syncToClient();
	
	public abstract Map<String, Float> getModifiers();
	
	public abstract void clientUpdate();
}
