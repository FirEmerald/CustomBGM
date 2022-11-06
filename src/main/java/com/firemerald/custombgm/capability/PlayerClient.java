package com.firemerald.custombgm.capability;

import net.minecraft.resources.ResourceLocation;

public class PlayerClient extends PlayerBase
{
	private ResourceLocation serverMusic = null;
	private int serverPriority = Integer.MIN_VALUE;

	@Override
	public ResourceLocation getLastMusicOverride()
	{
		return musicOverride;
	}

	@Override
	public void setLastMusicOverride(ResourceLocation music) {}

	@Override
	public void clearMusicOverride()
	{
		this.musicOverride = serverMusic;
		this.musicOverridePriority = serverPriority;
	}

	@Override
	public void setServerMusic(ResourceLocation music, int priority)
	{
		this.serverMusic = music;
		this.serverPriority = priority;
	}
}