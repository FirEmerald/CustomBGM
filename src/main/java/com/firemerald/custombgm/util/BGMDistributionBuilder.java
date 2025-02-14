package com.firemerald.custombgm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.fecore.distribution.DistributionUtil;
import com.firemerald.fecore.distribution.IDistribution;

import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

public class BGMDistributionBuilder {
	private final Map<BGM, Float> sounds = new HashMap<>();
	private String defaultNamespace;
	private LoopType defaultLoop;
	private float defaultWeight;

	public BGMDistributionBuilder() {
		this(LoopType.TRUE);
	}

	public BGMDistributionBuilder(String defaultNamespace) {
		this(defaultNamespace, LoopType.TRUE);
	}

	public BGMDistributionBuilder(LoopType defaultLoop) {
		this(defaultLoop, 1f);
	}

	public BGMDistributionBuilder(String defaultNamespace, LoopType defaultLoop) {
		this(defaultNamespace, defaultLoop, 1f);
	}

	public BGMDistributionBuilder(float defaultWeight) {
		this(LoopType.TRUE, defaultWeight);
	}

	public BGMDistributionBuilder(String defaultNamespace, float defaultWeight) {
		this(defaultNamespace, LoopType.TRUE, defaultWeight);
	}

	public BGMDistributionBuilder(LoopType defaultLoop, float defaultWeight) {
		this("minecraft", defaultLoop, defaultWeight);
	}

	public BGMDistributionBuilder(String defaultNamespace, LoopType defaultLoop, float defaultWeight) {
		this.defaultNamespace = defaultNamespace;
		this.defaultLoop = defaultLoop;
		this.defaultWeight = defaultWeight;
	}

	public BGMDistributionBuilder setNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
		return this;
	}

	public BGMDistributionBuilder setLooped(LoopType defaultLoop) {
		this.defaultLoop = defaultLoop;
		return this;
	}

	public BGMDistributionBuilder setWeight(float defaultWeight) {
		this.defaultWeight = defaultWeight;
		return this;
	}

	public BGMDistributionBuilder add(BGM bgm, float weight) {
		sounds.put(bgm, weight);
		return this;
	}

	public BGMDistributionBuilder add(ResourceLocation sound, LoopType loop, float weight) {
		return add(new BGM(sound, loop), weight);
	}

	public BGMDistributionBuilder add(String namespace, String path, LoopType loop, float weight) {
		return add(ResourceLocation.fromNamespaceAndPath(namespace, path), loop, weight);
	}

	public BGMDistributionBuilder add(String sound, LoopType loop, float weight) {
		return add(defaultNamespace, sound, loop, weight);
	}

	public BGMDistributionBuilder add(SoundEvent sound, LoopType loop, float weight) {
		return add(new BGM(sound, loop), weight);
	}

	public BGMDistributionBuilder add(Supplier<SoundEvent> sound, LoopType loop, float weight) {
		return add(new BGM(sound, loop), weight);
	}

	public BGMDistributionBuilder add(ResourceKey<SoundEvent> sound, LoopType loop, float weight) {
		return add(new BGM(sound, loop), weight);
	}

	public BGMDistributionBuilder add(Holder<SoundEvent> sound, LoopType loop, float weight) {
		return add(new BGM(sound, loop), weight);
	}

	public BGMDistributionBuilder add(Music music, LoopType loop, float weight) {
		return add(new BGM(music, loop), weight);
	}

	public BGMDistributionBuilder add(ResourceLocation sound, float weight) {
		return add(new BGM(sound, defaultLoop), weight);
	}

	public BGMDistributionBuilder add(String namespace, String path, float weight) {
		return add(ResourceLocation.fromNamespaceAndPath(namespace, path), weight);
	}

	public BGMDistributionBuilder add(String sound, float weight) {
		return add(defaultNamespace, sound, weight);
	}

	public BGMDistributionBuilder add(SoundEvent sound, float weight) {
		return add(new BGM(sound, defaultLoop), weight);
	}

	public BGMDistributionBuilder add(Supplier<SoundEvent> sound, float weight) {
		return add(new BGM(sound, defaultLoop), weight);
	}

	public BGMDistributionBuilder add(ResourceKey<SoundEvent> sound, float weight) {
		return add(new BGM(sound, defaultLoop), weight);
	}

	public BGMDistributionBuilder add(Holder<SoundEvent> sound, float weight) {
		return add(new BGM(sound, defaultLoop), weight);
	}

	public BGMDistributionBuilder add(Music music, float weight) {
		return add(new BGM(music, defaultLoop), weight);
	}

	public BGMDistributionBuilder add(MusicInfo music, LoopType loop, float weight) {
		return add(new BGM(music, loop), weight);
	}

	public BGMDistributionBuilder add(MusicInfo music, float weight) {
		return add(new BGM(music, defaultLoop), weight);
	}

	public BGMDistributionBuilder add(BGM bgm) {
		return add(bgm, defaultWeight);
	}

	public BGMDistributionBuilder add(ResourceLocation sound, LoopType loop) {
		return add(new BGM(sound, loop));
	}

	public BGMDistributionBuilder add(String namespace, String path, LoopType loop) {
		return add(ResourceLocation.fromNamespaceAndPath(namespace, path), loop);
	}

	public BGMDistributionBuilder add(String sound, LoopType loop) {
		return add(defaultNamespace, sound, loop);
	}

	public BGMDistributionBuilder add(SoundEvent sound, LoopType loop) {
		return add(new BGM(sound, loop));
	}

	public BGMDistributionBuilder add(Supplier<SoundEvent> sound, LoopType loop) {
		return add(new BGM(sound, loop));
	}

	public BGMDistributionBuilder add(ResourceKey<SoundEvent> sound, LoopType loop) {
		return add(new BGM(sound, loop));
	}

	public BGMDistributionBuilder add(Holder<SoundEvent> sound, LoopType loop) {
		return add(new BGM(sound, loop));
	}

	public BGMDistributionBuilder add(Music music, LoopType loop) {
		return add(new BGM(music, loop));
	}

	public BGMDistributionBuilder add(ResourceLocation sound) {
		return add(new BGM(sound, defaultLoop));
	}

	public BGMDistributionBuilder add(String namespace, String path) {
		return add(ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	public BGMDistributionBuilder add(String sound) {
		return add(defaultNamespace, sound);
	}

	public BGMDistributionBuilder add(SoundEvent sound) {
		return add(new BGM(sound, defaultLoop));
	}

	public BGMDistributionBuilder add(Supplier<SoundEvent> sound) {
		return add(new BGM(sound, defaultLoop));
	}

	public BGMDistributionBuilder add(ResourceKey<SoundEvent> sound) {
		return add(new BGM(sound, defaultLoop));
	}

	public BGMDistributionBuilder add(Holder<SoundEvent> sound) {
		return add(new BGM(sound, defaultLoop));
	}

	public BGMDistributionBuilder add(Music music) {
		return add(new BGM(music, defaultLoop));
	}

	public BGMDistributionBuilder add(MusicInfo music, LoopType loop) {
		return add(new BGM(music, loop));
	}

	public BGMDistributionBuilder add(MusicInfo music) {
		return add(new BGM(music, defaultLoop));
	}

	public IDistribution<BGM> build() {
		return DistributionUtil.get(sounds);
	}
}
