package com.infamousmisadventures.advancedanimationutil.api.animation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface AnimationInstance<E extends AdvancedAnimatable> {

    /**
     * The canonical name, as defined in the JSON file containing this animation instance.
     *
     * @return The canonical name of this animation instance, as defined in the JSON holding it.
     */
    String getAnimationName();

    /**
     * The length of this animation instance (as defined in the JSON file containing it), in ms.
     *
     * @return The length of this animation instance, in ms.
     */
    long getAnimationLength();

    /**
     * The progress of this animation in its current tracker, in ms.
     *
     * @return The progress of this animation in ms.
     */
    long getAnimationProgress();

    /**
     * The owner of this animation instance (usually be a {@link BlockEntity} or {@link Entity} subclass/instance by standard, though it's really anything implementing {@link AdvancedAnimatable}).
     *
     * @return The owner of this animation instance.
     */
    E getOwner();
}
