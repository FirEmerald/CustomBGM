package com.firemerald.custombgm.client.audio;

import java.util.ArrayList;
import java.util.List;

import com.firemerald.custombgm.api.ISoundLoop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class LoopingSounds
{
	public static final List<ISoundLoop> PLAYING = new ArrayList<>();

	public static void update()
	{
		for (ISoundLoop player : PLAYING.toArray(new ISoundLoop[0]))
		{
			if (player.isStopped()) PLAYING.remove(player);
			else player.updateCategoryVolume();
		}
	}

	public static void stopAll()
	{
		for (ISoundLoop player : PLAYING.toArray(new ISoundLoop[0])) player.stopSound();
		PLAYING.clear();
	}

	public static void pauseAll()
	{
		for (ISoundLoop player : PLAYING) player.pauseSound();
	}

	public static void resumeAll()
	{
		for (ISoundLoop player : PLAYING) player.resumeSound();
	}

	public static ISoundLoop playSound(ResourceLocation name, SoundSource category, boolean disablePan)
	{
		ISoundLoop loop = grabSound(name, category, disablePan);
		if (loop != null) loop.playSound();
		return loop;
	}

	public static ISoundLoop grabSound(ResourceLocation name, SoundSource category, boolean disablePan)
	{
		WeighedSoundEvents weightedSounds = Minecraft.getInstance().getSoundManager().getSoundEvent(name);
		if (weightedSounds == null) return null;
		else return OggSound.make(weightedSounds.getSound(), category, disablePan);
	}
}