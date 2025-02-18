package com.firemerald.custombgm.api.providers;

import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.providers.conditions.constant.TrueCondition;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public abstract class BGMProvider implements Comparable<BGMProvider> {
    public static final Codec<BGMProvider> CODEC = Codecs.byNameCodec(CustomBGMRegistries.providerCodecs).dispatch(BGMProvider::codec, MapCodec::codec);

	public final int priority;
	public final BGMProviderCondition condition;

	public BGMProvider(int priority, BGMProviderCondition condition) {
		this.priority = priority;
		this.condition = condition;
	}

	public abstract BgmDistribution getMusic(PlayerConditionData player);

	@Override
	public final int compareTo(BGMProvider other) {
		return priority - other.priority;
	}

	public abstract MapCodec<? extends BGMProvider> codec();

	public static abstract class BuilderBase<T extends BGMProvider, U extends BuilderBase<T, U>> {
		protected int priority;
		protected BGMProviderCondition condition;

		public BuilderBase() {
			priority = 0;
			condition = TrueCondition.INSTANCE;
		}

		public BuilderBase(T derive) {
			priority = derive.priority;
			condition = derive.condition;
		}

		@SuppressWarnings("unchecked")
		public U me() {
			return (U) this;
		}

		public U setPriority(int priority) {
			this.priority = priority;
			return me();
		}

		public U setCondition(BGMProviderCondition condition) {
			if (condition == null) throw new IllegalStateException("Attempted to set to a null BGMProviderCondition");
			this.condition = condition;
			return me();
		}

		public abstract T build();
	}
}