package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.client.model.CustomBGMModelLayers;
import com.firemerald.custombgm.datagen.CustomBGMClientMusicProviderProvider;
import com.firemerald.custombgm.datagen.CustomBGMModelProvider;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.providers.Providers;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.data.DataProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler
{
	private static Providers bgmProviders;
	public static final KeyMapping TRACKS_MENU = new KeyMapping("key.custombgm.tracks_menu", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.custombgm");

	public static Providers getBGMProviders() {
		return bgmProviders;
	}

	@SubscribeEvent
	public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(bgmProviders = Providers.forResourcePacks());
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
	public static void gatherClientData(GatherDataEvent event)
	{
		event.getGenerator().addProvider(event.includeClient(),
				(DataProvider.Factory<CustomBGMModelProvider>) output -> new CustomBGMModelProvider(output, event.getExistingFileHelper()));
		event.getGenerator().addProvider(event.includeClient(),
				(DataProvider.Factory<CustomBGMClientMusicProviderProvider>) (output -> new CustomBGMClientMusicProviderProvider(output, event.getLookupProvider())));
	}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
    	event.register(TRACKS_MENU);
    }
}