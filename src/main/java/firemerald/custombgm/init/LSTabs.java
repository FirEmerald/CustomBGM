package firemerald.custombgm.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class LSTabs
{
	public static final CreativeTabs TAB = new CreativeTabs("custombgm")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(LSItems.BGM);
		}
	};

	public static void init() {}
}