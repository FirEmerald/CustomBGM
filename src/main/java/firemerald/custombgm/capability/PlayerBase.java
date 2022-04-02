package firemerald.custombgm.capability;

import firemerald.custombgm.api.IPlayer;
import net.minecraft.util.ResourceLocation;

public class PlayerBase implements IPlayer
{
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
}