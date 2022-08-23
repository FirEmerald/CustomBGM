package com.firemerald.custombgm;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.networking.CustomBGMNetwork;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CustomBGMAPI.MOD_ID)
public class CustomBGMMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("Custom BGM");
    
    static final ForgeConfigSpec clientSpec;
    public static final ConfigClient CLIENT;
    static {
        final Pair<ConfigClient, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigClient::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public CustomBGMMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
        //FECoreItems.registerItems(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	CustomBGMNetwork.init();
    }
}
