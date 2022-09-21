package com.firemerald.custombgm.datagen;

import java.util.Arrays;

import com.firemerald.custombgm.init.CustomBGMBlocks;

import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;

public class CustomBGMBlockLoot extends BlockLoot
{
	@Override
	protected void addTables()
	{
		this.dropSelf(CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL.getBlock());
	}

	@Override
	protected Iterable<Block> getKnownBlocks()
	{
		return Arrays.asList(
				CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL.getBlock()
				);
	}
}
