package com.firemerald.custombgm.providers.conditions.modifier;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

public class OrCondition extends CompoundCondition {
    public static final MapCodec<OrCondition> CODEC = makeCodec(OrCondition::new);

	public OrCondition(BGMProviderCondition... conditions) {
		super(conditions);
	}

	@Override
	public boolean test(PlayerConditionData player) {
		for (BGMProviderCondition condition : conditions) if (condition.test(player)) return true;
		return false;
	}

	@Override
	public MapCodec<OrCondition> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends CompoundCondition.Builder<OrCondition> {
		public Builder() {
			super();
		}

		public Builder(OrCondition derive) {
			super(derive);
		}

		@Override
		public OrCondition build(BGMProviderCondition[] conditions) {
			return new OrCondition(conditions);
		}
	}

	@Override
	public NorCondition not() {
		return new NorCondition(conditions);
	}
}