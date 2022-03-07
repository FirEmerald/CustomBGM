package firemerald.custombgm.networking.server;

import firemerald.api.betterscreens.GuiTileEntityGui;
import firemerald.api.betterscreens.TileEntityGUI;
import firemerald.api.core.networking.ServerPacket;
import firemerald.custombgm.common.CommonState;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileGUIClosedPacket extends ServerPacket
{
	private BlockPos tilePos;
	private ByteBuf tilePacket;

	public TileGUIClosedPacket() {}

	@SideOnly(Side.CLIENT)
	public TileGUIClosedPacket(GuiTileEntityGui gui)
	{
		this.tilePos = gui.getTilePos();
		this.tilePacket = Unpooled.buffer(0);
		gui.write(tilePacket);
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
	public void handleServerSide(EntityPlayerMP player)
	{
		CommonState.QUEUED_ACTIONS.add(() -> {
			TileEntity tile = player.world.getTileEntity(tilePos);
			if (tile instanceof TileEntityGUI)
			{
				TileEntityGUI guiTile = (TileEntityGUI) tile;
				guiTile.read(tilePacket.copy());
				guiTile.markDirty();
			}
		});
	}

	public static class Handler extends ServerPacket.Handler<TileGUIClosedPacket> {}
}
