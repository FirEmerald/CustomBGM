package com.firemerald.custombgm.entity;

import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMItems;
import com.firemerald.custombgm.operators.BGMOperator;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BGMMinecart<O extends BGMOperator<O, S>, S extends BGMMinecart<O, S>> extends OperatorMinecart<O, S>
{
	public BGMMinecart(EntityType<?> type, Level level)
	{
		super(type, level);
	}

	public BGMMinecart(EntityType<?> type, Level level, double x, double y, double z)
	{
		super(type, level, x, y, z);
	}

	public BGMMinecart(Level level)
	{
		this(CustomBGMEntities.BGM_MINECART.get(), level);
	}

	public BGMMinecart(Level level, double x, double y, double z)
	{
		this(CustomBGMEntities.BGM_MINECART.get(), level, x, y, z);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected O makeOperator()
	{
		return (O) new BGMOperator<>((S) this);
	}

	@Override
	public BlockState getDefaultDisplayBlockState()
	{
		return CustomBGMBlockEntities.BGM.getBlock().defaultBlockState();
	}

	@Override
	public ItemStack getPickResult()
	{
		return new ItemStack(CustomBGMItems.BGM_MINECART_ITEM);
	}
}
