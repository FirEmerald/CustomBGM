package com.firemerald.custombgm.blocks;

import com.firemerald.custombgm.blockentity.BlockEntityBossSpawner;
import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.item.ITooltipProvider;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
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

public class BlockBossSpawner<O extends BossSpawnerOperator<O, S>, S extends BlockEntityBossSpawner<O, S>> extends BlockOperator<O, S>
{
	public static final MapCodec<BlockBossSpawner<?, ?>> CODEC = simpleCodec(BlockBossSpawner::new);

    public BlockBossSpawner(ResourceKey<Block> id)
    {
    	this(BossSpawnerOperator::addTooltip, id);
    }

    public BlockBossSpawner(BlockBehaviour.Properties properties)
    {
    	this(BossSpawnerOperator::addTooltip, properties);
    }

    public BlockBossSpawner(ITooltipProvider tooltip, ResourceKey<Block> id)
    {
    	super(tooltip, id);
    }

    public BlockBossSpawner(ITooltipProvider tooltip, BlockBehaviour.Properties properties)
    {
    	super(tooltip, properties);
    }

	@SuppressWarnings("unchecked")
	@Override
	public S newBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		return (S) new BlockEntityBossSpawner<>(blockPos, blockState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return (CustomBGMObjects.BOSS_SPAWNER.isThisBlockEntity(type) && !level.isClientSide) ? (level2, blockPos, blockState, blockEntity) -> ((S) blockEntity).serverTick(level2, blockPos, blockState) : null;
	}

	@Override
	protected MapCodec<BlockBossSpawner<?, ?>> codec() {
		return CODEC;
	}
}