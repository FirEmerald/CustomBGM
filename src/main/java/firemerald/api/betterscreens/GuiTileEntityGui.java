package firemerald.api.betterscreens;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public abstract class GuiTileEntityGui extends GuiBetterScreen
{
	public abstract BlockPos getTilePos();

	public abstract void read(ByteBuf buf);

	public abstract void write(ByteBuf buf);
}