package firemerald.custombgm.tileentity;

import java.util.stream.Stream;

import firemerald.api.betterscreens.GuiTileEntityGui;
import firemerald.custombgm.api.Capabilities;
import firemerald.custombgm.api.IPlayer;
import firemerald.custombgm.client.gui.GuiBGM;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBGM extends TileEntityEntityOperator<EntityPlayer>
{
	public ResourceLocation music;
	public int priority;

    public TileEntityBGM()
    {
    	super(EntityPlayer.class);
    }

	@Override
	public void update()
	{
		int prevCount = this.getSuccessCount();
		super.update();
		int count = this.getSuccessCount();
		if (count != prevCount) world.updateComparatorOutputLevel(getPos(), world.getBlockState(pos).getBlock());
	}

	@Override
	public boolean isActive()
	{
		return !world.isRemote && world.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	@Override
	public boolean operate(EntityPlayer player)
	{
		IPlayer iPlayer = player.getCapability(Capabilities.player, null);
		if (iPlayer != null)
		{
			iPlayer.addMusicOverride(music, priority);
			return true;
		}
		else return false;
	}

	@Override
	public Stream<? extends EntityPlayer> allEntities()
	{
		return world.playerEntities.stream();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		String music = tag.getString("music");
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = tag.getInteger("priority");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag = super.writeToNBT(tag);
		tag.setString("music", music == null ? "" : music.toString());
		tag.setInteger("priority", priority);
		return tag;
	}

	@Override
	public void read(ByteBuf buf)
	{
		super.read(buf);
		String music = ByteBufUtils.readUTF8String(buf);
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf)
	{
		super.write(buf);
		ByteBufUtils.writeUTF8String(buf, music == null ? "" : music.toString());
		buf.writeInt(priority);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiTileEntityGui getScreen()
	{
		return new GuiBGM(this.pos);
	}
}