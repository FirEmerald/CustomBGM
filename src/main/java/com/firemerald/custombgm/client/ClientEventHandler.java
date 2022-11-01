package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.audio.LoopingSounds;
import com.firemerald.custombgm.providers.Providers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler
{
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		if (event.phase == Phase.START)
		{
			LoopingSounds.update();
			@SuppressWarnings("resource")
			Player player = Minecraft.getInstance().player;
			if (player == null) ClientState.clientPlayer = null;
			else ClientState.clientPlayer = IPlayer.getOrNull(player);
		}
		else if (ClientState.currentBGM != null)
		{
			ClientState.currentBGM.tick(true);
		}
	}
	
	@SubscribeEvent
	public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event)
	{
		event.registerReloadListener(new ReloadListener());
		event.registerReloadListener(Providers.forResourcePacks());
	}
}