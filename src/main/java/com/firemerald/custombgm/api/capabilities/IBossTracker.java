package com.firemerald.custombgm.api.capabilities;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

public interface IBossTracker extends ICapabilitySerializable<ByteTag>
{
	public static final ResourceLocation NAME = new ResourceLocation(CustomBGMAPI.MOD_ID, "boss_tracker");
	public static final Capability<IBossTracker> CAP = CapabilityManager.get(new CapabilityToken<>(){});

	public static LazyOptional<IBossTracker> get(ICapabilityProvider obj)
	{
		return obj.getCapability(CAP);
	}

	public static LazyOptional<IBossTracker> get(ICapabilityProvider obj, @Nullable Direction side)
	{
		return obj.getCapability(CAP, side);
	}

	public static IBossTracker getOrNull(ICapabilityProvider obj)
	{
		return get(obj).resolve().orElse(null);
	}

	public static IBossTracker getOrNull(ICapabilityProvider obj, @Nullable Direction side)
	{
		return get(obj, side).resolve().orElse(null);
	}

	public void setBossSpawnerBlock(BlockEntity blockEntity);

	public void setBossSpawnerEntity(Entity entity);

	public void setNoBossSpawner();

	public Object getBossSpawnerObject();

	public boolean isBoss();

	public static class Impl implements IBossTracker
	{
	    private final LazyOptional<IBossTracker> holder = LazyOptional.of(() -> this);
	    private Level level = null;
	    private BlockPos pos = null;
	    private int entityId = -1;
	    private boolean isBoss = false;

	    @Override
		public void setBossSpawnerBlock(BlockEntity blockEntity)
		{
			this.level = blockEntity.getLevel();
			this.pos = blockEntity.getBlockPos();
			this.entityId = -1;
			this.isBoss = true;
		}

	    @Override
		public void setBossSpawnerEntity(Entity entity)
		{
			this.level = entity.getLevel();
			this.pos = null;
			this.entityId = entity.getId();
			this.isBoss = true;
		}

	    @Override
		public void setNoBossSpawner()
		{
			this.level = null;
			this.pos = null;
			this.entityId = -1;
			this.isBoss = false;
		}

	    @Override
	    public Object getBossSpawnerObject()
	    {
	    	if (!isBoss || level == null) return null;
	    	else if (entityId >= 0) return level.getEntity(entityId);
	    	else if (pos != null) return level.getBlockEntity(pos);
	    	else return null;
	    }

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
		{
	        return CAP.orEmpty(cap, holder);
		}

		@Override
		public boolean isBoss()
		{
			return isBoss;
		}

		@Override
		public ByteTag serializeNBT()
		{
			return isBoss ? ByteTag.ONE : ByteTag.ZERO;
		}

		@Override
		public void deserializeNBT(ByteTag nbt)
		{
			isBoss = nbt.getAsByte() != 0;
		}
	}
}