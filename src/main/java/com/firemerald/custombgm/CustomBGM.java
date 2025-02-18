package com.firemerald.custombgm;

import org.slf4j.Logger;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.capabilities.BossTracker;
import com.firemerald.custombgm.capabilities.ServerPlayerData;
import com.firemerald.custombgm.capabilities.Targeter;
import com.firemerald.custombgm.config.ClientConfig;
import com.firemerald.custombgm.config.ServerConfig;
import com.firemerald.custombgm.init.CustomBGMConditions;
import com.firemerald.custombgm.init.CustomBGMCreativeModeTabs;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.init.CustomBGMProviders;
import com.firemerald.custombgm.init.CustomBGMVolumes;
import com.firemerald.custombgm.network.clientbound.MusicSyncPacket;
import com.firemerald.fecore.network.SimpleNetwork;
import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(CustomBGMAPI.MOD_ID)
public class CustomBGM
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final SimpleNetwork NETWORK = new SimpleNetwork(CustomBGMAPI.id("main"), "3");

	public CustomBGM(FMLJavaModLoadingContext loadingContext)
    {
    	IEventBus modEventBus = loadingContext.getModEventBus();
		loadingContext.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    	modEventBus.addListener(this::setup);
    	modEventBus.addListener(this::registerCaps);
        CustomBGMObjects.init(modEventBus);
        CustomBGMEntities.init(modEventBus);
        CustomBGMCreativeModeTabs.init(modEventBus);
        CustomBGMProviders.init(modEventBus);
        CustomBGMConditions.init(modEventBus);
        CustomBGMVolumes.init(modEventBus);
        if (FMLEnvironment.dist.isClient()) doClientStuff(loadingContext);
    }

    @OnlyIn(Dist.CLIENT)
    public void doClientStuff(FMLJavaModLoadingContext loadingContext) {
		loadingContext.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
    	NETWORK.registerClientPacket(MusicSyncPacket.class, MusicSyncPacket::new);
    }

	public void registerCaps(RegisterCapabilitiesEvent event) {
		event.register(ServerPlayerData.class);
		event.register(Targeter.class);
		event.register(BossTracker.class);
	}
}
