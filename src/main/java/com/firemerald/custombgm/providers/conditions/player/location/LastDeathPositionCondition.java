package com.firemerald.custombgm.providers.conditions.player.location;

import java.util.Optional;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.player.Player;

public record LastDeathPositionCondition(MinMaxBounds.Doubles distance) implements BGMProviderPlayerCondition {
	public static final MapCodec<LastDeathPositionCondition> CODEC = Codecs.DOUBLE_BOUNDS.fieldOf("distance").xmap(LastDeathPositionCondition::new, LastDeathPositionCondition::distance);

	@Override
	public MapCodec<LastDeathPositionCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		double distanceSqr;
		Optional<GlobalPos> deathPosOpt = player.getLastDeathLocation();
		if (deathPosOpt.isEmpty()) distanceSqr = Double.POSITIVE_INFINITY;
		else {
			GlobalPos deathPos = deathPosOpt.get();
			if (!player.level().dimension().equals(deathPos.dimension())) distanceSqr = Double.POSITIVE_INFINITY;
			else {
				BlockPos pos = deathPos.pos();
				distanceSqr = player.distanceToSqr(pos.getX() + .5, pos.getY(), pos.getZ() + .5);
			}
		}
		return distance.matchesSqr(distanceSqr);
	}
}
