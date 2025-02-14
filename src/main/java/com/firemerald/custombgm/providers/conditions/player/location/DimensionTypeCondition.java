package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.providers.conditions.holderset.HolderCondition;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypeCondition extends HolderCondition<DimensionType> {
	public static final MapCodec<DimensionTypeCondition> CODEC = getCodec(Registries.DIMENSION_TYPE, "dimension_type", DimensionTypeCondition::new);

	private DimensionTypeCondition(HolderSet<DimensionType> holderSet) {
		super(holderSet);
	}

	@Override
	public Holder<DimensionType> getHolder(PlayerConditionData player) {
		return player.player == null ? null : player.player.level().dimensionTypeRegistration();
	}

	@Override
	public MapCodec<DimensionTypeCondition> codec() {
		return CODEC;
	}

	public static class Builder extends HolderCondition.Builder<DimensionType, DimensionTypeCondition, Builder> {
		public Builder(Provider provider) {
			super(provider, Registries.DIMENSION_TYPE);
		}

		@Override
		public DimensionTypeCondition build() {
			return new DimensionTypeCondition(holderSet);
		}
	}
}