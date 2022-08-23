package com.firemerald.custombgm.client;

import com.firemerald.custombgm.client.audio.LoopingSounds;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ReloadListener implements ResourceManagerReloadListener
{
	@Override
	public void onResourceManagerReload(ResourceManager manager)
	{
		//if (resourcePredicate.test(VanillaResourceType.SOUNDS)) LoopingSounds.loadInfos();
		LoopingSounds.loadInfos();
	}
}
