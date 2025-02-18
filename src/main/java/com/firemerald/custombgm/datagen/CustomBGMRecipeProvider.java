package com.firemerald.custombgm.datagen;

import java.util.function.Consumer;

import com.firemerald.custombgm.init.CustomBGMObjects;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

public class CustomBGMRecipeProvider extends RecipeProvider
{
	public CustomBGMRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> register) {
		ShapedRecipeBuilder
		.shaped(RecipeCategory.TRANSPORTATION, CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL, 6)
		.define('X', Tags.Items.INGOTS_IRON)
		.define('T', Blocks.REDSTONE_TORCH)
		.define('P', Blocks.STONE_PRESSURE_PLATE)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.pattern("XTX")
		.pattern("XPX")
		.pattern("XRX")
		.unlockedBy("has_rail", has(ItemTags.RAILS))
		.save(register);
	}
}