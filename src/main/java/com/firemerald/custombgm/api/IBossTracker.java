package com.firemerald.custombgm.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface IBossTracker extends ICapabilityProvider
{
	public static final ResourceLocation CAPABILITY_NAME = new ResourceLocation(CustomBGMAPI.MOD_ID, "boss_tracker");
	
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
			this.pos = pos;
		}

		@Override
		public BlockEntity getBossBlock()
		{
			return level == null || pos == null ? null : level.getBlockEntity(pos);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
		{
	        return CustomBGMCapabilities.BOSS_TRACKER.orEmpty(cap, holder);
		}
	}
}