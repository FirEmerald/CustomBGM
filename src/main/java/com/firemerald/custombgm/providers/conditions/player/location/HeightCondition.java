package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.player.Player;

public record HeightCondition(MinMaxBounds.Doubles height) implements BGMProviderPlayerCondition {
	public static final MapCodec<HeightCondition> CODEC = MinMaxBounds.Doubles.CODEC.fieldOf("height").xmap(HeightCondition::new, HeightCondition::height);

	@Override
	public MapCodec<HeightCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return height.matches(player.getY());
	}
}
