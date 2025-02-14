package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public class CrouchingCondition  implements BGMProviderPlayerCondition {
	public static final MapCodec<CrouchingCondition> CODEC = Codec.BOOL.optionalFieldOf("crouching", true).xmap(CrouchingCondition::of, condition -> condition.crouching);

	public static final CrouchingCondition TRUE = new CrouchingCondition(true), FALSE = new CrouchingCondition(false);

	public static CrouchingCondition of(boolean inGame) {
		return inGame ? TRUE : FALSE;
	}

	public final boolean crouching;

	private CrouchingCondition(boolean crouching) {
		this.crouching = crouching;
	}

	@Override
	public MapCodec<CrouchingCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return player.isCrouching() == crouching;
	}

	@Override
	public CrouchingCondition simpleNot() {
		return of(!crouching);
	}

}
