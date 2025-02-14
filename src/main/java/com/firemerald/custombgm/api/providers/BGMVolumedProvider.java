package com.firemerald.custombgm.api.providers;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.api.providers.volume.BGMProviderVolume;
import com.firemerald.custombgm.api.providers.volume.ConstantVolume;
import com.firemerald.fecore.distribution.IDistribution;

public abstract class BGMVolumedProvider extends BGMProvider {
	public final BGMProviderVolume volume;

	public BGMVolumedProvider(int priority, BGMProviderCondition condition, BGMProviderVolume volume) {
		super(priority, condition);
		this.volume = volume;
	}

	@Override
	public BgmDistribution getMusic(PlayerConditionData player) {
		IDistribution<BGM> distribution = this.getDistribution(player);
		return distribution == null ? null : new BgmDistribution(distribution, volume.getVolume(player));
	}

	public abstract IDistribution<BGM> getDistribution(PlayerConditionData player);

	public static abstract class BuilderBase<T extends BGMVolumedProvider, U extends BuilderBase<T, U>> extends BGMProvider.BuilderBase<T, U> {
		protected BGMProviderVolume volume;

		public BuilderBase() {
			super();
			volume = ConstantVolume.DEFAULT;
		}

		public BuilderBase(T derive) {
			super(derive);
			volume = derive.volume;
		}

		public U setVolume(BGMProviderVolume volume) {
			this.volume = volume;
			return me();
		}

		public U setVolume(float volume) {
			return setVolume(new ConstantVolume(volume));
		}
	}
}