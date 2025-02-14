package com.firemerald.custombgm.api.providers.conditions;

import net.minecraft.world.entity.player.Player;

public interface BGMProviderPlayerCondition extends BGMProviderCondition {
	@Override
	public default boolean test(PlayerConditionData playerData) {
		return playerData.player != null && test(playerData, playerData.player);
	}

	public boolean test(PlayerConditionData playerData, Player player);
}
