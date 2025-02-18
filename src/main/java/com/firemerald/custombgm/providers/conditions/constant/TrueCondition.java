package com.firemerald.custombgm.providers.conditions.constant;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

public class TrueCondition implements BGMProviderCondition
{
	public static final TrueCondition INSTANCE = new TrueCondition();
	public static final MapCodec<TrueCondition> CODEC = MapCodec.unit(INSTANCE);
	public static final MapCodec<TrueCondition> CODEC2 = MapCodec.unit(INSTANCE);

	private TrueCondition() {}

	@Override
	public boolean test(PlayerConditionData player) {
		return true;
	}

	@Override
	public MapCodec<TrueCondition> codec() {
		return CODEC;
	}

	@Override
	public FalseCondition not() {
		return FalseCondition.INSTANCE;
	}
}