package com.firemerald.custombgm.api.providers.conditions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.providers.conditions.modifier.NotCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public interface BGMProviderCondition extends Predicate<PlayerConditionData> {
    public static final Codec<BGMProviderCondition> CODEC = CustomBGMRegistries.CONDITION_CODECS.byNameCodec().dispatch(BGMProviderCondition::codec, Function.identity());
    public static final Codec<List<BGMProviderCondition>> LIST_CODEC = CODEC.listOf();

	public MapCodec<? extends BGMProviderCondition> codec();
	
	public default BGMProviderCondition not() {
		return new NotCondition(this);
	}
	
	// this behaves like not() but will not respect certain uncommon cases such as player-based conditions returning false when not in-game
	public default BGMProviderCondition simpleNot() {
		return not();
	}
}
