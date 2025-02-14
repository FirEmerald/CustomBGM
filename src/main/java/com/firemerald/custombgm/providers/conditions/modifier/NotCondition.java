package com.firemerald.custombgm.providers.conditions.modifier;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NotCondition implements BGMProviderCondition
{
	public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder
			.group(BGMProviderCondition.CODEC.fieldOf("condition").forGetter(condition -> condition.condition))
			.apply(builder, NotCondition::new)
			);

	public final BGMProviderCondition condition;

	public NotCondition(BGMProviderCondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean test(PlayerConditionData player) {
		return !condition.test(player);
	}

	@Override
	public MapCodec<NotCondition> codec() {
		return CODEC;
	}

	@Override
	public BGMProviderCondition not() {
		return condition;
	}
}