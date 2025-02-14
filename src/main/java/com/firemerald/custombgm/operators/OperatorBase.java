package com.firemerald.custombgm.operators;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.firemerald.custombgm.client.gui.screen.OperatorScreen;
import com.firemerald.fecore.boundingshapes.BoundingShape;
import com.firemerald.fecore.boundingshapes.BoundingShapeSphere;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;

import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class OperatorBase<E extends Entity, O extends OperatorBase<E, O, S>, S extends IOperatorSource<O, S>> {
	public BoundingShape shape = new BoundingShapeSphere();
	//public String selector = null;
	private EntitySelector selector = null;
	private String selectorString = null;
	//public CompoundTag selectorNBT = new CompoundTag();
    private int found;
    public final S source;
    public final Class<E> clazz;

    public OperatorBase(Class<E> clazz, S source) {
    	this.clazz = clazz;
    	this.source = source;
    }

	public void readInternal(RegistryFriendlyByteBuf buf) {
		this.read(buf);
		source.setIsChanged();
	}

    public String getSelectorString() {
    	return selectorString;
    }

    public EntitySelector getSelector() {
    	return selector;
    }

    public void setSelectorString(String selectorString) {
    	this.selectorString = selectorString;
    	if (selectorString != null && !selectorString.isEmpty()) {
        	EntitySelectorParser parser = new EntitySelectorParser(new StringReader(selectorString), true);
        	try {
    			this.selector = parser.parse();
    		} catch (CommandSyntaxException e) {
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
        		this.selector = null;
    		}
    	}
    	else this.selector = null;
    }

    public boolean isValid(E entity) {
    	return true;
    }

    public abstract boolean operate(E entity);

    public abstract Stream<? extends E> allEntities(Level level);

	@SuppressWarnings("unchecked")
	public void serverTick(Level level, double x, double y, double z) {
		if (isActive()) {
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
			Predicate<E> tester = entity -> {
				if (entity instanceof Player player && player.isSpectator()) return false;
				else return shape.isWithin(entity, entity.position().x, entity.position().y, entity.position().z, x + 0.5, y + 0.5, z + 0.5);
			};
			found = (int) matchingEntities.filter(((Predicate<E>) this::isValid).and(tester).and(this::operate)).count();
			if (prevFound != found) source.setIsChanged();
		}
	}

	public void load(CompoundTag tag) {
		if (tag.contains("shape", 10)) {
			DataResult<Pair<BoundingShape, Tag>> decoded = BoundingShape.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("shape"));
			if (decoded.isSuccess()) shape = decoded.result().get().getFirst();
			else shape = new BoundingShapeSphere();
		}
		else shape = new BoundingShapeSphere();
		this.setSelectorString(tag.getString("selector"));
        this.found = tag.getInt("SuccessCount");
	}

	public void save(CompoundTag tag) {
		BoundingShape.CODEC.encode(shape, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(shapeParams -> tag.put("shape", shapeParams));
		tag.putString("selector", selectorString == null ? "" : selectorString);
		tag.putInt("SuccessCount", this.found);
	}

	public void read(RegistryFriendlyByteBuf buf) {
		shape = BoundingShape.STREAM_CODEC.decode(buf);
		this.setSelectorString(buf.readUtf());
		this.source.setTheName(ComponentSerialization.STREAM_CODEC.decode(buf));
	}

	public void write(RegistryFriendlyByteBuf buf) {
		BoundingShape.STREAM_CODEC.encode(buf, shape);
		buf.writeUtf(selectorString == null ? "" : selectorString);
		ComponentSerialization.STREAM_CODEC.encode(buf, this.source.getTheName());
	}

	public int getSuccessCount() {
		return this.found;
	}

    public boolean isActive() {
    	return source.isActive();
    }

	public abstract int getOutputLevel();

	public void onRemoved() {}

	@OnlyIn(Dist.CLIENT)
	public abstract OperatorScreen<O, S> getScreen();
}