package com.firemerald.custombgm;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.ISoundLoop;
import com.firemerald.custombgm.api.capabilities.IBossTracker;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.capability.Targeter;
import com.firemerald.custombgm.client.ConfigClient;
import com.firemerald.custombgm.client.CustomBGMModelLayers;
import com.firemerald.custombgm.client.audio.LoopingSounds;
import com.firemerald.custombgm.common.ConfigServer;
import com.firemerald.custombgm.datagen.*;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.init.CustomBGMBlocks;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMSounds;
import com.firemerald.custombgm.networking.client.SelfDataSyncPacket;
import com.firemerald.custombgm.networking.server.InitializedPacket;
import com.firemerald.custombgm.providers.Providers;
import com.firemerald.custombgm.providers.conditions.Conditions;
import com.firemerald.fecore.init.registry.DeferredObjectRegistry;
import com.firemerald.fecore.networking.SimpleNetwork;

import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
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
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(CustomBGMAPI.MOD_ID)
public class CustomBGMMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("Custom BGM");
    public static final SimpleNetwork NETWORK = new SimpleNetwork(new ResourceLocation(CustomBGMAPI.MOD_ID, "main"), "1");

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
        eventBus.addListener(this::setup);
        eventBus.addListener(this::onGatherData);
        eventBus.addListener(this::registerCaps);
        if (FMLEnvironment.dist.isClient()) eventBus.addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
        CustomBGMBlockEntities.init();
        CustomBGMBlocks.init();
        CustomBGMEntities.init(eventBus);
        CustomBGMSounds.init(eventBus);
        REGISTRY.register(eventBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	NETWORK.registerClientPacket(SelfDataSyncPacket.class, SelfDataSyncPacket::new);
    	NETWORK.registerServerPacket(InitializedPacket.class, InitializedPacket::new);
		CustomBGMAPI.instance = new CustomBGMAPI() {
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
		};
		event.enqueueWork(Conditions::registerProviderConditions);
		event.enqueueWork(Providers::registerProviders);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event)
    {
    	CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL.setRenderLayer(RenderType.cutout());
        LayerDefinition minecartBody = MinecartModel.createBodyLayer();
    	ForgeHooksClient.registerLayerDefinition(CustomBGMModelLayers.BGM_MINECART, () -> minecartBody);
    	ForgeHooksClient.registerLayerDefinition(CustomBGMModelLayers.ENTITY_TESTER_MINECART, () -> minecartBody);
    	ForgeHooksClient.registerLayerDefinition(CustomBGMModelLayers.BOSS_SPAWNER_MINECART, () -> minecartBody);
    	EntityRenderers.register(CustomBGMEntities.BGM_MINECART.get(), (context) -> new MinecartRenderer(context, CustomBGMModelLayers.BGM_MINECART));
    	EntityRenderers.register(CustomBGMEntities.ENTITY_TESTER_MINECART.get(), (context) -> new MinecartRenderer(context, CustomBGMModelLayers.ENTITY_TESTER_MINECART));
    	EntityRenderers.register(CustomBGMEntities.BOSS_SPAWNER_MINECART.get(), (context) -> new MinecartRenderer(context, CustomBGMModelLayers.BOSS_SPAWNER_MINECART));
    }

	public void registerCaps(RegisterCapabilitiesEvent event)
	{
		event.register(IPlayer.class);
		event.register(IBossTracker.class);
		event.register(Targeter.class);
	}

	public void onGatherData(GatherDataEvent event)
	{
		if (event.includeClient())
		{
			event.getGenerator().addProvider(new ModelGenerator(event.getGenerator(), CustomBGMAPI.MOD_ID, event.getExistingFileHelper()));
		}
		if (event.includeServer())
		{
			event.getGenerator().addProvider(new BlockTagsGenerator(event.getGenerator(), CustomBGMAPI.MOD_ID, event.getExistingFileHelper()));
			event.getGenerator().addProvider(new ItemTagsGenerator(event.getGenerator(), CustomBGMAPI.MOD_ID, event.getExistingFileHelper()));
			event.getGenerator().addProvider(new LootTableGenerator(event.getGenerator()));
			event.getGenerator().addProvider(new RecipeGenerator(event.getGenerator()));
		}
	}
}
