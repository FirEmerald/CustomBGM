package com.firemerald.custombgm.providers;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.distribution.SingletonWeightedDistribution;
import com.google.common.base.Optional;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.sounds.Music;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;

public class VanillaMusicProvider extends BuiltInMusicProvider
{
	public static final MapCodec<VanillaMusicProvider> CODEC = getCodec(VanillaMusicProvider::new);

	protected VanillaMusicProvider(int priority, BGMProviderCondition condition, LoopType loop, float weight) {
		super(priority, condition, loop, weight);
	}

	protected VanillaMusicProvider(int priority, BGMProviderCondition condition, LoopType loop) {
		super(priority, condition, loop);
	}

	protected VanillaMusicProvider(int priority, BGMProviderCondition condition, float weight) {
		super(priority, condition, weight);
	}

	protected VanillaMusicProvider(int priority, BGMProviderCondition condition) {
		super(priority, condition);
	}

	@Override
	public BgmDistribution getMusic(PlayerConditionData player) {
		return EffectiveSide.get() == LogicalSide.CLIENT ? getMusicClient(player) : null;
	}

	@OnlyIn(Dist.CLIENT)
	public BgmDistribution getMusicClient(PlayerConditionData player) {
		Optional<MusicInfo> musicInfo = player.getVanillaBGM();
		if (musicInfo.isPresent() && condition.test(player)) {
			Music music = musicInfo.get().music();
			if (music == null) return BgmDistribution.EMPTY;
			else return new BgmDistribution(new SingletonWeightedDistribution<>(new BGM(music, loop), weight), musicInfo.get().volume());
		} else return null;
	}

	@Override
	public MapCodec<VanillaMusicProvider> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends BuilderBase<VanillaMusicProvider, Builder> {
		public Builder() {
			super();
		}

		public Builder(VanillaMusicProvider derive) {
			super(derive);
		}

		@Override
		public VanillaMusicProvider build() {
			return new VanillaMusicProvider(priority, condition, loop, weight);
		}
	}
}