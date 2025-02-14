package com.firemerald.custombgm.blockentity;

import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.operators.BGMOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityBGM<O extends BGMOperator<O, S>, S extends BlockEntityBGM<O, S>> extends BlockEntityEntityOperator<O, S>
{
	@SuppressWarnings("unchecked")
	public BlockEntityBGM(BlockPos pos, BlockState state)
    {
    	this((BlockEntityType<? extends S>) CustomBGMObjects.BGM.getBlockEntityType(), pos, state);
    }

	public BlockEntityBGM(BlockEntityType<? extends S> type, BlockPos pos, BlockState state)
    {
    	super(type, pos, state);
    }

	@SuppressWarnings("unchecked")
	@Override
	protected O makeOperator()
	{
		return (O) new BGMOperator<>((S) this);
	}
}