package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.boundingshapes.BoundingShape;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public record InShapeCondition(BoundingShape shape) implements BGMProviderPlayerCondition {
	public static final MapCodec<InShapeCondition> CODEC = BoundingShape.CODEC.fieldOf("shape").xmap(InShapeCondition::new, InShapeCondition::shape);

	@Override
	public MapCodec<InShapeCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return shape.isWithin(player, player.position().x(), player.position().y(), player.position().z(), 0, 0, 0);
	}
}