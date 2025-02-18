package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public class UnderwaterCondition implements BGMProviderPlayerCondition {
	public static final MapCodec<UnderwaterCondition> CODEC = Codec.BOOL.optionalFieldOf("underwater", true).xmap(UnderwaterCondition::of, condition -> condition.underwater);

	public static final UnderwaterCondition TRUE = new UnderwaterCondition(true), FALSE = new UnderwaterCondition(false);

	public static UnderwaterCondition of(boolean underwater) {
		return underwater ? TRUE : FALSE;
	}

	public final boolean underwater;

	private UnderwaterCondition(boolean underwater) {
		this.underwater = underwater;
	}

	@Override
	public MapCodec<UnderwaterCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return player.isUnderWater() == underwater;
	}

	@Override
	public UnderwaterCondition simpleNot() {
		return of(!underwater);
	}
}
