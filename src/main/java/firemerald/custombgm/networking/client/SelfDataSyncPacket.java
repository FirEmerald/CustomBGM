package firemerald.custombgm.networking.client;

import firemerald.api.core.networking.ClientPacket;
import firemerald.custombgm.api.IPlayer;
import firemerald.custombgm.client.ClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SelfDataSyncPacket extends ClientPacket
{
	private ResourceLocation music = null;

	public SelfDataSyncPacket() {}

	public SelfDataSyncPacket(IPlayer player)
	{
		music = player.getMusicOverride();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		String music = ByteBufUtils.readUTF8String(buf);
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, music == null ? "" : music.toString());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide()
	{
		ClientState.QUEUED_ACTIONS.add(() -> {
			if (Minecraft.getMinecraft().player != null)
			{
				IPlayer player = ClientState.clientPlayer;
				if (player != null) player.addMusicOverride(music, Integer.MIN_VALUE);
			}
		});
	}

	public static class Handler extends ClientPacket.Handler<SelfDataSyncPacket> {}
}
