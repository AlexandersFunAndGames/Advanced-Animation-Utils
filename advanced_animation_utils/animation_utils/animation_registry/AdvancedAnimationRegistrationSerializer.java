package advanced_animation_utils.animation_utils.animation_registry;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import advanced_animation_utils.animation_utils.animation_registry.AdvancedAnimationRegistration.BoneAnimation;
import advanced_animation_utils.animation_utils.animation_registry.AdvancedAnimationRegistration.InterpolationType;
import advanced_animation_utils.animation_utils.animation_registry.AdvancedAnimationRegistration.StringKeyframe;
import advanced_animation_utils.animation_utils.animation_registry.AdvancedAnimationRegistration.TargetType;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedAnimationRegistrationSerializer implements JsonDeserializer<AdvancedAnimationRegistration> {

   public AdvancedAnimationRegistration deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonobject = GsonHelper.convertToJsonObject(element, "entry");
      return new AdvancedAnimationRegistration(
    		  GsonHelper.getAsFloat(jsonobject, "length", 0.0F), 
    		  GsonHelper.getAsBoolean(jsonobject, "looping", false), 
    		  getAnimations(jsonobject));
   }

   private Map<String, List<BoneAnimation>> getAnimations(JsonObject object) {
	  Map<String, List<BoneAnimation>> map = Maps.newHashMap();
      if (object.has("bones")) {
         JsonArray bones = GsonHelper.getAsJsonArray(object, "bones");

         for(int i = 0; i < bones.size(); ++i) {
            JsonObject boneObject = GsonHelper.convertToJsonObject(bones.get(i), "bone");                       
            
            map.put(GsonHelper.getAsString(boneObject, "name", (String)null), 
            		getBoneAnimations(boneObject));
         }
      }

      return map;
   }
   
   public List<BoneAnimation> getBoneAnimations(JsonObject object) {
	   List<BoneAnimation> boneAnimations = Lists.newArrayList();
       
       JsonArray animations = GsonHelper.getAsJsonArray(object, "bone_animations");
       for(int i = 0; i < animations.size(); ++i) {
       	JsonObject animationObject = GsonHelper.convertToJsonObject(animations.get(i), "animation");            	
       	
       	boneAnimations.add(new BoneAnimation(
       			TargetType.byName(GsonHelper.getAsString(animationObject, "target", (String)null)).target, 
       			((StringKeyframe[])getKeyframes(animationObject).toArray())));
       }
       
       return boneAnimations;
   }
   
   public List<StringKeyframe> getKeyframes(JsonObject object) {
      	List<StringKeyframe> stringKeyframes = Lists.newArrayList();
       	
      	JsonArray keyframes = GsonHelper.getAsJsonArray(object, "keyframes");
          for(int i = 0; i < keyframes.size(); ++i) {
          	JsonObject keyframeObject = GsonHelper.convertToJsonObject(keyframes.get(i), "keyframe");   
          	
          	stringKeyframes.add(new StringKeyframe(
          			GsonHelper.getAsFloat(keyframeObject, "timestamp", 0.0F), 
          			GsonHelper.getAsString(keyframeObject, "xValue", (String)null), 
          			GsonHelper.getAsString(keyframeObject, "yValue", (String)null), 
          			GsonHelper.getAsString(keyframeObject, "zValue", (String)null), 
          			InterpolationType.byName(GsonHelper.getAsString(keyframeObject, "interpolation", (String)null)).interpolation));
          }
          
          return stringKeyframes;
   }
}