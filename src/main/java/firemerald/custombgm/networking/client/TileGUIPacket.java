package firemerald.custombgm.networking.client;

import firemerald.api.betterscreens.GuiTileEntityGui;
import firemerald.api.betterscreens.TileEntityGUI;
import firemerald.api.core.networking.ClientPacket;
import firemerald.custombgm.client.ClientState;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileGUIPacket extends ClientPacket
{
	private BlockPos tilePos;
	private ByteBuf tilePacket;

	public TileGUIPacket() {}

	public TileGUIPacket(TileEntityGUI tile)
	{
		this.tilePos = tile.getPos();
		this.tilePacket = Unpooled.buffer(0);
		tile.write(tilePacket);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		tilePos = new BlockPos(ByteBufUtils.readVarInt(buf, 5), buf.readByte() & 0xFF, ByteBufUtils.readVarInt(buf, 5));
		tilePacket = buf.copy();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, tilePos.getX(), 5);
		buf.writeByte(tilePos.getY());
		ByteBufUtils.writeVarInt(buf, tilePos.getZ(), 5);
		buf.writeBytes(tilePacket.copy());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide()
	{
		ClientState.QUEUED_ACTIONS.add(() -> {
			if (Minecraft.getMinecraft().world != null)
			{
				TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(tilePos);
				if (tile instanceof TileEntityGUI)
				{
					GuiTileEntityGui gui = ((TileEntityGUI) tile).getScreen();
					gui.read(tilePacket.copy());
					Minecraft.getMinecraft().displayGuiScreen(gui);
				}
			}
		});
	}

	public static class Handler extends ClientPacket.Handler<TileGUIPacket> {}
}
