package com.firemerald.custombgm.api.providers.volume;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;

import net.minecraft.world.entity.player.Player;

public interface BGMProviderPlayerVolume extends BGMProviderVolume {
	@Override
    public default float getVolume(PlayerConditionData playerData) {
		return playerData.player != null ? getVolume(playerData, playerData.player) : 1f;
	}

	public float getVolume(PlayerConditionData playerData, Player player);
}
