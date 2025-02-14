package com.firemerald.custombgm.providers.conditions.player.level;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.bounds.FloatBounds;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.player.Player;

public record RegionalDifficultyCondition(FloatBounds difficulty, boolean clamped) implements BGMProviderPlayerCondition {
	public static final MapCodec<RegionalDifficultyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			FloatBounds.CODEC.fieldOf("difficulty").forGetter(RegionalDifficultyCondition::difficulty),
			Codec.BOOL.optionalFieldOf("clamped", false).forGetter(RegionalDifficultyCondition::clamped)
			).apply(instance, RegionalDifficultyCondition::new));
	
	public RegionalDifficultyCondition(FloatBounds difficulty) {
		this(difficulty, false);
	}

	@Override
	public MapCodec<RegionalDifficultyCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return difficulty.matches(clamped ? playerData.getDifficultyInstance().getSpecialMultiplier() : playerData.getDifficultyInstance().getEffectiveDifficulty());
	}
}
