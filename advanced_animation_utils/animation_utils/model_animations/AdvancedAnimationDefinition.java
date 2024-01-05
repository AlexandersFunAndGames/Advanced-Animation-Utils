package advanced_animation_utils.animation_utils.model_animations;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record AdvancedAnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AdvancedAnimationChannel>> boneAnimations) {
   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final float length;
      private final Map<String, List<AdvancedAnimationChannel>> animationByBone = Maps.newHashMap();
      private boolean looping;

      public static AdvancedAnimationDefinition.Builder withLength(float p_232276_) {
         return new AdvancedAnimationDefinition.Builder(p_232276_);
      }

      private Builder(float p_232273_) {
         this.length = p_232273_;
      }

      public AdvancedAnimationDefinition.Builder looping() {
         this.looping = true;
         return this;
      }

      public AdvancedAnimationDefinition.Builder addAnimation(String p_232280_, AdvancedAnimationChannel p_232281_) {
         this.animationByBone.computeIfAbsent(p_232280_, (p_232278_) -> {
            return Lists.newArrayList();
         }).add(p_232281_);
         return this;
      }

      public AdvancedAnimationDefinition build() {
         return new AdvancedAnimationDefinition(this.length, this.looping, this.animationByBone);
      }
   }
}