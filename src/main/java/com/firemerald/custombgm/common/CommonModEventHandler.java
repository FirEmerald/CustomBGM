package com.firemerald.custombgm.common;

import java.util.List;
import java.util.Set;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.datagen.CustomBGMBlockLootSubProvider;
import com.firemerald.custombgm.datagen.CustomBGMBlockTagsProvider;
import com.firemerald.custombgm.datagen.CustomBGMItemTagsProvider;
import com.firemerald.custombgm.datagen.CustomBGMRecipeProvider;
import com.firemerald.custombgm.datagen.CustomBGMServerMusicProviderProvider;

import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CommonModEventHandler {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(),
				(DataProvider.Factory<CustomBGMBlockTagsProvider>) output -> new CustomBGMBlockTagsProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
		event.getGenerator().addProvider(event.includeServer(),
				(DataProvider.Factory<CustomBGMItemTagsProvider>) output -> new CustomBGMItemTagsProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
		event.getGenerator().addProvider(event.includeServer(),
				(DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(
						output,
						Set.of(),
						List.of(new SubProviderEntry(CustomBGMBlockLootSubProvider::new, LootContextParamSets.BLOCK))
						));
		event.getGenerator().addProvider(event.includeServer(),
	            (DataProvider.Factory<CustomBGMRecipeProvider>) CustomBGMRecipeProvider::new);
		event.getGenerator().addProvider(event.includeServer(),
				(DataProvider.Factory<CustomBGMServerMusicProviderProvider>) (output -> new CustomBGMServerMusicProviderProvider(output, event.getLookupProvider())));
	}
}
