package com.firemerald.custombgm.api;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BGM implements Comparable<BGM> {
	public static final Codec<BGM> CODEC = RecordCodecBuilder.create(instance ->
	instance.group(
			ResourceLocation.CODEC.fieldOf("sound").forGetter(BGM::sound),
			LoopType.CODEC.optionalFieldOf("loop", LoopType.TRUE).forGetter(BGM::loop)
			).apply(instance, BGM::new)
			);
	public static final Codec<List<BGM>> LIST_CODEC = CODEC.listOf();
	public static final StreamCodec<ByteBuf, BGM> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, BGM::sound,
			LoopType.STREAM_CODEC, BGM::loop,
			BGM::new
			);
	public static final StreamCodec<ByteBuf, List<BGM>> STREAM_LIST_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

	public final ResourceLocation sound;
	public final LoopType loop;

	public BGM(ResourceLocation sound, LoopType loop) {
		this.sound = sound;
		this.loop = loop;
	}

	public BGM(String sound, LoopType loop) {
		this(ResourceLocation.parse(sound), loop);
	}

	public BGM(String namespace, String path, LoopType loop) {
		this(ResourceLocation.fromNamespaceAndPath(namespace, path), loop);
	}

	public BGM(SoundEvent sound, LoopType loop) {
		this(sound.location(), loop);
	}

	public BGM(Supplier<SoundEvent> sound, LoopType loop) {
		this(sound.get(), loop);
	}

	public BGM(ResourceKey<SoundEvent> sound, LoopType loop) {
		this(sound.location(), loop);
	}

	public BGM(Holder<SoundEvent> sound, LoopType loop) {
		this(sound.getKey(), loop);
	}

	public BGM(Music music, LoopType loop) {
		this(music.getEvent(), loop);
	}

	public BGM(ResourceLocation sound) {
		this(sound, LoopType.TRUE);
	}

	public BGM(String sound) {
		this(sound, LoopType.TRUE);
	}

	public BGM(String namespace, String path) {
		this(ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	public BGM(SoundEvent sound) {
		this(sound, LoopType.TRUE);
	}

	public BGM(Supplier<SoundEvent> sound) {
		this(sound, LoopType.TRUE);
	}

	public BGM(ResourceKey<SoundEvent> sound) {
		this(sound, LoopType.TRUE);
	}

	public BGM(Holder<SoundEvent> sound) {
		this(sound, LoopType.TRUE);
	}

	public BGM(Music music) {
		this(music, LoopType.FALSE);
	}

	public BGM(MusicInfo music, LoopType loop) {
		this(music.music().getEvent().getKey().location(), loop);
	}

	public BGM(MusicInfo music) {
		this(music, LoopType.FALSE);
	}

	public BGM(BGM other) {
		this(other.sound, other.loop);
	}

	public ResourceLocation sound() {
		return sound;
	}

	public LoopType loop() {
		return loop;
	}

	@Override
	public int compareTo(BGM other) {
        int i = sound.compareTo(other.sound);
        if (i == 0) i = loop.compareTo(other.loop);
        return i;
	}

	@Override
	public int hashCode() {
		return sound.hashCode(); //unlikely to have the same sound but different loop type
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		else if (o == this) return true;
		else if (o.getClass() != this.getClass()) return false;
		else {
			BGM other = (BGM) o;
			return other.loop == loop && other.sound.equals(sound);
		}
	}

	@Override
	public String toString() {
		return "BGM<sound=" + sound.toString() + ",loop=" + loop.toString() + ">";
	}

	@OnlyIn(Dist.CLIENT)
	public Component getName() {
		WeighedSoundEvents soundEvent = Minecraft.getInstance().getSoundManager().getSoundEvent(sound);
		if (soundEvent != null && soundEvent.getSubtitle() != null) return soundEvent.getSubtitle();
		else return Component.literal(sound.toString());
	}

	public boolean containsSound(Sound sound) {
		WeighedSoundEvents soundEvent = Minecraft.getInstance().getSoundManager().getSoundEvent(this.sound);
		if (soundEvent != null) return soundEvent.containsSound(sound);
		else return false;
	}

	public boolean is(Sound sound, LoopType loop) {
		return loop == this.loop && containsSound(sound);
	}

	public boolean is(ResourceLocation sound, LoopType loop) {
		return loop == this.loop && sound.equals(this.sound);
	}

	public boolean is(BGM bgm) {
		return bgm != null && is(bgm.sound, bgm.loop);
	}

	@Override
	public BGM clone() {
		return new BGM(this);
	}
}
