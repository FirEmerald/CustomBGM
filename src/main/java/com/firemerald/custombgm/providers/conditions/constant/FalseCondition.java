package com.firemerald.custombgm.providers.conditions.constant;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

public class FalseCondition implements BGMProviderCondition
{
	public static final FalseCondition INSTANCE = new FalseCondition();
	public static final MapCodec<FalseCondition> CODEC = MapCodec.unit(INSTANCE);
	public static final MapCodec<FalseCondition> CODEC2 = MapCodec.unit(INSTANCE);

	private FalseCondition() {}

	@Override
	public boolean test(PlayerConditionData player) {
		return false;
	}

	@Override
	public MapCodec<FalseCondition> codec() {
		return CODEC;
	}

	@Override
	public TrueCondition not() {
		return TrueCondition.INSTANCE;
	}
}