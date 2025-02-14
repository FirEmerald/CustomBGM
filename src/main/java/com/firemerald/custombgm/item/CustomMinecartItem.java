package com.firemerald.custombgm.item;

import java.util.List;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.TooltipFlag;

public class CustomMinecartItem extends MinecartItem
{
	protected final ITooltipProvider tooltip;

	public CustomMinecartItem(EntityType<? extends AbstractMinecart> entityType, ITooltipProvider tooltip, Item.Properties properties) {
		super(entityType, properties);
		this.tooltip = tooltip;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		this.tooltip.addTooltip(stack, context, tooltipComponents, tooltipFlag, DataComponents.ENTITY_DATA);
		tooltipComponents.add(Component.translatable("custombgm.tooltip.activator_rail_activated"));
	}
}