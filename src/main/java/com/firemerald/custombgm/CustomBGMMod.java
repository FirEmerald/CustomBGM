package com.firemerald.custombgm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.ICustomMusic;
import com.firemerald.custombgm.api.ISoundLoop;
import com.firemerald.custombgm.api.capabilities.IBossTracker;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.ConfigClient;
import com.firemerald.custombgm.client.ReloadListener;
import com.firemerald.custombgm.client.audio.LoopingSounds;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.init.CustomBGMSounds;
import com.firemerald.custombgm.networking.client.SelfDataSyncPacket;
import com.firemerald.custombgm.networking.server.InitializedPacket;
import com.firemerald.fecore.init.registry.DeferredObjectRegistry;
import com.firemerald.fecore.networking.SimpleNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(CustomBGMAPI.MOD_ID)
public class CustomBGMMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("Custom BGM");
    public static final SimpleNetwork NETWORK = new SimpleNetwork(new ResourceLocation(CustomBGMAPI.MOD_ID, "main"), "1");
    
    public static final DeferredObjectRegistry REGISTRY = new DeferredObjectRegistry(CustomBGMAPI.MOD_ID);

    static final ForgeConfigSpec clientSpec;
    public static final ConfigClient CLIENT;
    static {
        final Pair<ConfigClient, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigClient::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public CustomBGMMod()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        if (FMLEnvironment.dist.isClient()) eventBus.addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
        CustomBGMBlockEntities.init();
        CustomBGMSounds.registerSounds(eventBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	NETWORK.registerClientPacket(SelfDataSyncPacket.class, SelfDataSyncPacket::new);
    	NETWORK.registerServerPacket(InitializedPacket.class, InitializedPacket::new);
		CustomBGMAPI.instance = new CustomBGMAPI() {
			final Map<ResourceLocation, ICustomMusic<Holder<Biome>>> biomeMapping = new HashMap<>();
			final Map<TagKey<Biome>, ICustomMusic<Holder<Biome>>> tagMapping = new HashMap<>();

			@Override
			@OnlyIn(Dist.CLIENT)
			public ISoundLoop grabSound(ResourceLocation name, SoundSource category, boolean disablePan)
			{
				return LoopingSounds.grabSound(name, category, disablePan);
			}

			@Override
			@OnlyIn(Dist.CLIENT)
			public ISoundLoop playSound(ResourceLocation name, SoundSource category, boolean disablePan)
			{
				return LoopingSounds.playSound(name, category, disablePan);
			}

			@Override
			public void registerBiomeMusic(ResourceLocation biomeName, ICustomMusic<Holder<Biome>> music)
			{
				biomeMapping.put(biomeName, music);
			}

			@Override
			public void registerBiomeMusic(TagKey<Biome> biomeTag, ICustomMusic<Holder<Biome>> music)
			{
				tagMapping.put(biomeTag, music);
			}

			@Override
			protected ICustomMusic<Holder<Biome>> getBiomeMusic(ResourceLocation biomeName)
			{
				return biomeMapping.get(biomeName);
			}

			@Override
			protected ICustomMusic<Holder<Biome>> getBiomeMusic(TagKey<Biome> biomeTag)
			{
				return tagMapping.get(biomeTag);
			}
		};
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event)
    {
    	((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(new ReloadListener());
    }

	public void registerCaps(RegisterCapabilitiesEvent event)
	{
		event.register(IPlayer.class);
		event.register(IBossTracker.class);
	}
}
