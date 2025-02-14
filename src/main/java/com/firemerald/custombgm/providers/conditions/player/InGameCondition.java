package com.firemerald.custombgm.providers.conditions.player;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public class InGameCondition implements BGMProviderCondition {
	public static final MapCodec<InGameCondition> CODEC = Codec.BOOL.optionalFieldOf("in_game", true).xmap(InGameCondition::of, condition -> condition.inGame);

	public static final InGameCondition TRUE = new InGameCondition(true), FALSE = new InGameCondition(false);

	public static InGameCondition of(boolean inGame) {
		return inGame ? TRUE : FALSE;
	}

	public final boolean inGame;

	private InGameCondition(boolean inGame) {
		this.inGame = inGame;
	}

	@Override
	public boolean test(PlayerConditionData playerData) {
		return (playerData.player != null) == inGame;
	}

	@Override
	public MapCodec<InGameCondition> codec() {
		return CODEC;
	}

	@Override
	public InGameCondition not() {
		return of(!inGame);
	}
}
