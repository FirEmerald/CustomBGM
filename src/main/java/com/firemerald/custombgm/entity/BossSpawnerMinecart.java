package com.firemerald.custombgm.entity;

import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.operators.BossSpawnerOperator;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BossSpawnerMinecart<O extends BossSpawnerOperator<O, S>, S extends BossSpawnerMinecart<O, S>> extends OperatorMinecart<O, S>
{
	public BossSpawnerMinecart(EntityType<?> type, Level level)
	{
		super(type, level);
	}

	public BossSpawnerMinecart(EntityType<?> type, Level level, double x, double y, double z)
	{
		super(type, level, x, y, z);
	}

	public BossSpawnerMinecart(Level level)
	{
		this(CustomBGMEntities.BOSS_SPAWNER_MINECART.get(), level);
	}

	public BossSpawnerMinecart(Level level, double x, double y, double z)
	{
		this(CustomBGMEntities.BOSS_SPAWNER_MINECART.get(), level, x, y, z);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected O makeOperator()
	{
		return (O) new BossSpawnerOperator<>((S) this);
	}

	@Override
	public BlockState getDefaultDisplayBlockState()
	{
		return CustomBGMObjects.BOSS_SPAWNER.getBlock().defaultBlockState();
	}

	@Override
	protected Item getDropItem() {
		return CustomBGMObjects.BOSS_SPAWNER_MINECART.asItem();
	}
}
