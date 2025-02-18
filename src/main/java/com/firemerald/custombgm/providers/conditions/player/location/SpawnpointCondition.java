package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.player.Player;

public record SpawnpointCondition(MinMaxBounds.Doubles distance) implements BGMProviderPlayerCondition {
	public static final MapCodec<SpawnpointCondition> CODEC = Codecs.DOUBLE_BOUNDS.fieldOf("distance").xmap(SpawnpointCondition::new, SpawnpointCondition::distance);

	@Override
	public MapCodec<SpawnpointCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		double distanceSqr;
		GlobalPos respawnPos = playerData.getRespawnPoint();
		if (respawnPos == null || !player.level().dimension().equals(respawnPos.dimension())) distanceSqr = Double.POSITIVE_INFINITY;
		else {
			BlockPos pos = respawnPos.pos();
			distanceSqr = player.distanceToSqr(pos.getX() + .5, pos.getY(), pos.getZ() + .5);
		}
		return distance.matchesSqr(distanceSqr);
	}
}
