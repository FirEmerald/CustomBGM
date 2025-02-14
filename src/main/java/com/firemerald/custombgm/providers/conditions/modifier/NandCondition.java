package com.firemerald.custombgm.providers.conditions.modifier;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

public class NandCondition extends CompoundCondition {
    public static final MapCodec<NandCondition> CODEC = makeCodec(NandCondition::new);

	public NandCondition(BGMProviderCondition... conditions) {
		super(conditions);
	}

	@Override
	public boolean test(PlayerConditionData player) {
		for (BGMProviderCondition condition : conditions) if (!condition.test(player)) return true;
		return false;
	}

	@Override
	public MapCodec<NandCondition> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends CompoundCondition.Builder<NandCondition> {
		public Builder() {
			super();
		}

		public Builder(NandCondition derive) {
			super(derive);
		}

		@Override
		public NandCondition build(BGMProviderCondition[] conditions) {
			return new NandCondition(conditions);
		}
	}

	@Override
	public AndCondition not() {
		return new AndCondition(conditions);
	}
}