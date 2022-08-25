package com.firemerald.custombgm.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CustomBGMTabs
{
	public static final CreativeModeTab TAB = new CreativeModeTab("custombgm")
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(CustomBGMItems.BGM);
		}
	};
}