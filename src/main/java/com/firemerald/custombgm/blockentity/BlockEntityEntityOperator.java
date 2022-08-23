package com.firemerald.custombgm.blockentity;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.firemerald.fecore.betterscreens.BlockEntityGUI;
import com.firemerald.fecore.selectionshapes.BoundingShape;
import com.firemerald.fecore.selectionshapes.BoundingShapeSphere;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class BlockEntityEntityOperator<T extends Entity> extends BlockEntityGUI implements CommandSource
{
	public static final Component DEFAULT_NAME = new TextComponent("@");
	public BoundingShape shape = new BoundingShapeSphere();
	//public String selector = null;
	private EntitySelector selector = null;
	private String selectorString = null;
	//public CompoundTag selectorNBT = new CompoundTag();
    private int found;
    private Component customName = DEFAULT_NAME;
    public final Class<T> clazz;

    public BlockEntityEntityOperator(BlockEntityType<? extends BlockEntityEntityOperator<? extends T>> type, BlockPos pos, BlockState state, Class<T> clazz)
    {
    	super(type, pos, state);
    	this.clazz = clazz;
    }
    
    public String getSelectorString()
    {
    	return selectorString;
    }
    
    public EntitySelector getSelector()
    {
    	return selector;
    }
    
    public void setSelectorString(String selectorString)
    {
    	this.selectorString = selectorString;
    	if (selectorString != null && !selectorString.isEmpty())
    	{
        	EntitySelectorParser parser = new EntitySelectorParser(new StringReader(selectorString));
        	try
        	{
    			this.selector = parser.parse();
    		}
        	catch (CommandSyntaxException e)
        	{
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
        		this.selector = null;
    		}
    	}
    	else this.selector = null;
    }

    public abstract boolean isActive();

    public abstract boolean operate(T entity);

    public abstract Stream<? extends T> allEntities();

    public CommandSourceStack createCommandSourceStack()
    {
       return new CommandSourceStack(this, Vec3.atCenterOf(worldPosition), Vec2.ZERO, (ServerLevel) getLevel(), 2, getName().getString(), getName(), getLevel().getServer(), null);
    }

	@SuppressWarnings("unchecked")
	public void serverTick(Level level, BlockPos blockPos, BlockState blockState)
	{
		if (isActive())
		{
			Stream<? extends T> matchingEntities;
			if (selector == null) matchingEntities = allEntities();
			else try
			{
				matchingEntities = selector.findEntities(createCommandSourceStack()).stream().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
			}
			catch (CommandSyntaxException e)
			{
				// TODO Auto-generated catch block
				matchingEntities = allEntities();
			}
			Predicate<T> tester = entity -> shape.isWithin(entity, entity.position().x, entity.position().y, entity.position().z, blockPos.getX(), blockPos.getY(), blockPos.getZ());
			//if (!selectorNBT.isEmpty()) tester = tester.and(entity -> NbtUtils.areNBTEquals(selectorNBT, CommandBase.entityToNBT(entity), true));
			found = (int) matchingEntities.filter(tester.and(this::operate)).count();
			this.setChanged();
		}
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		if (tag.contains("shape", 10)) shape = BoundingShape.constructFromNBT(tag.getCompound("shape"));
		else shape = new BoundingShapeSphere();
		this.setSelectorString(tag.getString("selector"));
		//this.selectorNBT = tag.getCompound("selectorNBT");
        this.found = tag.getInt("SuccessCount");
        if (tag.contains("CustomName", 8)) this.setName(Component.Serializer.fromJson(tag.getString("CustomName")));
	}

	@Override
	public void saveAdditional(CompoundTag tag)
	{
		super.saveAdditional(tag);
		CompoundTag shapeParams = new CompoundTag();
		shape.saveToNBT(shapeParams);
		tag.put("shape", shapeParams);
		tag.putString("selector", selectorString == null ? "" : selectorString);
		//tag.put("selectorNBT", selectorNBT);
		tag.putInt("SuccessCount", this.found);
		tag.putString("CustomName", Component.Serializer.toJson(this.customName));
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		shape = BoundingShape.constructFromBuffer(buf);
		this.setSelectorString(buf.readUtf());
		//selectorNBT = buf.readNbt();
		this.setName(Component.Serializer.fromJson(buf.readUtf()));
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		shape.saveToBuffer(buf);
		buf.writeUtf(selectorString == null ? "" : selectorString);
		//buf.writeNbt(selectorNBT);
		buf.writeUtf(Component.Serializer.toJson(this.customName));
	}

	public int getSuccessCount()
	{
		return this.found;
	}

	public Component getName() {
		return this.customName;
	}
	
	public void setName(@Nullable Component customName)
	{
		if (customName != null) this.customName = customName;
		else this.customName = DEFAULT_NAME;
	}

	@Override
	public boolean acceptsSuccess()
	{
		return false;
	}

	@Override
	public boolean acceptsFailure()
	{
		return false;
	}

	@Override
	public boolean shouldInformAdmins()
	{
		return false;
	}
	
	@Override
	public void sendMessage(Component p_45426_, UUID p_45427_) {}
}