package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.bounds.FloatBounds;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public record ScaleCondition(FloatBounds scale) implements BGMProviderPlayerCondition {
	public static final MapCodec<ScaleCondition> CODEC = FloatBounds.CODEC.fieldOf("scale").xmap(ScaleCondition::new, ScaleCondition::scale);

	@Override
	public MapCodec<ScaleCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return scale.matches(player.getScale());
	}
}