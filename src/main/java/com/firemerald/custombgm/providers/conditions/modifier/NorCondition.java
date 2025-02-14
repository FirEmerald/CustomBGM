package com.firemerald.custombgm.providers.conditions.modifier;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

public class NorCondition extends CompoundCondition {
    public static final MapCodec<NorCondition> CODEC = makeCodec(NorCondition::new);

	public NorCondition(BGMProviderCondition... conditions) {
		super(conditions);
	}

	@Override
	public boolean test(PlayerConditionData player) {
		for (BGMProviderCondition condition : conditions) if (condition.test(player)) return false;
		return true;
	}

	@Override
	public MapCodec<NorCondition> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends CompoundCondition.Builder<NorCondition> {
		public Builder() {
			super();
		}

		public Builder(NorCondition derive) {
			super(derive);
		}

		@Override
		public NorCondition build(BGMProviderCondition[] conditions) {
			return new NorCondition(conditions);
		}
	}

	@Override
	public OrCondition not() {
		return new OrCondition(conditions);
	}
}