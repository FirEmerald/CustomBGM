package com.firemerald.custombgm.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.init.CustomBGMObjects;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class CustomBGMBlockTagsProvider extends TagsProvider<Block> {
	public CustomBGMBlockTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BLOCK, lookupProvider, CustomBGMAPI.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
	      return "CustomBGM Block Tags";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(Provider provider) {
		this.tag(BlockTags.RAILS).add((ResourceKey<Block>) ((RegistryObject<? extends Block>) CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL.block).getKey());
	}
}
