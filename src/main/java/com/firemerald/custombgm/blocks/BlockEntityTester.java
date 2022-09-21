package com.firemerald.custombgm.blocks;

import com.firemerald.custombgm.blockentity.BlockEntityEntityTester;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.item.ITooltipProvider;
import com.firemerald.custombgm.operators.EntityTesterOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityTester<O extends EntityTesterOperator<O, S>, S extends BlockEntityEntityTester<O, S>> extends BlockOperator<O, S>
{
    public BlockEntityTester()
    {
    	this(EntityTesterOperator::addTooltip);
    }

    public BlockEntityTester(BlockBehaviour.Properties properties)
    {
    	this(EntityTesterOperator::addTooltip, properties);
    }

    public BlockEntityTester(ITooltipProvider tooltip)
    {
    	super(tooltip);
    }

    public BlockEntityTester(ITooltipProvider tooltip, BlockBehaviour.Properties properties)
    {
    	super(tooltip, properties);
    }

	@SuppressWarnings("unchecked")
	@Override
	public S newBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		return (S) new BlockEntityEntityTester<>(blockPos, blockState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return (CustomBGMBlockEntities.ENTITY_TESTER.isThisBlockEntity(type) && !level.isClientSide) ? (level2, blockPos, blockState, blockEntity) -> ((S) blockEntity).serverTick(level2, blockPos, blockState) : null;
	}
}