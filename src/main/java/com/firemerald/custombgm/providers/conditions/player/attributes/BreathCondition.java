package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.player.Player;

public record BreathCondition(MinMaxBounds.Doubles oxygen, boolean scaled) implements BGMProviderPlayerCondition {
	public static final MapCodec<BreathCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				Codecs.DOUBLE_BOUNDS.fieldOf("oxygen").forGetter(BreathCondition::oxygen),
				Codec.BOOL.optionalFieldOf("scaled", true).forGetter(BreathCondition::scaled)
				)
		.apply(instance, BreathCondition::new)
	);

	public BreathCondition(MinMaxBounds.Doubles oxygen) {
		this(oxygen, true);
	}

	@Override
	public MapCodec<BreathCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		double oxygen = player.getAirSupply();
		if (scaled) oxygen /= player.getMaxAirSupply();
		return this.oxygen.matches(oxygen);
	}
}