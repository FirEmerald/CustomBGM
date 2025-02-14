package com.firemerald.custombgm.providers.conditions.modifier;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class XorCondition implements BGMProviderCondition
{
	public static final MapCodec<XorCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder
			.group(
					BGMProviderCondition.CODEC.fieldOf("conditionA").forGetter(condition -> condition.conditionA),
					BGMProviderCondition.CODEC.fieldOf("conditionB").forGetter(condition -> condition.conditionB)
					)
			.apply(builder, XorCondition::new)
			);

	public final BGMProviderCondition conditionA, conditionB;

	public XorCondition(BGMProviderCondition conditionA, BGMProviderCondition conditionB) {
		this.conditionA = conditionA;
		this.conditionB = conditionB;
	}

	@Override
	public boolean test(PlayerConditionData player) {
		return conditionA.test(player) ^ conditionB.test(player);
	}

	@Override
	public MapCodec<XorCondition> codec() {
		return CODEC;
	}

	@Override
	public XnorCondition not() {
		return new XnorCondition(conditionA, conditionB);
	}
}