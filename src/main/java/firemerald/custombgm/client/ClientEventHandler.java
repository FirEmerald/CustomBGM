package firemerald.custombgm.client;

import firemerald.api.betterscreens.ScissorUtil;
import firemerald.custombgm.api.Capabilities;
import firemerald.custombgm.client.audio.LoopingSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientEventHandler
{
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		if (event.phase == Phase.START)
		{
			LoopingSounds.update();
			EntityPlayer player = Minecraft.getMinecraft().player;
			ClientState.clientPlayer = (player != null) ? player.getCapability(Capabilities.player, null) : null;
		}
		else
		{
			if (ClientState.currentBGM != null) ClientState.currentBGM.tick(true);
			if (Minecraft.getMinecraft().world != null) while (!ClientState.QUEUED_ACTIONS.isEmpty()) ClientState.QUEUED_ACTIONS.poll().run();
			else ClientState.QUEUED_ACTIONS.clear();
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)
	{
		if (event.phase == Phase.START) ScissorUtil.clearScissor();
	}
}