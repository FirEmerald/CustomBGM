package com.firemerald.custombgm.blockentity;

import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.operators.EntityTesterOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityEntityTester<O extends EntityTesterOperator<O, S>, S extends BlockEntityEntityTester<O, S>> extends BlockEntityEntityOperator<O, S>
{
	@SuppressWarnings("unchecked")
	public BlockEntityEntityTester(BlockPos pos, BlockState state)
    {
    	this((BlockEntityType<S>) CustomBGMObjects.ENTITY_TESTER.getBlockEntityType(), pos, state);
    }

	public BlockEntityEntityTester(BlockEntityType<? extends S> type, BlockPos pos, BlockState state)
    {
    	super(type, pos, state);
    }

	@SuppressWarnings("unchecked")
	@Override
	protected O makeOperator()
	{
		return (O) new EntityTesterOperator<>((S) this);
	}
}