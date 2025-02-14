package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public class OnFireCondition  implements BGMProviderPlayerCondition {
	public static final MapCodec<OnFireCondition> CODEC = Codec.BOOL.optionalFieldOf("on_fire", true).xmap(OnFireCondition::of, condition -> condition.onFire);

	public static final OnFireCondition TRUE = new OnFireCondition(true), FALSE = new OnFireCondition(false);

	public static OnFireCondition of(boolean onFire) {
		return onFire ? TRUE : FALSE;
	}

	public final boolean onFire;

	private OnFireCondition(boolean onFire) {
		this.onFire = onFire;
	}

	@Override
	public MapCodec<OnFireCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return player.isOnFire() == onFire;
	}

	@Override
	public OnFireCondition simpleNot() {
		return of(!onFire);
	}

}
