package com.firemerald.custombgm.client.audio;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class CustomBGMSoundInstance implements SoundInstance {
	public static CustomBGMSoundInstance of(ResourceLocation location, boolean loop, Sound sound) {
		return new CustomBGMSoundInstance(location, loop, sound);
	}

	public static CustomBGMSoundInstance of(ResourceLocation location, boolean loop, RandomSource random, SoundManager handler) {
		Sound sound;
        if (location.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
        else {
        	WeighedSoundEvents weighedsoundevents = handler.getSoundEvent(location);
        	if (weighedsoundevents == null) sound = SoundManager.EMPTY_SOUND;
        	else sound = weighedsoundevents.getSound(random);
        }
		return new CustomBGMSoundInstance(location, loop, sound);
	}

	public final ResourceLocation location;
	public final boolean loop;
	public final Sound sound;

	public CustomBGMSoundInstance(ResourceLocation location, boolean loop, Sound sound) {
		this.location = location;
		this.loop = loop;
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
		return 1;
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

}
