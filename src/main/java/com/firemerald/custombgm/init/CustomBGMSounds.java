package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CustomBGMAPI.MOD_ID)
public class CustomBGMSounds
{
	@ObjectHolder("empty")
	public static final SoundEvent EMPTY = null;

	public static void registerSounds(IEventBus eventBus)
	{
		DeferredRegister<SoundEvent> sounds = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CustomBGMAPI.MOD_ID);
		sounds.register("empty", () -> new SoundEvent(new ResourceLocation(CustomBGMAPI.MOD_ID, "empty")));
		sounds.register(eventBus);
	}
}