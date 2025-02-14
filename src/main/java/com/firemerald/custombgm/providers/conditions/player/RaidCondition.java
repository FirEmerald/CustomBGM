package com.firemerald.custombgm.providers.conditions.player;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;

public record RaidCondition(Raid.RaidStatus status, MinMaxBounds.Ints wave, MinMaxBounds.Ints level) implements BGMProviderPlayerCondition {
	public static final MapCodec<RaidCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.xmap(Raid.RaidStatus::getByName, Raid.RaidStatus::getName).optionalFieldOf("status", Raid.RaidStatus.ONGOING).forGetter(RaidCondition::status),
			MinMaxBounds.Ints.CODEC.optionalFieldOf("wave", MinMaxBounds.Ints.ANY).forGetter(RaidCondition::wave),
			MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(RaidCondition::level)
			).apply(instance, RaidCondition::new)
	);

	@Override
	public MapCodec<RaidCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		Raid raid = playerData.getRaid();
		return raid != null && raid.status == status && wave.matches(raid.getGroupsSpawned()) && level.matches(raid.getRaidOmenLevel());
	}
}
