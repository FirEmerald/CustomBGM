package com.firemerald.custombgm.providers.conditions.modifier;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class XnorCondition implements BGMProviderCondition
{
	public static final MapCodec<XnorCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder
			.group(
					BGMProviderCondition.CODEC.fieldOf("conditionA").forGetter(condition -> condition.conditionA),
					BGMProviderCondition.CODEC.fieldOf("conditionB").forGetter(condition -> condition.conditionB)
					)
			.apply(builder, XnorCondition::new)
			);

	public final BGMProviderCondition conditionA, conditionB;

	public XnorCondition(BGMProviderCondition conditionA, BGMProviderCondition conditionB) {
		this.conditionA = conditionA;
		this.conditionB = conditionB;
	}

	@Override
	public boolean test(PlayerConditionData player) {
		return conditionA.test(player) == conditionB.test(player);
	}

	@Override
	public MapCodec<XnorCondition> codec() {
		return CODEC;
	}

	@Override
	public XorCondition not() {
		return new XorCondition(conditionA, conditionB);
	}
}