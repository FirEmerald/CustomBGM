package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.audio.LoopingSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CustomBGMAPI.MOD_ID)
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
}