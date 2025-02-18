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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class CustomBGMItemTagsProvider extends TagsProvider<Item> {
	public CustomBGMItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.ITEM, lookupProvider, CustomBGMAPI.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
	      return "CustomBGM Item Tags";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(Provider provider) {
		this.tag(ItemTags.RAILS).add((ResourceKey<Item>) ((RegistryObject<? extends Item>) CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL.item).getKey());
	}
}
