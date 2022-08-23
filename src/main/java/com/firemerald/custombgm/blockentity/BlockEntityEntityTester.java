package com.firemerald.custombgm.blockentity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.firemerald.custombgm.client.gui.GuiEntityTester;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.fecore.betterscreens.BlockEntityGUIScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEntityEntityTester extends BlockEntityEntityOperator<Entity>
{
	public List<ResourceLocation> enabled = new ArrayList<>();
	public short min = 1, max = Short.MAX_VALUE;
	public int count = 0;

    public BlockEntityEntityTester(BlockPos pos, BlockState state)
    {
    	this(CustomBGMBlockEntities.ENTITY_TESTER, pos, state);
    }

    public BlockEntityEntityTester(BlockEntityType<? extends BlockEntityEntityTester> type, BlockPos pos, BlockState state)
    {
    	super(type, pos, state, Entity.class);
    }

	@Override
	public void serverTick(Level level, BlockPos blockPos, BlockState blockState)
	{
		int prevCount = count;
		super.serverTick(level, blockPos, blockState);
		int count = this.getSuccessCount();
		if (count < min || count > max) this.count = 0;
		else this.count = count + 1 - min;
		if (count != prevCount) level.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
	}

	@Override
	public boolean isActive()
	{
		return !level.isClientSide && level.hasNeighborSignal(worldPosition);
	}

	@Override
	public boolean operate(Entity entity)
	{
		return enabled.contains(getId(entity));
	}

	@Override
	public Stream<? extends Entity> allEntities()
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
		if (this.count != prevCount) level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
	}

	@Override
	public void saveAdditional(CompoundTag tag)
	{
		super.saveAdditional(tag);
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
		if (this.count != prevCount) level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
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
	@OnlyIn(Dist.CLIENT)
	public BlockEntityGUIScreen getScreen()
	{
		return new GuiEntityTester(this.worldPosition);
	}
}