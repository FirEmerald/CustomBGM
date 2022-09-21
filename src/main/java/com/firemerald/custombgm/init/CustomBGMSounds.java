package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CustomBGMSounds
{
	private static DeferredRegister<SoundEvent> registry = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CustomBGMAPI.MOD_ID);

	public static final RegistryObject<SoundEvent> EMPTY = registry.register(RegistryNames.SOUND_EMPTY, () -> new SoundEvent(new ResourceLocation(CustomBGMAPI.MOD_ID, RegistryNames.SOUND_EMPTY)));

	public static void init(IEventBus eventBus)
	{
		registry.register(eventBus);
		registry = null;
	}
}