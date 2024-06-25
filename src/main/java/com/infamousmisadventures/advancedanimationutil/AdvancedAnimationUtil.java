package com.infamousmisadventures.advancedanimationutil;

import com.infamousmisadventures.advancedanimationutil.manager.AAUModManager;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Locale;

@Mod(AdvancedAnimationUtil.MODID)
public class AdvancedAnimationUtil {
    public static final String MOD_NAME = "Advanced Animation Util";
    public static final String MODID = "advancedanimationutil";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AdvancedAnimationUtil() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        if (modBus != null && forgeBus != null) AAUModManager.registerAll(modBus, forgeBus);
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MODID, path.toLowerCase(Locale.ROOT));
    }
}
