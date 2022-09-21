package com.firemerald.custombgm.init;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.entity.BGMMinecart;
import com.firemerald.custombgm.entity.BossSpawnerMinecart;
import com.firemerald.custombgm.entity.EntityTesterMinecart;
import com.firemerald.custombgm.item.CustomMinecartItem;
import com.firemerald.custombgm.operators.BGMOperator;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.EntityTesterOperator;
import com.firemerald.fecore.init.registry.ItemObject;

import net.minecraft.world.item.Item;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CustomBGMItems
{
	public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CustomBGMTabs.TAB);

	public static final ItemObject<CustomMinecartItem<?, ?>> BGM_MINECART_ITEM = CustomBGMMod.REGISTRY.registerItem(RegistryNames.MINECART_ENTITY_BGM, () -> new CustomMinecartItem(BGMMinecart::new, BGMOperator::addTooltip, ITEM_PROPERTIES));
	public static final ItemObject<CustomMinecartItem<?, ?>> ENTITY_TESTER_MINECART_ITEM = CustomBGMMod.REGISTRY.registerItem(RegistryNames.MINECART_ENTITY_ENTITY_TESTER, () -> new CustomMinecartItem(EntityTesterMinecart::new, EntityTesterOperator::addTooltip, ITEM_PROPERTIES));
	public static final ItemObject<CustomMinecartItem<?, ?>> BOSS_SPAWNER_MINECART_ITEM = CustomBGMMod.REGISTRY.registerItem(RegistryNames.MINECART_ENTITY_BOSS_SPAWNER, () -> new CustomMinecartItem(BossSpawnerMinecart::new, BossSpawnerOperator::addTooltip, ITEM_PROPERTIES));

	public static void init() {}
}