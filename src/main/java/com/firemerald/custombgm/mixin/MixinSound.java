package com.firemerald.custombgm.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.firemerald.custombgm.client.audio.IExtendedSound;

import net.minecraft.client.resources.sounds.Sound;

@Mixin(Sound.class)
public class MixinSound implements IExtendedSound
{
	public int loopStart = 0, loopEnd = 0;

	@Override
	public int getLoopStart()
	{
		return loopStart;
	}

	@Override
	public int getLoopEnd()
	{
		return loopEnd;
	}

	@Override
	public void setLoopStart(int start)
	{
		loopStart = start;
	}

	@Override
	public void setLoopEnd(int end)
	{
		loopEnd = end;
	}
}