package firemerald.custombgm.capability;

import net.minecraft.util.ResourceLocation;

public class PlayerServer extends PlayerBase
{
	public int musicOverridePriority = Integer.MIN_VALUE;

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

}