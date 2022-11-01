package com.firemerald.custombgm.capability;

import com.firemerald.custombgm.api.capabilities.IPlayer;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerBase implements IPlayer
{
    private final LazyOptional<IPlayer> holder = LazyOptional.of(() -> this);
	public ResourceLocation musicOverride = null;
	private boolean isInit = false;

	@Override
	public void addMusicOverride(ResourceLocation music, int priority)
	{
		this.musicOverride = music;
	}

	@Override
	public void clearMusicOverride()
	{
		this.musicOverride = null;
	}

	@Override
	public ResourceLocation getMusicOverride()
	{
		return musicOverride;
	}

	@Override
	public ResourceLocation getLastMusicOverride()
	{
		return musicOverride;
	}

	@Override
	public void setLastMusicOverride(ResourceLocation music) {}

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

	@Override
	public int getCurrentPriority()
	{
		return 0;
	}
}