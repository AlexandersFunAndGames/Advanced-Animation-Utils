package advanced_animation_utils.armour_utils;

import com.mojang.blaze3d.vertex.PoseStack;

public interface AdvancedArmourWearingModel {
	void translateArmour(AdvancedArmourLayer.ArmourModelPart modelPart, PoseStack stack, boolean innerModel);
}
