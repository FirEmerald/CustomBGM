package com.firemerald.custombgm.providers;

import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.providers.conditions.constant.TrueCondition;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public abstract class BuiltInMusicProvider extends BGMProvider
{
	public static <T extends BuiltInMusicProvider> MapCodec<T> getCodec(Function4<Integer, BGMProviderCondition, LoopType, Float, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				Codec.INT.optionalFieldOf("priority", 0).forGetter(provider -> provider.priority),
				BGMProviderCondition.CODEC.optionalFieldOf("condition", TrueCondition.INSTANCE).forGetter(provider -> provider.condition),
				LoopType.CODEC.optionalFieldOf("loop", LoopType.FALSE).forGetter(provider -> provider.loop),
				Codec.FLOAT.optionalFieldOf("weight", 1f).forGetter(provider -> provider.weight)
				).apply(instance, constructor)
		);
	}

	public final LoopType loop;
	public final float weight;

	protected BuiltInMusicProvider(int priority, BGMProviderCondition condition, LoopType loop, float weight) {
		super(priority, condition);
		this.loop = loop;
		this.weight = weight;
	}

	protected BuiltInMusicProvider(int priority, BGMProviderCondition condition, LoopType loop) {
		this(priority, condition, loop, 1f);
	}

	protected BuiltInMusicProvider(int priority, BGMProviderCondition condition, float weight) {
		this(priority, condition, LoopType.FALSE, weight);
	}

	protected BuiltInMusicProvider(int priority, BGMProviderCondition condition) {
		this(priority, condition, LoopType.FALSE);
	}

	public abstract static class BuilderBase<T extends BuiltInMusicProvider, U extends BuilderBase<T, U>> extends BGMProvider.BuilderBase<T, U> {
		protected LoopType loop = LoopType.FALSE;
		protected float weight = 1f;

		public BuilderBase() {
			super();
		}

		public BuilderBase(T derive) {
			super(derive);
			loop = derive.loop;
			weight = derive.weight;
		}

		public U setLoop(LoopType loop) {
			this.loop = loop;
			return me();
		}

		public U setWeight(float weight) {
			this.weight = weight;
			return me();
		}
	}
}