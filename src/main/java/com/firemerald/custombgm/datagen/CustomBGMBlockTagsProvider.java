package com.firemerald.custombgm.datagen;

import java.util.concurrent.CompletableFuture;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.init.CustomBGMObjects;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class CustomBGMBlockTagsProvider extends BlockTagsProvider {
	public CustomBGMBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, CustomBGMAPI.MOD_ID);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(BlockTags.RAILS).add(CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL.block.getKey());
	}
}
