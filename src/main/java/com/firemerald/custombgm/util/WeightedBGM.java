package com.firemerald.custombgm.util;

import java.util.List;
import java.util.Map;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.LoopType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class WeightedBGM extends BGM {
	public static final Codec<WeightedBGM> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
				ResourceLocation.CODEC.fieldOf("sound").forGetter(WeightedBGM::sound),
				LoopType.CODEC.optionalFieldOf("loop", LoopType.TRUE).forGetter(WeightedBGM::loop),
				Codec.FLOAT.optionalFieldOf("weight", 1f).forGetter(WeightedBGM::weight)
				).apply(instance, WeightedBGM::new)
		);
	public static final Codec<List<WeightedBGM>> LIST_CODEC = CODEC.listOf();
	public static final StreamCodec<ByteBuf, WeightedBGM> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, WeightedBGM::sound,
			LoopType.STREAM_CODEC, WeightedBGM::loop,
			ByteBufCodecs.FLOAT, WeightedBGM::weight,
			WeightedBGM::new
			);
	public static final StreamCodec<ByteBuf, List<WeightedBGM>> STREAM_LIST_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
	
	public final float weight;
	
	public WeightedBGM(ResourceLocation sound, LoopType loop, float weight) {
		super(sound, loop);
		this.weight = weight;
	}

	public WeightedBGM(BGM bgm, float weight) {
		this(bgm.sound(), bgm.loop(), weight);
	}

	public WeightedBGM(BGM bgm) {
		this(bgm, 1f);
	}

	public WeightedBGM(Map.Entry<BGM, Float> entry) {
		this(entry.getKey(), entry.getValue());
	}

	public WeightedBGM(ResourceLocation sound, SoundProperties properties, LoopType currentLoop, float currentWeight) {
		this(sound, properties.getLoop(currentLoop), properties.getWeight(currentWeight));
	}

	public WeightedBGM(ResourceLocation sound, SoundProperties properties) {
		this(sound, properties.getLoop(), properties.getWeight());
	}
	
	public WeightedBGM(WeightedBGM other) {
		this(other.sound, other.loop, other.weight);
	}
	
	public float weight() {
		return weight;
	}

	@Override
	public int compareTo(BGM other) {
        int i = sound.compareTo(other.sound);
        if (i == 0) i = loop.compareTo(other.loop);
        if (i == 0 && other instanceof WeightedBGM weighted) i = Float.compare(weight, weighted.weight);
        return i;
	}

	public Pair<BGM, Float> pair() {
		return new Pair<>(this, weight);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		else if (o == this) return true;
		else if (o.getClass() != this.getClass()) return false;
		else {
			WeightedBGM other = (WeightedBGM) o;
			return other.loop == loop && other.weight == weight && other.sound.equals(sound);
		}
	}
	
	@Override
	public String toString() {
		return "WeightedBGM<sound=" + sound.toString() + ",loop=" + loop.toString() + ",weight=" + weight + ">";
	}
	
	@Override
	public WeightedBGM clone() {
		return new WeightedBGM(this);
	}
}
