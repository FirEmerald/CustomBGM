package com.firemerald.custombgm.providers.conditions.player.level;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.EnumCodec;
import com.firemerald.fecore.util.bounds.FloatBounds;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

public record WeatherCondition(@Nullable FloatBounds rainLevel, @Nullable FloatBounds thunderLevel, @Nullable Biome.Precipitation precipitationType) implements BGMProviderPlayerCondition {
	public static final MapCodec<WeatherCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			FloatBounds.CODEC.optionalFieldOf("rain_level", null).forGetter(WeatherCondition::rainLevel),
			FloatBounds.CODEC.optionalFieldOf("thunder_level", null).forGetter(WeatherCondition::thunderLevel),
			new EnumCodec<>(Biome.Precipitation.values()).optionalFieldOf("precipitation_type", null).forGetter(WeatherCondition::precipitationType)
			)
			.apply(instance, WeatherCondition::new)
	);

	@Override
	public MapCodec<WeatherCondition> codec() {
		return CODEC;
	}

	@SuppressWarnings("resource")
	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		if (rainLevel != null && !rainLevel.matches(player.level().rainLevel)) return false;
		if (thunderLevel != null && !thunderLevel.matches(player.level().thunderLevel)) return false;
		if (precipitationType != null && playerData.getBiome().value().getPrecipitationAt(player.blockPosition()) == precipitationType) return false;
		return true;
	}
}
