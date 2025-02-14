package com.firemerald.custombgm.blocks;

import com.firemerald.custombgm.blockentity.BlockEntityBGM;
import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.item.ITooltipProvider;
import com.firemerald.custombgm.operators.BGMOperator;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBGM<O extends BGMOperator<O, S>, S extends BlockEntityBGM<O, S>> extends BlockOperator<O, S>
{
	public static final MapCodec<BlockBGM<?, ?>> CODEC = simpleCodec(BlockBGM::new);

    public BlockBGM(ResourceKey<Block> id)
    {
    	this(BGMOperator::addTooltip, id);
    }

    public BlockBGM(BlockBehaviour.Properties properties)
    {
    	this(BGMOperator::addTooltip, properties);
    }

    public BlockBGM(ITooltipProvider tooltip, ResourceKey<Block> id)
    {
    	super(tooltip, id);
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
		return (CustomBGMObjects.BGM.isThisBlockEntity(type) && !level.isClientSide) ? (level2, blockPos, blockState, blockEntity) -> ((S) blockEntity).serverTick(level2, blockPos, blockState) : null;
	}

	@Override
	protected MapCodec<BlockBGM<?, ?>> codec() {
		return CODEC;
	}
}