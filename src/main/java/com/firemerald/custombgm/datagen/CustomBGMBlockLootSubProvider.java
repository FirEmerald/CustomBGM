package com.firemerald.custombgm.datagen;

import java.util.List;
import java.util.Set;

import com.firemerald.custombgm.init.CustomBGMObjects;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

public class CustomBGMBlockLootSubProvider extends BlockLootSubProvider
{
	public CustomBGMBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

	@Override
	protected Iterable<Block> getKnownBlocks()
	{
		return List.of(CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL.getBlock());
	}

    @Override
    protected void generate() {
        this.dropSelf(CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL.getBlock());
    }
}
