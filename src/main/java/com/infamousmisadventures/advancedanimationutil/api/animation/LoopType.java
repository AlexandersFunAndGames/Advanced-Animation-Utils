package com.infamousmisadventures.advancedanimationutil.api.animation;

import it.unimi.dsi.fastutil.Function;

/**
 * An interface which serves as an extensibility point for animation loop types, allowing developers to define custom looping functions.
 */
public interface LoopType {

    /**
     * The canonical name of this loop type instance.
     *
     * @return The canonical name of this loop type.
     */
    String getName();

    /**
     * Whether this loop type should skip animation transitions entirely (regardless of the type of transition happening at any given moment - new animation, no animation, looping, etc.)
     *
     * @return Whether this loop type should skip animation transitions.
     */
    boolean shouldSnap();

    /**
     * The looping function used to apply looping behaviour to a given {@link AdvancedAnimation}.
     *
     * @return The looping function.
     */
    Function<AdvancedAnimation, AdvancedLoopType> getLoopingFunction();
}
