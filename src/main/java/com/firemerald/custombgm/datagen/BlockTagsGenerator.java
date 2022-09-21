package com.firemerald.custombgm.datagen;

import com.firemerald.custombgm.init.CustomBGMBlocks;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagsGenerator extends TagsProvider<Block>
{
	@SuppressWarnings("deprecation")
	public BlockTagsGenerator(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper)
	{
		super(generator, Registry.BLOCK, modId, existingFileHelper);
	}

	@Override
	public String getName()
	{
	      return "CustomBGM Block Tags";
	}

	@Override
	protected void addTags()
	{
		this.tag(BlockTags.RAILS).add(CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL.getBlock());
	}
}
