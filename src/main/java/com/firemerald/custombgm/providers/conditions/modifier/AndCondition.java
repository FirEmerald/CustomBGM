package com.firemerald.custombgm.providers.conditions.modifier;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

public class AndCondition extends CompoundCondition {
    public static final MapCodec<AndCondition> CODEC = makeCodec(AndCondition::new);

	public AndCondition(BGMProviderCondition... conditions) {
		super(conditions);
	}

	@Override
	public boolean test(PlayerConditionData player) {
		for (BGMProviderCondition condition : conditions) if (!condition.test(player)) return false;
		return true;
	}

	@Override
	public MapCodec<AndCondition> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends CompoundCondition.Builder<AndCondition> {
		public Builder() {
			super();
		}

		public Builder(AndCondition derive) {
			super(derive);
		}

		@Override
		public AndCondition build(BGMProviderCondition[] conditions) {
			return new AndCondition(conditions);
		}
	}

	@Override
	public NandCondition not() {
		return new NandCondition(conditions);
	}
}