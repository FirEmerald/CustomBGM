package com.firemerald.custombgm.entity;

import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.operators.EntityTesterOperator;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityTesterMinecart<O extends EntityTesterOperator<O, S>, S extends EntityTesterMinecart<O, S>> extends OperatorMinecart<O, S>
{
	public EntityTesterMinecart(EntityType<?> type, Level level)
	{
		super(type, level);
	}

	public EntityTesterMinecart(EntityType<?> type, Level level, double x, double y, double z)
	{
		super(type, level, x, y, z);
	}

	public EntityTesterMinecart(Level level)
	{
		this(CustomBGMEntities.ENTITY_TESTER_MINECART.get(), level);
	}

	public EntityTesterMinecart(Level level, double x, double y, double z)
	{
		this(CustomBGMEntities.ENTITY_TESTER_MINECART.get(), level, x, y, z);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected O makeOperator()
	{
		return (O) new EntityTesterOperator<>((S) this);
	}

	@Override
	public BlockState getDefaultDisplayBlockState()
	{
		return CustomBGMObjects.ENTITY_TESTER.getBlock().defaultBlockState();
	}

	@Override
	protected Item getDropItem() {
		return CustomBGMObjects.ENTITY_TESTER_MINECART.asItem();
	}
}
