package com.firemerald.custombgm;

import org.slf4j.Logger;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.config.ServerConfig;
import com.firemerald.custombgm.init.CustomBGMAttachments;
import com.firemerald.custombgm.init.CustomBGMConditions;
import com.firemerald.custombgm.init.CustomBGMCreativeModeTabs;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.init.CustomBGMProviders;
import com.firemerald.custombgm.init.CustomBGMVolumes;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(CustomBGMAPI.MOD_ID)
public class CustomBGM
{
    public static final Logger LOGGER = LogUtils.getLogger();

	public CustomBGM(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        CustomBGMObjects.init(modEventBus);
        CustomBGMEntities.init(modEventBus);
        CustomBGMCreativeModeTabs.init(modEventBus);
        CustomBGMAttachments.init(modEventBus);
        CustomBGMProviders.init(modEventBus);
        CustomBGMConditions.init(modEventBus);
        CustomBGMVolumes.init(modEventBus);
    }
}
