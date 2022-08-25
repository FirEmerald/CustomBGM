package com.firemerald.custombgm.api.capabilities;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface IBossTracker extends ICapabilityProvider
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
	
	public void setBossBlock(Level level, BlockPos pos);
	
	public BlockEntity getBossBlock();
	
	public static class Impl implements IBossTracker
	{
	    private final LazyOptional<IBossTracker> holder = LazyOptional.of(() -> this);
	    private Level level = null;
	    private BlockPos pos = null;

		@Override
		public void setBossBlock(Level level, BlockPos pos)
		{
			this.level = level;
			this.pos = pos;
		}

		@Override
		public BlockEntity getBossBlock()
		{
			return (level == null || pos == null) ? null : level.getBlockEntity(pos);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
		{
	        return CAP.orEmpty(cap, holder);
		}
	}
}