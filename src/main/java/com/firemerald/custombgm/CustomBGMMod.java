package com.firemerald.custombgm;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.client.ConfigClient;
import com.firemerald.custombgm.common.ConfigServer;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.init.CustomBGMBlocks;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMSounds;
import com.firemerald.fecore.init.registry.DeferredObjectRegistry;
import com.firemerald.fecore.networking.SimpleNetwork;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CustomBGMAPI.MOD_ID)
public class CustomBGMMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("Custom BGM");
    public static final SimpleNetwork NETWORK = new SimpleNetwork(new ResourceLocation(CustomBGMAPI.MOD_ID, "main"), "2");

    public static final DeferredObjectRegistry REGISTRY = new DeferredObjectRegistry(CustomBGMAPI.MOD_ID);

    static final ForgeConfigSpec clientSpec, serverSpec;
    public static final ConfigClient CLIENT;
    public static final ConfigServer SERVER;
    static {
        final Pair<ConfigClient, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigClient::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
        final Pair<ConfigServer, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ConfigServer::new);
        serverSpec = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }

    public CustomBGMMod()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
        CustomBGMBlockEntities.init();
        CustomBGMBlocks.init();
        CustomBGMEntities.init(eventBus);
        CustomBGMSounds.init(eventBus);
        REGISTRY.register(eventBus);
    }
}
