package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.api.providers.volume.BGMProviderVolume;
import com.firemerald.custombgm.api.providers.volume.ConstantVolume;
import com.firemerald.custombgm.providers.volume.BiomeVolume;
import com.mojang.serialization.MapCodec;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomBGMVolumes {
	private static DeferredRegister<MapCodec<? extends BGMProviderVolume>> registry = DeferredRegister.create(CustomBGMRegistries.Keys.VOLUME_CODECS, CustomBGMAPI.MOD_ID);

	public static final DeferredHolder<MapCodec<? extends BGMProviderVolume>, MapCodec<ConstantVolume>> BASE = registry.register("constant", () -> ConstantVolume.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderVolume>, MapCodec<BiomeVolume>> BIOME = registry.register("biome", () -> BiomeVolume.CODEC);

	public static void init(IEventBus bus) {
		registry.register(bus);
		registry = null;
	}
}
