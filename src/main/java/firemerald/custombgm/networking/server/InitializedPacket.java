package firemerald.custombgm.networking.server;

import firemerald.api.core.networking.ServerPacket;
import firemerald.custombgm.api.Capabilities;
import firemerald.custombgm.api.IPlayer;
import firemerald.custombgm.common.CommonState;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class InitializedPacket extends ServerPacket
{
	public InitializedPacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public void handleServerSide(EntityPlayerMP player)
	{
		CommonState.QUEUED_ACTIONS.add(() -> {
			IPlayer lsPlayer = player.getCapability(Capabilities.player, null);
			if (lsPlayer != null) lsPlayer.setInit(true);
		});
	}

	public static class Handler extends ServerPacket.Handler<InitializedPacket> {}
}
