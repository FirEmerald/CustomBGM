package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.player.Player;

public record HealthCondition(MinMaxBounds.Doubles health, boolean scaled) implements BGMProviderPlayerCondition {
	public static final MapCodec<HealthCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				Codecs.DOUBLE_BOUNDS.fieldOf("health").forGetter(HealthCondition::health),
				Codec.BOOL.optionalFieldOf("scaled", true).forGetter(HealthCondition::scaled)
				)
		.apply(instance, HealthCondition::new)
	);

	public HealthCondition(MinMaxBounds.Doubles health) {
		this(health, true);
	}

	@Override
	public MapCodec<HealthCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		double health = player.getHealth();
		if (scaled) health /= player.getMaxHealth();
		return this.health.matches(health);
	}
}