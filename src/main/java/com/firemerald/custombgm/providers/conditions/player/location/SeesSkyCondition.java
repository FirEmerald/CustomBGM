package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public class SeesSkyCondition implements BGMProviderCondition {
	public static final MapCodec<SeesSkyCondition> CODEC = Codec.BOOL.optionalFieldOf("sees_sky", true).xmap(SeesSkyCondition::of, condition -> condition.seesSky);

	public static final SeesSkyCondition TRUE = new SeesSkyCondition(true), FALSE = new SeesSkyCondition(false);

	public static SeesSkyCondition of(boolean inGame) {
		return inGame ? TRUE : FALSE;
	}

	public final boolean seesSky;

	private SeesSkyCondition(boolean seesSky) {
		this.seesSky = seesSky;
	}

	@Override
	public MapCodec<SeesSkyCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData) {
		return playerData.seesSky() == seesSky;
	}

	@Override
	public SeesSkyCondition simpleNot() {
		return of(!seesSky);
	}
}
