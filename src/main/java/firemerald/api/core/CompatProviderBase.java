package firemerald.api.core;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public abstract class CompatProviderBase implements ICompatProvider
{
	public final ArtifactVersion version;
	protected boolean isPresent = false;

	public CompatProviderBase(ArtifactVersion version)
	{
		this.version = version;
	}

	@Override
	public ArtifactVersion getVersion()
	{
		return version;
	}

	@Override
	public boolean isPresent()
	{
		return isPresent;
	}

	@Override
	public void setPresent()
	{
		isPresent = true;
	}
}