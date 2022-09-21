package com.firemerald.custombgm.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.firemerald.custombgm.client.gui.screen.EntityTesterScreen;
import com.firemerald.custombgm.client.gui.screen.OperatorScreen;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTesterOperator<O extends EntityTesterOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorBase<Entity, O, S>
{
	public List<ResourceLocation> enabled = new ArrayList<>();
	public short min = 1, max = Short.MAX_VALUE;
	public int count = 0;

	public EntityTesterOperator(S source)
	{
		super(Entity.class, source);
	}

	@Override
	public void serverTick(Level level, double x, double y, double z)
	{
		int prevCount = count;
		super.serverTick(level, x, y, z);
		int count = this.getSuccessCount();
		if (count < min || count > max) this.count = 0;
		else this.count = count + 1 - min;
		if (count != prevCount) source.updateOutputValue();
	}

	@Override
	public boolean operate(Entity entity)
	{
		return enabled.contains(getId(entity));
	}

	@Override
	public Stream<? extends Entity> allEntities(Level level)
	{
		return StreamSupport.stream(((ServerLevel) level).getAllEntities().spliterator(), false);
	}

	@SuppressWarnings("deprecation")
	public static ResourceLocation getId(Entity entity)
	{
		return Registry.ENTITY_TYPE.getKey(entity.getType());
	}

	@Override
	public void load(CompoundTag tag)
	{
		int prevCount = count;
		super.load(tag);
		count = tag.getInt("count");
		min = tag.getShort("min");
		max = tag.getShort("max");
		ListTag list = tag.getList("enabled", 8);
		enabled.clear();
		for (int i = 0; i < list.size(); i++) enabled.add(new ResourceLocation(list.getString(i)));
		int count = this.getSuccessCount();
		if (count < min || count > max) this.count = 0;
		else this.count = count + 1 - min;
		if (this.count != prevCount) source.updateOutputValue();
	}

	@Override
	public void save(CompoundTag tag)
	{
		super.save(tag);
		tag.putInt("count", count);
		tag.putShort("min", min);
		tag.putShort("max", max);
		ListTag list = new ListTag();
		enabled.stream().map(name -> StringTag.valueOf(name.toString())).forEach(list::add);
		tag.put("enabled", list);
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		int prevCount = count;
		super.read(buf);
		min = (short) buf.readVarInt();
		max = (short) buf.readVarInt();
		enabled.clear();
		int numEntries = buf.readVarInt();
		for (int i = 0; i < numEntries; i++) enabled.add(new ResourceLocation(buf.readUtf()));
		int count = this.getSuccessCount();
		if (count < min || count > max) this.count = 0;
		else this.count = count + 1 - min;
		if (this.count != prevCount) source.updateOutputValue();
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		buf.writeVarInt(min);
		buf.writeVarInt(max);
		buf.writeVarInt(enabled.size());
		enabled.forEach(name -> buf.writeUtf(name.toString()));
	}

	@Override
	public int getOutputLevel()
	{
		return count;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OperatorScreen<O, S> getScreen()
	{
		return new EntityTesterScreen<>(source);
	}

	public static void addTooltip(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag, Supplier<CompoundTag> operatorTag)
	{
		tooltip.add(new TranslatableComponent("custombgm.tooltip.entity_tester"));
	}
}