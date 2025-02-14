package com.firemerald.custombgm.providers.conditions.player;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;

public record EntityCondition(EntityPredicate predicate) implements BGMProviderCondition {
	public static final MapCodec<EntityCondition> CODEC = EntityPredicate.CODEC.xmap(EntityCondition::new, EntityCondition::predicate).fieldOf("predicate");

	@Override
	public boolean test(PlayerConditionData t) {
		if (t.player instanceof ServerPlayer player) return predicate.matches(player, player);
		else return false;
	}

	@Override
	public MapCodec<EntityCondition> codec() {
		return CODEC;
	}

}
