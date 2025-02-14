package com.firemerald.custombgm.providers.volume;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.api.providers.volume.BGMProviderPlayerVolume;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

public class BiomeVolume implements BGMProviderPlayerVolume {
	public static final BiomeVolume INSTANCE = new BiomeVolume();

	public static final MapCodec<BiomeVolume> CODEC = MapCodec.unit(INSTANCE);

	private BiomeVolume() {}

	@Override
	public MapCodec<BiomeVolume> codec() {
		return CODEC;
	}

	@Override
	public float getVolume(PlayerConditionData playerData, Player player) {
		Holder<Biome> biome = playerData.getBiome();
		return biome == null ? 1f : biome.value().getBackgroundMusicVolume();
	}
}