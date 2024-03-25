package advanced_animation_utils.animation_utils.animation_trackers;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Maps;

import advanced_animation_utils.animation_utils.AdvancedAnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;

public abstract class AbstractAdvancedAnimation implements AdvancedAnimation {

	protected float animationTime;
	protected float loopTime;
	protected float animationLength;
	protected int transitionTicks;
	protected float amount = 0;
	protected float amountO = 0;
	protected boolean looping;
	public int tickCount = 0;
	protected float temporaryLock = 0;
	protected boolean lock;
	protected AdvancedAnimationTracker tracker;
	protected String name;	
	protected Runnable onFinish;
	protected float progressO = 0;
	protected boolean holdOnLastFrame = false;
	protected Map<String, Float> modifiers = Maps.newHashMap();

	public AbstractAdvancedAnimation(boolean holdOnLastFrame) {
		this.holdOnLastFrame = holdOnLastFrame;
	}
	
	public boolean isProgressAt(float progress) {		
		return Mth.abs(progress - progress()) < 0.025F;
	}
	
	public float progress() {
		return looping ? loopTime : animationLength - animationTime;
	}
	
	public void start(float animationLength, int transitionTicks) {
		if (serverSide() && !isPlaying() && !isLocked()) {
			this.animationLength = animationLength;
			this.animationTime = animationLength;
			this.transitionTicks = transitionTicks;
			this.onFinish = null;
			if (transitionTicks <= 0) {
				amount = 1;
				amountO = amount;
			}
			syncToClient();
		}
	}
	
	public void start(float animationLength, int transitionTicks, Runnable onFinish) {
		if (serverSide() && !isPlaying() && !isLocked()) {
			this.animationLength = animationLength;
			this.animationTime = animationLength;
			this.transitionTicks = transitionTicks;
			this.onFinish = onFinish;
			if (transitionTicks <= 0) {
				amount = 1;
				amountO = amount;
			}
			syncToClient();
		}
	}
		
	public void startLooping(int transitionTicks) {
		if (serverSide() && !isPlaying() && !isLocked()) {
			looping = true;
			loopTime = 0;
			this.transitionTicks = transitionTicks;			
			this.onFinish = null;
			if (transitionTicks <= 0) {
				amount = 1;
				amountO = amount;
			}
			syncToClient();
		}
	}
	
	public void stop(int transitionTicks) {
		if (serverSide() && isPlaying() && !isLocked()) {
			this.animationLength = 0;
			this.animationTime = 0;
			this.transitionTicks = transitionTicks;
			looping = false;
			this.onFinish = null;
			if (transitionTicks <= 0) {
				amount = 0;
				amountO = amount;
			}
			syncToClient();
		}
	}	

	public boolean holdOnLastFrame() {
		return holdOnLastFrame;
	}
	
	public void setHoldOnLastFrame(boolean value) {
		holdOnLastFrame = value;
		syncToClient();
	}
	
	public void lockTemporarily(float lockFor) {
		this.temporaryLock = lockFor;
		syncToClient();
	}
	
	public void lock() {
		this.lock = true;
		syncToClient();
	}
	
	public void unlock() {
		this.temporaryLock = 0;
		this.lock = false;
		syncToClient();
	}
	
	public boolean isLocked() {
		return temporaryLock > 0 || lock;
	}
	
	public boolean isPlaying() {
		return isLooping() || animationTime > 0;
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
		syncToClient();
	}
	
	public void setTransitionTicks(int value) {
		transitionTicks = value;
		syncToClient();
	}
	
	@Override
	public void tick() {
		tickCount ++;
		amountO = amount;
		progressO = progress();
		if (temporaryLock > 0) {
			temporaryLock -= 0.05F;
		}
		if (looping || animationTime > 0 || amount > 0) {
			animationTime -= 0.05F;
			loopTime ++;
			
			if (!isLooping() && isProgressAt(animationLength)) {
				finish();
			}
			
			if (looping || animationTime > 0) {
				if (transitionTicks <= 0) {
					amount = 1;
					amountO = amount;
				} else {
					amount = Mth.clamp(amount + (1.0F / (float)transitionTicks), 0, 1);
				}
			} else {
				if (transitionTicks <= 0) {
					amount = 0;
					amountO = amount;
				} else {
					amount = Mth.clamp(amount - (1.0F / (float)transitionTicks), 0, 1);
				}
			}	
		} else {
			animationTime = 0;
		}			
	}
	
	protected void finish() {
		if (onFinish != null) {
			onFinish.run();
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
	
	public float lerpAmount() {
		return Mth.lerp(Minecraft.getInstance().getPartialTick(), amountO, amount);
	}

	public float lerpProgress() {
		return Mth.lerp(Minecraft.getInstance().getPartialTick(), progressO, progress());
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
		buf.writeFloat(temporaryLock);
		buf.writeBoolean(lock);
		buf.writeBoolean(holdOnLastFrame);
	}
	
	public void read(FriendlyByteBuf buf) {
		animationTime = buf.readFloat();
		animationLength = buf.readFloat();
		transitionTicks = buf.readInt();
		amount = buf.readFloat();
		amountO = buf.readFloat();
		looping = buf.readBoolean();
		temporaryLock = buf.readFloat();
		lock = buf.readBoolean();
		holdOnLastFrame = buf.readBoolean();
	}
	
	public abstract boolean serverSide();
}