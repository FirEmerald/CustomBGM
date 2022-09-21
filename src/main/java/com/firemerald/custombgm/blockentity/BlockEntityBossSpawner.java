package com.firemerald.custombgm.blockentity;

import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.operators.BossSpawnerOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityBossSpawner<O extends BossSpawnerOperator<O, S>, S extends BlockEntityBossSpawner<O, S>> extends BlockEntityEntityOperator<O, S>
{
	@SuppressWarnings("unchecked")
	public BlockEntityBossSpawner(BlockPos pos, BlockState state)
    {
    	this((BlockEntityType<S>) CustomBGMBlockEntities.BOSS_SPAWNER.getBlockEntityType(), pos, state);
    }

	public BlockEntityBossSpawner(BlockEntityType<? extends S> type, BlockPos pos, BlockState state)
    {
    	super(type, pos, state);
    }

	@SuppressWarnings("unchecked")
	@Override
	protected O makeOperator()
	{
		return (O) new BossSpawnerOperator<>((S) this);
	}
}