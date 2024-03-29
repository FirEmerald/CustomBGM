package com.firemerald.custombgm.capability;

import com.firemerald.custombgm.api.capabilities.IPlayer;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class PlayerBase implements IPlayer
{
    private final LazyOptional<IPlayer> holder = LazyOptional.of(() -> this);
	public ResourceLocation musicOverride = null;
	protected int musicOverridePriority = Integer.MIN_VALUE;
	private boolean isInit = false;

	@Override
	public ResourceLocation getMusicOverride()
	{
		return musicOverride;
	}

	@Override
	public void addMusicOverride(ResourceLocation music, int priority)
	{
		if (priority > this.musicOverridePriority)
		{
			this.musicOverride = music;
			this.musicOverridePriority = priority;
		}
	}

	@Override
	public int getCurrentPriority()
	{
		return musicOverridePriority;
	}

	@Override
	public boolean getInit()
	{
		return isInit;
	}

	@Override
	public void setInit(boolean isInit)
	{
		this.isInit = isInit;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
        return CAP.orEmpty(cap, holder);
	}
}