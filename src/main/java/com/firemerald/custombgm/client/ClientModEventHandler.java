package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.client.model.CustomBGMModelLayers;
import com.firemerald.custombgm.common.CommonModEventHandler;
import com.firemerald.custombgm.datagen.CustomBGMClientMusicProviderProvider;
import com.firemerald.custombgm.datagen.CustomBGMModelProvider;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.providers.Providers;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.data.DataProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler
{
	private static Providers bgmProviders;
	public static final KeyMapping TRACKS_MENU = new KeyMapping("key.custombgm.tracks_menu", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.custombgm");

	public static Providers getBGMProviders() {
		return bgmProviders;
	}

	@SubscribeEvent
	public static void onRegisterClientReloadListeners(AddClientReloadListenersEvent event) {
		event.addListener(CustomBGMAPI.id("resource_pack_providers"), bgmProviders = Providers.forResourcePacks());
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		CustomBGMModelLayers.registerLayerDefinitions(event);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		CustomBGMEntities.registerRenderers(event);
	}

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event)
	{
		event.getGenerator().addProvider(true, (DataProvider.Factory<CustomBGMModelProvider>) CustomBGMModelProvider::new);
		event.getGenerator().addProvider(true, (DataProvider.Factory<CustomBGMClientMusicProviderProvider>) (output -> new CustomBGMClientMusicProviderProvider(output, event.getLookupProvider())));
		CommonModEventHandler.gatherData(event);
	}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
    	event.register(TRACKS_MENU);
    }
}