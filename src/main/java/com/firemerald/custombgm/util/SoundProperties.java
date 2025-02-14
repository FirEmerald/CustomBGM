package com.firemerald.custombgm.util;

import java.util.Optional;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SoundProperties(Optional<LoopType> loop, Optional<Float> weight) {
	public static final Codec<SoundProperties> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
				LoopType.CODEC.optionalFieldOf("loop").forGetter(SoundProperties::loop),
				Codec.FLOAT.optionalFieldOf("weight").forGetter(SoundProperties::weight)
				).apply(instance, SoundProperties::new)
	);
	public static final Codec<SoundProperties> ADAPTABLE_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<T> encode(SoundProperties input, DynamicOps<T> ops, T prefix) {
			if (input.loop.isEmpty()) {
				if (input.weight.isPresent()) return Codec.FLOAT.encode(input.weight.get(), ops, prefix); //only weight
			} else {
				if (input.weight.isEmpty()) return LoopType.CODEC.encode(input.loop.get(), ops, prefix); //only loop
			}
			return CODEC.encode(input, ops, prefix);
		}

		@Override
		public <T> DataResult<Pair<SoundProperties, T>> decode(DynamicOps<T> ops, T input) {
			DataResult<Pair<Float, T>> decodeWeight = Codec.FLOAT.decode(ops, input);
			if (decodeWeight.isSuccess()) return Codecs.mapResult(decodeWeight, SoundProperties::new);
			DataResult<Pair<LoopType, T>> decodeLoop = LoopType.CODEC.decode(ops, input);
			if (decodeLoop.isSuccess()) return Codecs.mapResult(decodeLoop, SoundProperties::new);
			DataResult<Pair<SoundProperties, T>> decodeFull = CODEC.decode(ops, input);
			if (decodeFull.isSuccess()) return decodeFull;
			return DataResult.error(() -> "Could not parse weighted bgm sound properties: Expected a boolean, float, or map{loop:boolean,weight:float}");
		}
	};

	public SoundProperties(LoopType loop) {
		this(Optional.of(loop), Optional.empty());
	}

	public SoundProperties(float weight) {
		this(Optional.empty(), Optional.of(weight));
	}

	public SoundProperties(LoopType loop, float weight, LoopType defaultLoop, Float defaultWeight) {
		this(
				defaultLoop == loop ? Optional.empty() : Optional.of(loop),
				defaultWeight != null && defaultWeight == weight ? Optional.empty() : Optional.of(weight));
	}

	public SoundProperties(BGM bgm, float weight, LoopType defaultLoop, Float defaultWeight) {
		this(bgm.loop(), weight, defaultLoop, defaultWeight);
	}

	public SoundProperties(LoopType loop, LoopType defaultLoop) {
		this(
				defaultLoop == loop ? Optional.empty() : Optional.of(loop),
				Optional.empty());
	}

	public SoundProperties(BGM bgm, LoopType defaultLoop) {
		this(bgm.loop(), defaultLoop);
	}

	public boolean isEmpty() {
		return loop.isEmpty()  && weight.isEmpty();
	}

	public LoopType getLoop(LoopType currentLoop) {
		return loop.orElse(currentLoop);
	}

	public LoopType getLoop() {
		return getLoop(LoopType.TRUE);
	}

	public float getWeight(float currentWeight) {
		return weight.orElse(1f) * currentWeight;
	}

	public float getWeight() {
		return getWeight(1);
	}
}