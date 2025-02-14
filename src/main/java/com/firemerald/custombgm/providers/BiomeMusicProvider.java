package com.firemerald.custombgm.providers;

import java.util.Optional;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.distribution.DistributionUtil;
import com.firemerald.fecore.util.SimpleCollector;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;

public class BiomeMusicProvider extends BuiltInMusicProvider
{
	public static final MapCodec<BiomeMusicProvider> CODEC = getCodec(BiomeMusicProvider::new);

	protected BiomeMusicProvider(int priority, BGMProviderCondition condition, LoopType loop, float weight) {
		super(priority, condition, loop, weight);
	}

	protected BiomeMusicProvider(int priority, BGMProviderCondition condition, LoopType loop) {
		super(priority, condition, loop);
	}

	protected BiomeMusicProvider(int priority, BGMProviderCondition condition, float weight) {
		super(priority, condition, weight);
	}

	protected BiomeMusicProvider(int priority, BGMProviderCondition condition) {
		super(priority, condition);
	}

	@Override
	public BgmDistribution getMusic(PlayerConditionData player) {
		Holder<Biome> biome = player.getBiome();
		if (biome == null) return null;
        float volume = biome.value().getBackgroundMusicVolume();
        Optional<SimpleWeightedRandomList<Music>> optional = biome.value().getBackgroundMusic();
        if (optional.isPresent() && condition.test(player)) {
            return new BgmDistribution(DistributionUtil.get(optional.get().unwrap().stream().collect(SimpleCollector.toFloatMap(
        			wrapper -> new BGM(wrapper.data().getEvent().getKey().location(), loop),
        			wrapper -> (float) wrapper.getWeight().asInt()
        			))), volume);
        } else return null;
	}

	@Override
	public MapCodec<BiomeMusicProvider> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends BuilderBase<BiomeMusicProvider, Builder> {
		public Builder() {
			super();
		}

		public Builder(BiomeMusicProvider derive) {
			super(derive);
		}

		@Override
		public BiomeMusicProvider build() {
			return new BiomeMusicProvider(priority, condition, loop, weight);
		}
	}
}