package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.providers.conditions.IDCondition;
import com.mojang.serialization.MapCodec;

import net.minecraft.resources.ResourceLocation;

public class DimensionCondition extends IDCondition {
	public static final MapCodec<DimensionCondition> CODEC = getCodec("dimensions", DimensionCondition::new);

	private DimensionCondition(ResourceLocation[] ids) {
		super(ids);
	}

	@Override
	public boolean test(PlayerConditionData player) {
		if (player.player == null) return false;
		ResourceLocation dimension = player.player.level().dimension().location();
		for (ResourceLocation id : ids)
			if (dimension.equals(id)) return true;
		return false;
	}

	@Override
	public MapCodec<DimensionCondition> codec() {
		return CODEC;
	}

	public static class Builder extends IDCondition.Builder<DimensionCondition, Builder> {
		public Builder() {
			super();
		}

		public Builder(DimensionCondition derive) {
			super(derive);
		}

		@Override
		public DimensionCondition build() {
			return new DimensionCondition(ids.toArray(ResourceLocation[]::new));
		}
	}
}