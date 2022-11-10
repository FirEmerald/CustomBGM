package com.firemerald.custombgm.api.providers.conditions;

import java.util.HashMap;
import java.util.Map;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.capabilities.IPlayer;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

public class PlayerConditionData
{
	public static final ConditionKey<Holder<Biome>> BIOME_KEY = new ConditionKey<>(CustomBGMAPI.MOD_ID, "biome") {
		@Override
		public Holder<Biome> compose(PlayerConditionData playerData)
		{
			return playerData.player.level.getBiome(playerData.player.blockPosition());
		}
	};

	public final Player player;
	public final IPlayer iPlayer;
	private final Map<ConditionKey<?>, Object> data = new HashMap<>();

	public PlayerConditionData(Player player, IPlayer iPlayer)
	{
		this.player = player;
		this.iPlayer = iPlayer;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(ConditionKey<T> key)
	{
		return (T) data.computeIfAbsent(key, k -> k.compose(this));
	}

	public Holder<Biome> getBiome()
	{
		return getData(BIOME_KEY);
	}
}
