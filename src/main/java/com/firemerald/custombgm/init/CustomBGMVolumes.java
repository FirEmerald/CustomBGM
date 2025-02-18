package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.api.providers.volume.BGMProviderVolume;
import com.firemerald.custombgm.api.providers.volume.ConstantVolume;
import com.mojang.serialization.MapCodec;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class CustomBGMVolumes {
	private static DeferredRegister<MapCodec<? extends BGMProviderVolume>> registry = DeferredRegister.create(CustomBGMRegistries.Keys.VOLUME_CODECS, CustomBGMAPI.MOD_ID);

	static {
		CustomBGMRegistries.volumeCodecs = registry.makeRegistry(() -> RegistryBuilder.of(CustomBGMRegistries.Keys.VOLUME_CODECS.location()));
	}

	public static final RegistryObject<MapCodec<ConstantVolume>> BASE = registry.register("constant", () -> ConstantVolume.CODEC);

	public static void init(IEventBus bus) {
		registry.register(bus);
		registry = null;
	}
}
