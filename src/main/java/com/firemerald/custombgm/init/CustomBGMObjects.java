package com.firemerald.custombgm.init;

import java.util.function.Supplier;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.blockentity.BlockEntityBGM;
import com.firemerald.custombgm.blockentity.BlockEntityBossSpawner;
import com.firemerald.custombgm.blockentity.BlockEntityEntityTester;
import com.firemerald.custombgm.blocks.ActivatorDetectorRailBlock;
import com.firemerald.custombgm.blocks.BlockBGM;
import com.firemerald.custombgm.blocks.BlockBossSpawner;
import com.firemerald.custombgm.blocks.BlockEntityTester;
import com.firemerald.custombgm.item.CustomMinecartItem;
import com.firemerald.custombgm.item.ITooltipProvider;
import com.firemerald.custombgm.operators.BGMOperator;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.EntityTesterOperator;
import com.firemerald.fecore.init.registry.BlockEntityObject;
import com.firemerald.fecore.init.registry.BlockObject;
import com.firemerald.fecore.init.registry.DeferredObjectRegistry;
import com.firemerald.fecore.init.registry.ItemObject;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;

public class CustomBGMObjects {
	private static DeferredObjectRegistry registry = new DeferredObjectRegistry(CustomBGMAPI.MOD_ID);

	public static final BlockEntityObject<BlockEntityBGM<?, ?>, BlockBGM<?, ?>, BlockItem> BGM = registry.registerBlockEntity("bgm", BlockBGM::new, BlockEntityBGM::new);
	public static final BlockEntityObject<BlockEntityEntityTester<?, ?>, BlockEntityTester<?, ?>, BlockItem> ENTITY_TESTER = registry.registerBlockEntity("entity_tester", BlockEntityTester::new, BlockEntityEntityTester::new);
	public static final BlockEntityObject<BlockEntityBossSpawner<?, ?>, BlockBossSpawner<?, ?>, BlockItem> BOSS_SPAWNER = registry.registerBlockEntity("boss_spawner", BlockBossSpawner::new, BlockEntityBossSpawner::new);
	public static final ItemObject<CustomMinecartItem> BGM_MINECART = registry.registerItem("bgm_minecart", key -> createMinecartItem(CustomBGMEntities.BGM_MINECART, BGMOperator::addTooltip, key));
	public static final ItemObject<CustomMinecartItem> ENTITY_TESTER_MINECART = registry.registerItem("entity_tester_minecart", key -> createMinecartItem(CustomBGMEntities.ENTITY_TESTER_MINECART, EntityTesterOperator::addTooltip, key));
	public static final ItemObject<CustomMinecartItem> BOSS_SPAWNER_MINECART = registry.registerItem("boss_spawner_minecart", key -> createMinecartItem(CustomBGMEntities.BOSS_SPAWNER_MINECART, BossSpawnerOperator::addTooltip, key));
	public static final BlockObject<ActivatorDetectorRailBlock, BlockItem> ACTIVATOR_DETECTOR_RAIL = registry.registerBlock("activator_detector_rail", key -> new ActivatorDetectorRailBlock(BlockBehaviour.Properties.of().noCollission().strength(0.7F).sound(SoundType.METAL).setId(key)));

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static CustomMinecartItem createMinecartItem(Supplier entityType, ITooltipProvider tooltip, ResourceKey<Item> key) {
		return new CustomMinecartItem((EntityType<? extends AbstractMinecart>) entityType.get(), tooltip, new Item.Properties().stacksTo(1).setId(key));
	}

	public static void init(IEventBus bus) {
		registry.register(bus);
		registry = null;
	}
}
