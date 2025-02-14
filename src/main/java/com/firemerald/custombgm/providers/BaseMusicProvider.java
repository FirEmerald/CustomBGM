package com.firemerald.custombgm.providers;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.providers.BGMVolumedProvider;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.api.providers.volume.BGMProviderVolume;
import com.firemerald.custombgm.api.providers.volume.ConstantVolume;
import com.firemerald.custombgm.codecs.BGMDistributionCodec;
import com.firemerald.custombgm.providers.conditions.constant.TrueCondition;
import com.firemerald.fecore.distribution.EmptyDistribution;
import com.firemerald.fecore.distribution.IDistribution;
import com.firemerald.fecore.distribution.SingletonUnweightedDistribution;
import com.firemerald.fecore.distribution.SingletonWeightedDistribution;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BaseMusicProvider extends BGMVolumedProvider
{
	public static final MapCodec<BaseMusicProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				Codec.INT.optionalFieldOf("priority", 0).forGetter(provider -> provider.priority),
				BGMProviderCondition.CODEC.optionalFieldOf("condition", TrueCondition.INSTANCE).forGetter(provider -> provider.condition),
				BGMProviderVolume.ADAPTABLE_CODEC.optionalFieldOf("volume", ConstantVolume.DEFAULT).forGetter(provider -> provider.volume),
				BGMDistributionCodec.INSTANCE.fieldOf("music").forGetter(provider -> provider.music)
				).apply(instance, BaseMusicProvider::new)
		);

	public final IDistribution<BGM> music;

	private BaseMusicProvider(int priority, BGMProviderCondition condition, BGMProviderVolume volume, IDistribution<BGM> music) {
		super(priority, condition, volume);
		this.music = music;
	}

	@Override
	public IDistribution<BGM> getDistribution(PlayerConditionData player) {
		return condition.test(player) ? music : null;
	}

	@Override
	public MapCodec<BaseMusicProvider> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends BuilderBase<BaseMusicProvider, Builder> {
		public Builder() {
			super();
		}

		public Builder(BaseMusicProvider derive) {
			super(derive);
		}

		@Override
		public BaseMusicProvider build() {
			return new BaseMusicProvider(priority, condition, volume, music);
		}
	}

	public abstract static class BuilderBase<T extends BaseMusicProvider, U extends BuilderBase<T, U>> extends BGMVolumedProvider.BuilderBase<T, U> {
		protected IDistribution<BGM> music;

		public BuilderBase() {
			super();
			music = EmptyDistribution.get();
		}

		public BuilderBase(T derive) {
			super(derive);
			music = derive.music;
		}

		public U setMusic(IDistribution<BGM> music) {
			if (music == null) throw new IllegalStateException("Attempted to set to a null Distribution");
			this.music = music;
			return me();
		}

		public U setMusic(BGM music, float weight) {
			if (music == null) throw new IllegalStateException("Attempted to set to a null music");
			return setMusic(new SingletonWeightedDistribution<>(music, weight));
		}

		public U setMusic(BGM music) {
			if (music == null) throw new IllegalStateException("Attempted to set to a null music");
			return setMusic(new SingletonUnweightedDistribution<>(music));
		}
	}
}