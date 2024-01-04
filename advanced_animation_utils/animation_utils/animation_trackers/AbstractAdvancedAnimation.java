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
	
	public boolean isProgressAt(float progress) {		
		return Mth.abs(progress - (animationLength - animationTime)) <= 0.05F;
	}
	
	public void start(float animationLength, float transitionSpeed) {
		this.animationLength = animationLength;
		this.animationTime = animationLength;
		this.transitionSpeed = transitionSpeed;
		syncToClient();
	}
	
	public void stop() {
		this.animationLength = 0;
		this.animationTime = 0;
		syncToClient();
	}
	
	public void startLooping(float transitionSpeed) {
		looping = true;
		this.transitionSpeed = transitionSpeed;
		syncToClient();
	}
	
	public void stopLooping() {
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
