package com.firemerald.custombgm.init;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.blockentity.BlockEntityBGM;
import com.firemerald.custombgm.blockentity.BlockEntityBossSpawner;
import com.firemerald.custombgm.blockentity.BlockEntityEntityTester;
import com.firemerald.custombgm.blocks.BlockBGM;
import com.firemerald.custombgm.blocks.BlockBossSpawner;
import com.firemerald.custombgm.blocks.BlockEntityTester;
import com.firemerald.fecore.init.registry.BlockEntityObject;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class CustomBGMBlockEntities
{
	public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CustomBGMTabs.TAB);

	public static final BlockEntityObject<BlockEntityBGM, BlockBGM, BlockItem> BGM = CustomBGMMod.REGISTRY.registerBlockEntity(RegistryNames.BLOCK_ENTITY_BGM, BlockBGM::new, block -> new BlockItem(block.get(), ITEM_PROPERTIES), BlockEntityBGM::new);
	public static final BlockEntityObject<BlockEntityEntityTester, BlockEntityTester, BlockItem> ENTITY_TESTER = CustomBGMMod.REGISTRY.registerBlockEntity(RegistryNames.BLOCK_ENTITY_ENTITY_TESTER, BlockEntityTester::new, block -> new BlockItem(block.get(), ITEM_PROPERTIES), BlockEntityEntityTester::new);
	public static final BlockEntityObject<BlockEntityBossSpawner, BlockBossSpawner, BlockItem> BOSS_SPAWNER = CustomBGMMod.REGISTRY.registerBlockEntity(RegistryNames.BLOCK_ENTITY_BOSS_SPAWNER, BlockBossSpawner::new, block -> new BlockItem(block.get(), ITEM_PROPERTIES), BlockEntityBossSpawner::new);

	public static void init() {}
}