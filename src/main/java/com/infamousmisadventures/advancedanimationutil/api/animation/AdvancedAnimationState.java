package com.infamousmisadventures.advancedanimationutil.api.animation;

import java.util.function.BiFunction;

/**
 * An enum that represents the current state of a given {@link AnimationInstance} (or any of its subclasses).
 */
public enum AdvancedAnimationState {
    /**
     * The animation is currently playing.
     */
    PLAYING(),
    /**
     * The animation is currently transitioning (either to a new state or back for looping).
     */
    TRANSITIONING(),
    /**
     * The animation is currently paused.
     */
    PAUSED(),
    /**
     * The animation is currently stopped.
     */
    STOPPED();
}
