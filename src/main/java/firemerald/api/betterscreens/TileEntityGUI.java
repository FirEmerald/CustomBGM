package firemerald.api.betterscreens;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityGUI extends TileEntity
{
	public abstract void read(ByteBuf buf);

	public abstract void write(ByteBuf buf);

	@SideOnly(Side.CLIENT)
	public abstract GuiTileEntityGui getScreen();
}