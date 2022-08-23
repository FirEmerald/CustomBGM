package com.firemerald.custombgm.capability;

import net.minecraft.resources.ResourceLocation;

public class PlayerServer extends PlayerBase
{
	private int musicOverridePriority = Integer.MIN_VALUE;
	private ResourceLocation synchronizedMusic = null;

	@Override
	public void addMusicOverride(ResourceLocation music, int priority)
	{
		if (priority > this.musicOverridePriority)
		{
			super.addMusicOverride(music, priority);
			this.musicOverridePriority = priority;
		}
	}

	@Override
	public void clearMusicOverride()
	{
		super.clearMusicOverride();
		this.musicOverridePriority = Integer.MIN_VALUE;
	}

	@Override
	public ResourceLocation getLastMusicOverride()
	{
		return synchronizedMusic;
	}

	@Override
	public void setLastMusicOverride(ResourceLocation music)
	{
		this.synchronizedMusic = music;
	}

}