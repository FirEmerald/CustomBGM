package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public class FlyingCondition implements BGMProviderPlayerCondition {
	public static final MapCodec<FlyingCondition> CODEC = Codec.BOOL.optionalFieldOf("flying", true).xmap(FlyingCondition::of, condition -> condition.flying);

	public static final FlyingCondition TRUE = new FlyingCondition(true), FALSE = new FlyingCondition(false);

	public static FlyingCondition of(boolean inGame) {
		return inGame ? TRUE : FALSE;
	}

	public final boolean flying;

	private FlyingCondition(boolean flying) {
		this.flying = flying;
	}

	@Override
	public MapCodec<FlyingCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return (player.isFallFlying() || player.getAbilities().flying) == flying;
	}

	@Override
	public FlyingCondition simpleNot() {
		return of(!flying);
	}

}
