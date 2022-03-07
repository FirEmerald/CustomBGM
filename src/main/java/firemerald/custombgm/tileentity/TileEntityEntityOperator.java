package firemerald.custombgm.tileentity;

import java.util.List;
import java.util.function.Predicate;

import firemerald.api.betterscreens.TileEntityGUI;
import firemerald.api.selectionshapes.BoundingShape;
import firemerald.api.selectionshapes.BoundingShapeSphere;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public abstract class TileEntityEntityOperator<T extends Entity> extends TileEntityGUI implements ITickable, ICommandSender
{
	public BoundingShape shape = new BoundingShapeSphere();
	public String selector = null;
	public NBTTagCompound selectorNBT = new NBTTagCompound();
    /** The number of successful commands run. (used for redstone output) */
    private int successCount;
    /** The custom name of the command block. (defaults to "@") */
    private String customName = "@";
    private final CommandResultStats resultStats = new CommandResultStats();
    public final Class<T> clazz;

    public TileEntityEntityOperator(Class<T> clazz)
    {
    	this.clazz = clazz;
    }

    public abstract boolean isActive();

    public abstract boolean operate(T entity);

    public abstract List<? extends T> allEntities();

	@Override
	public void update()
	{
		if (isActive())
		{
			List<? extends T> matchingEntities;
			if (selector == null) matchingEntities = allEntities();
			else try
			{
				matchingEntities = EntitySelector.matchEntities(this, selector, clazz);
			}
			catch (CommandException e)
			{
				matchingEntities = allEntities();
			}
			Predicate<T> tester = entity -> shape.isWithin(entity, entity.posX, entity.posY, entity.posZ, pos.getX(), pos.getY(), pos.getZ());
			if (!selectorNBT.hasNoTags()) tester = tester.and(entity -> NBTUtil.areNBTEquals(selectorNBT, CommandBase.entityToNBT(entity), true));
			successCount = (int) matchingEntities.stream().filter(tester.and(this::operate)).count();
			this.setCommandStat(Type.AFFECTED_ENTITIES, successCount);
			this.setCommandStat(Type.SUCCESS_COUNT, successCount);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		if (tag.hasKey("shape", 10)) shape = BoundingShape.constructFromNBT(tag.getCompoundTag("shape"));
		else shape = new BoundingShapeSphere();
		String selector = tag.getString("selector");
		this.selector = selector.isEmpty() ? null : selector;
		this.selectorNBT = tag.getCompoundTag("selectorNBT");
        this.successCount = tag.getInteger("SuccessCount");
        if (tag.hasKey("CustomName", 8)) this.customName = tag.getString("CustomName");
        this.resultStats.readStatsFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagCompound shapeParams = new NBTTagCompound();
		shape.saveToNBT(shapeParams);
		tag.setTag("shape", shapeParams);
		tag.setString("selector", selector == null ? "" : selector);
		tag.setTag("selectorNBT", selectorNBT);
		tag.setInteger("SuccessCount", this.successCount);
		tag.setString("CustomName", this.customName);
        this.resultStats.writeStatsToNBT(tag);
		return tag;
	}

	@Override
	public void read(ByteBuf buf)
	{
		shape = BoundingShape.constructFromBuffer(buf);
		String selector = ByteBufUtils.readUTF8String(buf);
		this.selector = selector.isEmpty() ? null : selector;
		selectorNBT = ByteBufUtils.readTag(buf);
		customName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void write(ByteBuf buf)
	{
		shape.saveToBuffer(buf);
		ByteBufUtils.writeUTF8String(buf, selector == null ? "" : selector);
		ByteBufUtils.writeTag(buf, selectorNBT);
		ByteBufUtils.writeUTF8String(buf, customName);
	}

	@Override
	public String getName()
	{
		return customName;
	}

	@Override
	public boolean canUseCommand(int permLevel, String commandName)
	{
        return permLevel <= 2;
	}

	@Override
	public World getEntityWorld()
	{
		return world;
	}

	@Override
	public MinecraftServer getServer()
	{
		return world.getMinecraftServer();
	}

	@Override
	public BlockPos getPosition()
    {
        return pos;
    }

	@Override
    public Vec3d getPositionVector()
    {
        return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

	@Override
    public void setCommandStat(CommandResultStats.Type type, int amount)
    {
        this.resultStats.setCommandStatForSender(this.getServer(), this, type, amount);
    }

	public int getSuccessCount()
	{
		return this.successCount;
	}

	public void setName(String displayName)
	{
		this.customName = displayName;
	}

	public CommandResultStats getStats()
	{
		return this.resultStats;
	}
}