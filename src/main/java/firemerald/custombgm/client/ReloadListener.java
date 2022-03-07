package firemerald.custombgm.client;

import java.util.function.Predicate;

import firemerald.custombgm.client.audio.LoopingSounds;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;

public class ReloadListener implements ISelectiveResourceReloadListener
{
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
	{
		if (resourcePredicate.test(VanillaResourceType.SOUNDS)) LoopingSounds.loadInfos();
	}
}
