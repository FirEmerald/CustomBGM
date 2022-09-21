package com.firemerald.custombgm.blocks;

import com.firemerald.custombgm.blockentity.BlockEntityBGM;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.item.ITooltipProvider;
import com.firemerald.custombgm.operators.BGMOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBGM<O extends BGMOperator<O, S>, S extends BlockEntityBGM<O, S>> extends BlockOperator<O, S>
{
    public BlockBGM()
    {
    	this(BGMOperator::addTooltip);
    }

    public BlockBGM(BlockBehaviour.Properties properties)
    {
    	this(BGMOperator::addTooltip, properties);
    }

    public BlockBGM(ITooltipProvider tooltip)
    {
    	super(tooltip);
    }

    public BlockBGM(ITooltipProvider tooltip, BlockBehaviour.Properties properties)
    {
    	super(tooltip, properties);
    }

	@SuppressWarnings("unchecked")
	@Override
	public S newBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		return (S) new BlockEntityBGM<>(blockPos, blockState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return (CustomBGMBlockEntities.BGM.isThisBlockEntity(type) && !level.isClientSide) ? (level2, blockPos, blockState, blockEntity) -> ((S) blockEntity).serverTick(level2, blockPos, blockState) : null;
	}
}