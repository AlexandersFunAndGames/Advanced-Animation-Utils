package advanced_animation_utils.animation_utils.animation_trackers;

import java.util.Map;

import com.google.common.collect.Maps;

import advanced_animation_utils.animation_utils.AdvancedAnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;

public abstract class AbstractAdvancedAnimation implements AdvancedAnimation {

	protected AnimationState state = new AnimationState();
	protected float animationTime;
	protected float loopTime;
	protected float animationLength;
	protected int transitionTicks;
	protected float amount = 0;
	protected float amountO = 0;
	protected boolean looping;
	protected int tickCount = 0;
	protected AdvancedAnimationTracker tracker;
	protected String name;	
	protected Map<String, Float> modifiers = Maps.newHashMap();
	
	public boolean isProgressAt(float progress) {		
		return Mth.abs(progress - progress()) <= 0.049F;
	}
	
	public float progress() {
		return looping ? loopTime : animationLength - animationTime;
	}
	
	public void start(float animationLength, int transitionTicks) {
		if (!isPlaying()) {
			this.animationLength = animationLength;
			this.animationTime = animationLength;
			this.transitionTicks = transitionTicks;
			syncToClient();
		}
	}
		
	public void startLooping(int transitionTicks) {
		if (!isPlaying()) {
			looping = true;
			loopTime = 0;
			this.transitionTicks = transitionTicks;
			syncToClient();
		}
	}
	
	public void stop(int transitionTicks) {
		if (isPlaying()) {
			this.animationLength = 0;
			this.animationTime = 0;
			this.transitionTicks = transitionTicks;
			looping = false;
			syncToClient();
		}
	}
	
	public boolean isPlaying() {
		return progress() > 0 && isLooping();
	}
	
	public boolean isLooping() {
		return looping;
	}
	
	public float getLength() {
		return animationLength;
	}
	
	public float getTransitionTicks() {
		return transitionTicks;
	}
	
	public void setLength(float value) {
		animationLength = value; 
		animationTime = value;
		looping = false;
	}
	
	public void setTransitionTicks(float value) {
		transitionTicks = Math.max(transitionTicks, 1);
	}
	
	@Override
	public void tick() {
		tickCount ++;
		amountO = amount;
		if (looping || animationTime > 0 || amount > 0) {
			animationTime -= 0.05F;
			loopTime ++;
			state.startIfStopped(tickCount);
			
			if (looping || animationTime > 0) {
				amount = transitionTicks == 0 ? 1 : Mth.clamp(amount + (1.0F / (float)transitionTicks), 0, 1);
			} else {
				amount =transitionTicks == 0 ? 0 :  Mth.clamp(amount - (1.0F / (float)transitionTicks), 0, 1);
			}
			
		} else {
			animationTime = 0;
			state.stop();	
		}	
	}
	
	@Override
	public void clientUpdate() {
		Minecraft minecraft = Minecraft.getInstance();
		modifiers.put("life_time", AdvancedAnimationUtils.getTick(tickCount));
		modifiers.put("anim_time", AdvancedAnimationUtils.getTick(progress()));
		modifiers.put("actor_count", (float)minecraft.level.getEntityCount());
		modifiers.put("time_of_day", ((float)minecraft.level.getDayTime() / 24000));
		modifiers.put("moon_phase", (float)minecraft.level.getMoonPhase());
	}
	
	@Override
	public Map<String, Float> getModifiers() {
		return modifiers;
	}
	
	public AnimationState getState() {
		return state;
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
		buf.writeInt(transitionTicks);
		buf.writeFloat(amount);
		buf.writeFloat(amountO);
		buf.writeBoolean(looping);
	}
	
	public void read(FriendlyByteBuf buf) {
		animationTime = buf.readFloat();
		animationLength = buf.readFloat();
		transitionTicks = buf.readInt();
		amount = buf.readFloat();
		amountO = buf.readFloat();
		looping = buf.readBoolean();
	}
}
