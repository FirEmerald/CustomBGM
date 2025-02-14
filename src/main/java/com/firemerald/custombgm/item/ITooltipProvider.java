package com.firemerald.custombgm.item;

import java.util.List;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

@FunctionalInterface
public interface ITooltipProvider
{
	public abstract void addTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag, DataComponentType<CustomData> componentType);
}