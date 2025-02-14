package com.firemerald.custombgm.datagen;

import java.util.concurrent.CompletableFuture;

import com.firemerald.custombgm.init.CustomBGMObjects;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class CustomBGMRecipeProvider extends RecipeProvider
{
    protected CustomBGMRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

	@Override
	protected void buildRecipes() {
		shaped(RecipeCategory.TRANSPORTATION, CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL.getBlock(), 6)
		.define('X', Tags.Items.INGOTS_IRON)
		.define('T', Blocks.REDSTONE_TORCH)
		.define('P', Blocks.STONE_PRESSURE_PLATE)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.pattern("XTX")
		.pattern("XPX")
		.pattern("XRX")
		.unlockedBy("has_rail", has(ItemTags.RAILS))
		.save(output);
	}

    // The runner to add to the data generator
    public static class Runner extends RecipeProvider.Runner {
        // Get the parameters from GatherDataEvent.
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new CustomBGMRecipeProvider(provider, output);
        }

		@Override
		public String getName() {
			return "CustomBGM recipes";
		}
    }
}