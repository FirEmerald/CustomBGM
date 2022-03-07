package firemerald.custombgm.plugin.transformers;

import org.apache.logging.log4j.Logger;

import firemerald.api.core.plugin.StandardTransformer;
import firemerald.custombgm.plugin.Plugin;

public abstract class LSTransformer extends StandardTransformer
{
	public LSTransformer(boolean computeFrames)
	{
		super(computeFrames);
	}

	public LSTransformer(boolean computeFrames, boolean compute_maxs)
	{
		super(computeFrames, compute_maxs);
	}

	public LSTransformer(boolean skipFrames, boolean computeFrames, boolean compute_maxs)
	{
		super(computeFrames, computeFrames, compute_maxs);
	}

	@Override
	public Logger logger()
	{
		return Plugin.logger();
	}
}