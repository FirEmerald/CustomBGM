package firemerald.custombgm.capability;

import firemerald.custombgm.api.IPlayer;
import net.minecraft.util.ResourceLocation;

public class PlayerBase implements IPlayer
{
	public ResourceLocation musicOverride = null;

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
}