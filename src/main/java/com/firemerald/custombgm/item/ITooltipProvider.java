package com.firemerald.custombgm.item;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;

@FunctionalInterface
public interface ITooltipProvider
{
	public abstract void addTooltip(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipComponents, TooltipFlag tooltipFlag, Function<ItemStack, CompoundTag> getData);
}