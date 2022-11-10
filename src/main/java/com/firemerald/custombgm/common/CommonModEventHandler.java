package com.firemerald.custombgm.common;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.ISoundLoop;
import com.firemerald.custombgm.api.capabilities.IBossTracker;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.capability.Targeter;
import com.firemerald.custombgm.client.audio.LoopingSounds;
import com.firemerald.custombgm.datagen.*;
import com.firemerald.custombgm.networking.client.SelfDataSyncPacket;
import com.firemerald.custombgm.networking.server.InitializedPacket;
import com.firemerald.custombgm.providers.Providers;
import com.firemerald.custombgm.providers.conditions.Conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEventHandler
{
	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event)
    {
    	CustomBGMMod.NETWORK.registerClientPacket(SelfDataSyncPacket.class, SelfDataSyncPacket::new);
    	CustomBGMMod.NETWORK.registerServerPacket(InitializedPacket.class, InitializedPacket::new);
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

	@SubscribeEvent
	public static void registerCaps(RegisterCapabilitiesEvent event)
	{
		event.register(IPlayer.class);
		event.register(IBossTracker.class);
		event.register(Targeter.class);
	}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event)
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
			event.getGenerator().addProvider(new BackgroundMusicProvider(event.getGenerator()));
		}
	}
}