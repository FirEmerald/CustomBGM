package com.firemerald.custombgm.providers.conditions.player.level;

import java.util.HashMap;
import java.util.Map;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.EnumCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;

public record LightLevelCondition(Map<LightLayer, MinMaxBounds.Ints> levels) implements BGMProviderPlayerCondition {
	public static final MapCodec<LightLevelCondition> CODEC = Codec.unboundedMap(
			new EnumCodec<>(LightLayer.values()), 
			MinMaxBounds.Ints.CODEC
			).fieldOf("levels").xmap(LightLevelCondition::new, LightLevelCondition::levels);

	@Override
	public MapCodec<LightLevelCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return levels.entrySet().stream().allMatch(entry -> entry.getValue().matches(player.level().getBrightness(entry.getKey(), player.blockPosition())));
	}
	
	public static class Builder {
		private Map<LightLayer, MinMaxBounds.Ints> levels;
		
		public Builder addLevel(LightLayer level, MinMaxBounds.Ints bounds) {
			levels.put(level, bounds);
			return this;
		}
		
		public Builder addLevels(Map<LightLayer, MinMaxBounds.Ints> levels) {
			this.levels.putAll(levels);
			return this;
		}
		
		public LightLevelCondition build() {
			return new LightLevelCondition(new HashMap<>(levels));
		}
	}
}
