package com.firemerald.custombgm.providers.conditions.player.attributes;

import java.util.HashMap;
import java.util.Map;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.player.Player;

public record StatisticCondition(Map<Stat<?>, MinMaxBounds.Ints> stats) implements BGMProviderPlayerCondition {
	@SuppressWarnings("unchecked")
	public static final MapCodec<StatisticCondition> CODEC = Codec.dispatchedMap(
			BuiltInRegistries.STAT_TYPE.byNameCodec(),
			statType -> Codec.unboundedMap(
					((Registry<Object>) statType.getRegistry()).byNameCodec(), 
					MinMaxBounds.Ints.CODEC
					)
			).fieldOf("stats").xmap(expanded -> {
				Map<Stat<?>, MinMaxBounds.Ints> stats = new HashMap<>();
				expanded.forEach((type, objects) -> processStatType(type, objects, stats));
				return new StatisticCondition(stats);
			}, condition -> {
				Map<StatType<?>, Map<Object, MinMaxBounds.Ints>> expanded = new HashMap<>();
				condition.stats.forEach((stat, bounds) -> expanded.computeIfAbsent(stat.getType(), type -> new HashMap<>()).put(stat.getValue(), bounds));
				return expanded;
			});
	
	@SuppressWarnings("unchecked")
	public static <T> void processStatType(StatType<?> type, Map<?, MinMaxBounds.Ints> objects, Map<Stat<?>, MinMaxBounds.Ints> stats) {
		StatType<T> type2 = (StatType<T>) type;
		Map<T, MinMaxBounds.Ints> objects2 = (Map<T, MinMaxBounds.Ints>) objects;
		objects2.forEach((object, bounds) -> stats.put(type2.get(object), bounds));
	}

	@Override
	public MapCodec<StatisticCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			ServerStatsCounter stats = serverPlayer.getStats();
			return this.stats.entrySet().stream().allMatch(entry -> entry.getValue().matches(stats.getValue(entry.getKey())));
		} else return false;
	}
	
	public static class Builder {
		private Map<Stat<?>, MinMaxBounds.Ints> stats;
		
		public Builder addStat(Stat<?> stat, MinMaxBounds.Ints bounds) {
			stats.put(stat, bounds);
			return this;
		}
		
		public Builder addStats(Map<Stat<?>, MinMaxBounds.Ints> stats) {
			this.stats.putAll(stats);
			return this;
		}
		
		public StatisticCondition build() {
			return new StatisticCondition(new HashMap<>(stats));
		}
	}
}
