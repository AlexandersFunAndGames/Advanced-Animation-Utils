package advanced_animation_utils.animation_utils.animation_trackers;

import java.util.Map;

import com.google.common.collect.Maps;

import advanced_animation_utils.animation_utils.model_animations.AdvancedAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;

public abstract class AbstractAdvancedAnimation implements AdvancedAnimation {

	public AnimationState state = new AnimationState();
	public float animationTime;
	public float animationLength;
	public float transitionSpeed;
	public float amount = 0;
	public float amountO = 0;
	public boolean looping;
	public int tickCount = 0;
	public AdvancedAnimationTracker tracker;
	public String name;	
	public Map<String, Float> modifiers = Maps.newHashMap();
	
	public boolean isProgressAt(float progress) {		
		return Mth.abs(progress - progress()) <= 0.049F;
	}
	
	public float progress() {
		return animationLength - animationTime;
	}
	
	public void start(float animationLength, int transitionSpeed) {
		this.animationLength = animationLength;
		this.animationTime = animationLength;
		this.transitionSpeed = (float)transitionSpeed / 20.0F;
		syncToClient();
	}
		
	public void startLooping(int transitionSpeed) {
		looping = true;
		this.transitionSpeed = (float)transitionSpeed / 20.0F;
		syncToClient();
	}
	
	public void stop(int transitionSpeed) {
		this.animationLength = 0;
		this.animationTime = 0;
		this.transitionSpeed = (float)transitionSpeed / 20.0F;
		looping = false;
		syncToClient();
	}
	
	@Override
	public void tick() {
		tickCount ++;
		amountO = amount;
		if (looping || animationTime > 0 || amount > 0) {
			animationTime -= 0.05F;
			state.startIfStopped(tickCount);
			
			if (looping || animationTime > 0) {
				amount = Mth.clamp(amount + transitionSpeed, 0, 1);
			} else {
				amount = Mth.clamp(amount - transitionSpeed, 0, 1);
			}
			
		} else {
			animationTime = 0;
			state.stop();	
		}	
	}
	
	@Override
	public void updateModifiers() {
		modifiers.put("life_time", AdvancedAnimator.getTick(tickCount));
		modifiers.put("anim_time", AdvancedAnimator.getTick(progress() * 20));
	}
	
	@Override
	public Map<String, Float> getModifiers() {
		return modifiers;
	}
	
	public float lerpAmount() {
		return Mth.lerp(Minecraft.getInstance().getPartialTick(), amountO, amount);
	}

	@Override
	public AdvancedAnimationTracker getTracker() {
		return tracker;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setTracker(AdvancedAnimationTracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeFloat(animationTime);
		buf.writeFloat(animationLength);
		buf.writeFloat(transitionSpeed);
		buf.writeFloat(amount);
		buf.writeFloat(amountO);
		buf.writeBoolean(looping);
	}
	
	public void read(FriendlyByteBuf buf) {
		animationTime = buf.readFloat();
		animationLength = buf.readFloat();
		transitionSpeed = buf.readFloat();
		amount = buf.readFloat();
		amountO = buf.readFloat();
		looping = buf.readBoolean();
	}
}
