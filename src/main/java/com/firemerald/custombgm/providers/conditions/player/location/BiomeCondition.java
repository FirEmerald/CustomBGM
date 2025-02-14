package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.providers.conditions.holderset.HolderCondition;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

public class BiomeCondition extends HolderCondition<Biome> {
	public static final MapCodec<BiomeCondition> CODEC = getCodec(Registries.BIOME, "biome", BiomeCondition::new);

	public BiomeCondition(HolderSet<Biome> holderSet) {
		super(holderSet);
	}

	@Override
	public MapCodec<BiomeCondition> codec() {
		return CODEC;
	}

	@Override
	public Holder<Biome> getHolder(PlayerConditionData playerData) {
		return playerData.getBiome();
	}

    public static class Builder extends HolderCondition.Builder<Biome, BiomeCondition, Builder> {
    	public Builder(Provider provider) {
			super(provider, Registries.BIOME);
		}

		@Override
        public BiomeCondition build() {
            return new BiomeCondition(this.holderSet);
        }
    }
}