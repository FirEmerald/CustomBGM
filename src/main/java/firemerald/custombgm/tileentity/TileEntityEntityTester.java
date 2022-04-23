package firemerald.custombgm.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import firemerald.api.betterscreens.GuiTileEntityGui;
import firemerald.custombgm.client.gui.GuiEntityTester;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityEntityTester extends TileEntityEntityOperator<Entity>
{
    public static final ResourceLocation PLAYER = new ResourceLocation("player");
	public List<ResourceLocation> enabled = new ArrayList<>();
	public short min = 1, max = Short.MAX_VALUE;
	public int count = 0;

    public TileEntityEntityTester()
    {
    	super(Entity.class);
    }

	@Override
	public void update()
	{
		int prevCount = count;
		super.update();
		int count = this.getSuccessCount();
		if (count < min || count > max) this.count = 0;
		else this.count = count + 1 - min;
		if (this.count != prevCount) world.updateComparatorOutputLevel(getPos(), world.getBlockState(pos).getBlock());
	}

	@Override
	public boolean isActive()
	{
		return !world.isRemote && world.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	@Override
	public boolean operate(Entity entity)
	{
		return enabled.contains(getId(entity));
	}

	@Override
	public Stream<? extends Entity> allEntities()
	{
		return world.loadedEntityList.stream();
	}

	public static ResourceLocation getId(Entity entity)
	{
		return entity instanceof EntityPlayer ? PLAYER : EntityList.getKey(entity);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		int prevCount = count;
		super.readFromNBT(tag);
		count = tag.getInteger("count");
		min = tag.getShort("min");
		max = tag.getShort("max");
		NBTTagList list = tag.getTagList("enabled", 8);
		enabled.clear();
		for (int i = 0; i < list.tagCount(); i++) enabled.add(new ResourceLocation(list.getStringTagAt(i)));
		int count = this.getSuccessCount();
		if (count < min || count > max) this.count = 0;
		else this.count = count + 1 - min;
		if (this.count != prevCount) world.updateComparatorOutputLevel(getPos(), world.getBlockState(pos).getBlock());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag = super.writeToNBT(tag);
		tag.setInteger("count", count);
		tag.setShort("min", min);
		tag.setShort("max", max);
		NBTTagList list = new NBTTagList();
		enabled.stream().map(name -> new NBTTagString(name.toString())).forEach(list::appendTag);
		tag.setTag("enabled", list);
		return tag;
	}

	@Override
	public void read(ByteBuf buf)
	{
		int prevCount = count;
		super.read(buf);
		min = (short) ByteBufUtils.readVarShort(buf);
		max = (short) ByteBufUtils.readVarShort(buf);
		enabled.clear();
		int numEntries = ByteBufUtils.readVarInt(buf, 5);
		for (int i = 0; i < numEntries; i++) enabled.add(new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
		int count = this.getSuccessCount();
		if (count < min || count > max) this.count = 0;
		else this.count = count + 1 - min;
		if (this.count != prevCount) world.updateComparatorOutputLevel(getPos(), world.getBlockState(pos).getBlock());
	}

	@Override
	public void write(ByteBuf buf)
	{
		super.write(buf);
		ByteBufUtils.writeVarShort(buf, min);
		ByteBufUtils.writeVarShort(buf, max);
		ByteBufUtils.writeVarInt(buf, enabled.size(), 5);
		enabled.forEach(name -> ByteBufUtils.writeUTF8String(buf, name.toString()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiTileEntityGui getScreen()
	{
		return new GuiEntityTester(this.pos);
	}
}