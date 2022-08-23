package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.ISoundLoop;
import com.firemerald.custombgm.client.audio.LoopingSounds;
import com.firemerald.custombgm.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
    public void onConstruction(FMLConstructionEvent event)
    {
		super.onConstruction(event);
		CustomBGMAPI.instance = new CustomBGMAPI() {
			@Override
			public ISoundLoop grabSound(ResourceLocation name, SoundCategory category, boolean disablePan)
			{
				return LoopingSounds.grabSound(name, category, disablePan);
			}

			@Override
			public ISoundLoop playSound(ResourceLocation name, SoundCategory category, boolean disablePan)
			{
				return LoopingSounds.playSound(name, category, disablePan);
			}
		};
    }

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
		super.onPreInitialization(event);
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ReloadListener());
    }
}