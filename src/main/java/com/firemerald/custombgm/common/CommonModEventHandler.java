package com.firemerald.custombgm.common;

import java.util.List;
import java.util.Set;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.datagen.CustomBGMBlockLootSubProvider;
import com.firemerald.custombgm.datagen.CustomBGMBlockTagsProvider;
import com.firemerald.custombgm.datagen.CustomBGMItemTagsProvider;
import com.firemerald.custombgm.datagen.CustomBGMRecipeProvider;
import com.firemerald.custombgm.datagen.CustomBGMServerMusicProviderProvider;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.init.CustomBGMObjects;
import com.firemerald.custombgm.network.clientbound.MusicSyncPacket;
import com.firemerald.fecore.network.NetworkUtil;

import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CommonModEventHandler {
	@SubscribeEvent
	public static void registerRegistries(NewRegistryEvent event) {
		event.register(CustomBGMRegistries.PROVIDER_CODECS);
		event.register(CustomBGMRegistries.CONDITION_CODECS);
		event.register(CustomBGMRegistries.VOLUME_CODECS);
	}

	@SubscribeEvent
	public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(CustomBGMAPI.MOD_ID);
		NetworkUtil.playToClient(registrar, MusicSyncPacket.TYPE, MusicSyncPacket::new);
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			DispenserBlock.registerBehavior(CustomBGMObjects.BGM_MINECART, new MinecartDispenseItemBehavior(CustomBGMEntities.BGM_MINECART.get()));
			DispenserBlock.registerBehavior(CustomBGMObjects.ENTITY_TESTER_MINECART, new MinecartDispenseItemBehavior(CustomBGMEntities.ENTITY_TESTER_MINECART.get()));
			DispenserBlock.registerBehavior(CustomBGMObjects.BOSS_SPAWNER_MINECART, new MinecartDispenseItemBehavior(CustomBGMEntities.BOSS_SPAWNER_MINECART.get()));
		});
	}

	@SubscribeEvent
	public static void gatherServerData(GatherDataEvent.Server event) {
		gatherData(event);
	}

	public static void gatherData(GatherDataEvent event) {
		event.createBlockAndItemTags(CustomBGMBlockTagsProvider::new, CustomBGMItemTagsProvider::new);
		event.getGenerator().addProvider(
				true,
				(DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(
						output,
						Set.of(),
						List.of(new SubProviderEntry(CustomBGMBlockLootSubProvider::new, LootContextParamSets.BLOCK)),
						event.getLookupProvider()
						)
				);
		event.getGenerator().addProvider(
	            true,
	            (DataProvider.Factory<CustomBGMRecipeProvider.Runner>) output -> new CustomBGMRecipeProvider.Runner(output, event.getLookupProvider())
	    );
		event.getGenerator().addProvider(true, (DataProvider.Factory<CustomBGMServerMusicProviderProvider>) (output -> new CustomBGMServerMusicProviderProvider(output, event.getLookupProvider())));
	}
}
