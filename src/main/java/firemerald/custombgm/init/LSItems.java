package firemerald.custombgm.init;

import static firemerald.api.core.InitFunctions.addItem;
import static firemerald.api.core.InitFunctions.addItemBlock;
import static firemerald.api.core.InitFunctions.registerItemBlockModels;
import static firemerald.api.core.InitFunctions.registerItemModels;

import firemerald.custombgm.items.ItemShapeTool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class LSItems
{
	public static final ItemShapeTool SHAPE_TOOL = new ItemShapeTool();
	public static final ItemBlock BGM = new ItemBlock(LSBlocks.BGM);
	public static final ItemBlock ENTITY_TESTER = new ItemBlock(LSBlocks.ENTITY_TESTER);
	public static final ItemBlock BOSS_SPAWNER = new ItemBlock(LSBlocks.BOSS_SPAWNER);

	public static void init(IForgeRegistry<Item> registry)
	{
		addItem(SHAPE_TOOL, "shape_tool", registry);
		addItemBlock(BGM, registry);
		addItemBlock(ENTITY_TESTER, registry);
		addItemBlock(BOSS_SPAWNER, registry);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		registerItemModels(SHAPE_TOOL);
		registerItemBlockModels(BGM);
		registerItemBlockModels(ENTITY_TESTER);
		registerItemBlockModels(BOSS_SPAWNER);
	}
}