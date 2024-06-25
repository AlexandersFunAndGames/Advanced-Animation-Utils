package com.infamousmisadventures.advancedanimationutil.api.animation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The primary handler class for animations playing on any given {@link AdvancedAnimatable}. This class automatically handles siding, syncing data, and other animation-related functionality under the hood. Abstraction methods can be found in
 * {@link AdvancedAnimatable}, wherein common-case methods are provided. The passages below detail the functionality, usage, and extensibility of this class.
 * <p></p>
 * <h2>Functionality</h2>
 * {@code AdvancedAnimationTrackers} (AATs) are the objects responsible for handling the animation states of any given {@link AdvancedAnimatable}. They handle looping, chaining, transitioning, and other operations through the methods of each
 * {@link AnimationInstance}. Their primary purpose is to play, transition, pause, or stop animations while keeping data synced. Generally speaking, the animation instances themselves handle syncing their own data (e.g. progress,
 * length, name, loop type, state, and more), whilst the tracker simply manages that data.
 *<p></p>
 * Like the vanilla keyframe animation system, data such as animation progress and animation length is returned in ms rather than ticks, for maximum precision in tracking. That way, animations can be rendered in the same way they are tracked
 * on the functional client/server (approach inspired by Functional Animation Abstraction Layer (FAAL)).
 * <p></p>
 * <b><u>Animation data can be mutated to some degree when working with AATs. To be precise:</u></b>
 * <ul>
 *     <li>Animation progress can be set to any value and will be handled automatically based on its loop type (see {@link LoopType#getLoopingFunction()}).</li>
 *     <li>Animations can be dynamically paused/reset.</li>
 *     <li>Animation length can be changed, and an option is provided for whether the animation should normalize to fit the new length or if it should simply stop playing at the new length, which if lengthened otherwise, will be treated
 *     as an animation that holds for that specific amount of time (the tracker provides native utilities to handle that, though).</li>
 *     <li>Animation loop types can be changed/overridden, but only when none of the aforementioned mutations are being applied.</li>
 * </ul>
 * The rest of the data is immutable (animation name and other misc. data) and will be handled accordingly.
 * <p></p>
 * AATs support dynamically creating animations at runtime based on existing animations, either caching them or marking them as transient (based on what the end-developer specifies). They do so by copying and mutating data into a new
 * {@link AnimationInstance} (or its appropriate subclass) and handling said new animation accordingly. Transient animations are not stored anywhere besides the queue in which they're set to be played, and will be deleted afterwards. Cached animations are re-usable
 * and can even be written to the original animation JSON for persistence across loads.
 * <p></p>
 * AATs are capable of running multiple animations at once. However, just like GeckoLib, animations that modify the same bones <u>at the same time</u> cause visual chunkiness due to the nature of the animation system and how it works.
 * As such, if an animation is playing and another one modifying the same bones attempts to play at the same time, unless specified by the end-developer (via #shouldAllowFullConcurrency()), the second animation will not play until the first
 * animation is done playing.
 * <p></p>
 * While you can get by having 1 AAT for your {@link AdvancedAnimatable} objects, having multiple AATs still holds value. For instance, despite the fact that AATs are capable of running multiple animations
 * concurrently, they can only have 1 animation chaining sequence run at any given time. Additionally, having too many animations (especially for bosses) attached to the same AAT is comparable to making your Minecraft mod in 1 class: It
 * might be possible, but it is definitely not practical.
 * <p></p>
 * AATs are able to play a specified sequence of animations, more formally known as an Animation Chaining Sequence (ACS). ACSs are defined in the {@link AdvancedAnimationChainer} class, where you can configure your animations to play
 * in whatever order you'd like them to (for more information, see {@link AdvancedAnimationChainer} javadocs). As stated earlier, however, an AAT can only handle 1 ACS at any given time, which means that if you'd like to have multiple
 * ACSs play at once, you'd have to create multiple AATs accordingly. Despite this limitation, singular {@linkplain AdvancedAnimation AdvancedAnimations} (or other {@link AnimationInstance} derivatives not specified here) can still be
 * played at the same time an ACS is playing in a given AAT. However, if a non-ACS animation instance attempts to play the same animation as the current one in a running ACS within the same AAT, it will be queued to play after the ACS is
 * done playing that animation.
 * <p></p>
 * And so, now that we've covered the functionality of AATs on a high level, let's delve into their <b>usage</b>.
 * <p></p>
 * <h2>Usage</h2>
 * At the moment, AATs only support {@linkplain Entity Entities} and {@linkplain BlockEntity BlockEntities}. That being said, the snippets below cover what a typical {@link AdvancedAnimatable} class using AATs should look like. For more
 * comprehensive snippets, refer to the classes listed below under "See Also".
 * <pre>
 *     {@code
 *
 *     // Base class, not extending any of the natively provided AdvancedAnimatable classes.
 *     public class SomeAdvancedAnimatable extends Entity implements AdvancedAnimatable {
 *         //.. (Fields)
 *         private final AdvancedAnimationTracker mainTracker = new AdvancedAnimationTracker(this, "aat_name_following_regex_conventions").setTransitionTicks(3); //... (Other builder methods)
 *         //... (More fields)
 *
 *         public SomeAdvancedAnimatable(EntityType<MyEntity> entityType, Level world) {
 *             super(entityType, world);
 *         }
 *
 *         //... (AdvancedAnimatable methods, or wherever you'd like to place them).
 *
 *         @Override
 *         public void tick() {
 *             tickAnims(tickCount); // Method from AdvancedAnimatable which handles functional tracker-ticking.
 *             super.tick();
 *         }
 *
 *         //... (Other methods)
 *     }
 *
 *     // ... \\
 *
 *     public class SomeBaseGoal extends Goal { // Can be a brain task, or whatever else. This is just an example.
 *         private final SomeAdvancedAnimatable owner;
 *         private final Supplier<AnimationInstance> someAnim; // Cache in a supplier for Memoization:tm:
 *
 *         public SomeBaseGoal(SomeAdvancedAnimatable owner, Supplier<AnimationInstance> someAnim) {
 *             this.owner = owner;
 *             this.someAnim = someAnim;
 *         }
 *
 *
 *     }
 *      }
 * </pre>
 *
 * @see AdvancedAnimatable
 * @see AnimationInstance
 * @see AdvancedAnimation
 * @see AdvancedAnimationChainer
 */
public class AdvancedAnimationTracker {
}
