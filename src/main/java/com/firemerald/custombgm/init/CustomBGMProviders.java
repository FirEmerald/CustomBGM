package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.providers.BaseMusicProvider;
import com.firemerald.custombgm.providers.BiomeMusicProvider;
import com.firemerald.custombgm.providers.ScreenMusicProvider;
import com.firemerald.custombgm.providers.VanillaMusicProvider;
import com.mojang.serialization.MapCodec;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class CustomBGMProviders {
	private static DeferredRegister<MapCodec<? extends BGMProvider>> registry = DeferredRegister.create(CustomBGMRegistries.Keys.PROVIDER_CODECS, CustomBGMAPI.MOD_ID);

	static {
		CustomBGMRegistries.providerCodecs = registry.makeRegistry(() -> RegistryBuilder.of(CustomBGMRegistries.Keys.PROVIDER_CODECS.location()));
	}

	public static final RegistryObject<MapCodec<BaseMusicProvider>> BASE = registry.register("base", () -> BaseMusicProvider.CODEC);
	public static final RegistryObject<MapCodec<BiomeMusicProvider>> BIOME = registry.register("biome", () -> BiomeMusicProvider.CODEC);
	public static final RegistryObject<MapCodec<ScreenMusicProvider>> SCREEN = registry.register("screen", () -> ScreenMusicProvider.CODEC);
	public static final RegistryObject<MapCodec<VanillaMusicProvider>> VANILLA = registry.register("vanilla", () -> VanillaMusicProvider.CODEC);

	public static void init(IEventBus bus) {
		registry.register(bus);
		registry = null;
	}
}
