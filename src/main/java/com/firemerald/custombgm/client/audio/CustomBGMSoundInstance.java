package com.firemerald.custombgm.client.audio;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class CustomBGMSoundInstance implements TickableSoundInstance {
	public static CustomBGMSoundInstance of(ResourceLocation location, boolean loop, float volume, Sound sound) {
		return new CustomBGMSoundInstance(location, loop, volume, sound);
	}

	public static CustomBGMSoundInstance of(ResourceLocation location, boolean loop, float volume, RandomSource random, SoundManager handler) {
		Sound sound;
        if (location.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
        else {
        	WeighedSoundEvents weighedsoundevents = handler.getSoundEvent(location);
        	if (weighedsoundevents == null) sound = SoundManager.EMPTY_SOUND;
        	else sound = weighedsoundevents.getSound(random);
        }
		return new CustomBGMSoundInstance(location, loop, volume, sound);
	}

	public final ResourceLocation location;
	public final boolean loop;
	public final Sound sound;
	public float volume;

	public CustomBGMSoundInstance(ResourceLocation location, boolean loop, float volume, Sound sound) {
		this.location = location;
		this.loop = loop;
		this.volume = volume;
		this.sound = sound;
	}

	@Override
	public ResourceLocation getLocation() {
		return location;
	}

	@Override
	public SoundSource getSource() {
		return SoundSource.MUSIC;
	}

	@Override
	public boolean isLooping() {
		return loop;
	}

	@Override
	public boolean isRelative() {
		return true;
	}

	@Override
	public int getDelay() {
		return 0;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public float getPitch() {
		return 1;
	}

	@Override
	public double getX() {
		return 0;
	}

	@Override
	public double getY() {
		return 0;
	}

	@Override
	public double getZ() {
		return 0;
	}

	@Override
	public Attenuation getAttenuation() {
		return Attenuation.NONE;
	}

	@Override
	public WeighedSoundEvents resolve(SoundManager handler) {
        if (this.location.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) {
            return SoundManager.INTENTIONALLY_EMPTY_SOUND_EVENT;
        } else {
            return handler.getSoundEvent(this.location);
        }
	}

	@Override
	public Sound getSound() {
		return sound;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public void tick() {}
}
