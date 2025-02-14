package com.firemerald.custombgm.datagen;

import java.util.concurrent.CompletableFuture;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

public class CustomBGMItemTagsProvider extends ItemTagsProvider {
	public CustomBGMItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider) {
		super(output, lookupProvider, blockTagProvider, CustomBGMAPI.MOD_ID);
	}

	@Override
	public String getName() {
	      return "CustomBGM Item Tags";
	}

	@Override
	protected void addTags(Provider provider) {
		this.copy(BlockTags.RAILS, ItemTags.RAILS);
	}
}
