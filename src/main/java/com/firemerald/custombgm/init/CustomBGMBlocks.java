package com.firemerald.custombgm.init;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.blocks.ActivatorDetectorRailBlock;
import com.firemerald.fecore.init.registry.BlockObject;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class CustomBGMBlocks
{
	public static final Item.Properties ITEM_PROPERTIES = CustomBGMItems.ITEM_PROPERTIES;

	public static final BlockObject<ActivatorDetectorRailBlock, BlockItem> ACTIVATOR_DETECTOR_RAIL = CustomBGMMod.REGISTRY.registerBlock(RegistryNames.ACTIVATOR_DETECTOR_RAIL, () -> new ActivatorDetectorRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)), block -> new BlockItem(block.get(), ITEM_PROPERTIES));

	public static void init() {}
}