package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CustomBGMAPI.MOD_ID)
public class CustomBGMItems
{
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_BGM)
	public static final BlockItem BGM = null;
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_ENTITY_TESTER)
	public static final BlockItem ENTITY_TESTER = null;
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_BOSS_SPAWNER)
	public static final BlockItem BOSS_SPAWNER = null;
	
	public static void registerItems(IEventBus eventBus)
	{
		DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, CustomBGMAPI.MOD_ID);
		items.register(RegistryNames.BLOCK_ENTITY_BGM, () -> new BlockItem(CustomBGMBlocks.BGM, new Item.Properties().tab(CustomBGMTabs.TAB)));
		items.register(RegistryNames.BLOCK_ENTITY_ENTITY_TESTER, () -> new BlockItem(CustomBGMBlocks.ENTITY_TESTER, new Item.Properties().tab(CustomBGMTabs.TAB)));
		items.register(RegistryNames.BLOCK_ENTITY_BOSS_SPAWNER, () -> new BlockItem(CustomBGMBlocks.BOSS_SPAWNER, new Item.Properties().tab(CustomBGMTabs.TAB)));
		items.register(eventBus);
	}
}