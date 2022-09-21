package com.firemerald.custombgm.operators;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.firemerald.custombgm.client.gui.screen.OperatorScreen;
import com.firemerald.fecore.boundingshapes.BoundingShape;
import com.firemerald.fecore.boundingshapes.BoundingShapeSphere;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class OperatorBase<E extends Entity, O extends OperatorBase<E, O, S>, S extends IOperatorSource<O, S>>
{
	public BoundingShape shape = new BoundingShapeSphere();
	//public String selector = null;
	private EntitySelector selector = null;
	private String selectorString = null;
	//public CompoundTag selectorNBT = new CompoundTag();
    private int found;
    public final S source;
    public final Class<E> clazz;

    public OperatorBase(Class<E> clazz, S source)
    {
    	this.clazz = clazz;
    	this.source = source;
    }

	public void readInternal(FriendlyByteBuf buf)
	{
		this.read(buf);
		source.setIsChanged();
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

    public abstract boolean operate(E entity);

    public abstract Stream<? extends E> allEntities(Level level);

	@SuppressWarnings("unchecked")
	public void serverTick(Level level, double x, double y, double z)
	{
		if (isActive())
		{
			int prevFound = found;
			Stream<? extends E> matchingEntities;
			if (selector == null) matchingEntities = allEntities(level);
			else try
			{
				matchingEntities = selector.findEntities(source.createACommandSourceStack()).stream().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (E) e);
			}
			catch (CommandSyntaxException e)
			{
				// TODO Auto-generated catch block
				matchingEntities = allEntities(level);
			}
			Predicate<E> tester = entity -> shape.isWithin(entity, entity.position().x, entity.position().y, entity.position().z, x + 0.5, y + 0.5, z + 0.5);
			found = (int) matchingEntities.filter(tester.and(this::operate)).count();
			if (prevFound != found) source.setIsChanged();
		}
	}

	public void load(CompoundTag tag)
	{
		if (tag.contains("shape", 10)) shape = BoundingShape.constructFromNBT(tag.getCompound("shape"));
		else shape = new BoundingShapeSphere();
		this.setSelectorString(tag.getString("selector"));
        this.found = tag.getInt("SuccessCount");
	}

	public void save(CompoundTag tag)
	{
		CompoundTag shapeParams = new CompoundTag();
		shape.saveToNBT(shapeParams);
		tag.put("shape", shapeParams);
		tag.putString("selector", selectorString == null ? "" : selectorString);
		tag.putInt("SuccessCount", this.found);
	}

	public void read(FriendlyByteBuf buf)
	{
		shape = BoundingShape.constructFromBuffer(buf);
		this.setSelectorString(buf.readUtf());
		this.source.setTheName(Component.Serializer.fromJson(buf.readUtf()));
	}

	public void write(FriendlyByteBuf buf)
	{
		shape.saveToBuffer(buf);
		buf.writeUtf(selectorString == null ? "" : selectorString);
		buf.writeUtf(Component.Serializer.toJson(this.source.getTheName()));
	}

	public int getSuccessCount()
	{
		return this.found;
	}

    public boolean isActive()
    {
    	return source.isActive();
    }

	public abstract int getOutputLevel();

	public void onRemoved() {}

	@OnlyIn(Dist.CLIENT)
	public abstract OperatorScreen<O, S> getScreen();
}