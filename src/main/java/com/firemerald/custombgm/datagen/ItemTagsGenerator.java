package com.firemerald.custombgm.datagen;

import com.firemerald.custombgm.init.CustomBGMBlocks;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagsGenerator extends TagsProvider<Item>
{
	@SuppressWarnings("deprecation")
	public ItemTagsGenerator(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper)
	{
		super(generator, Registry.ITEM, modId, existingFileHelper);
	}

	@Override
	public String getName()
	{
	      return "CustomBGM Item Tags";
	}

	@Override
	protected void addTags()
	{
		this.tag(ItemTags.RAILS).add(CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL.asItem());
	}
}
