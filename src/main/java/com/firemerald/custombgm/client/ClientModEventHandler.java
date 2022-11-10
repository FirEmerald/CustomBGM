package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.init.CustomBGMBlocks;
import com.firemerald.custombgm.init.CustomBGMEntities;
import com.firemerald.custombgm.providers.Providers;

import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CustomBGMAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler
{
	private static Providers bgmProviders;

	public static Providers getBGMProviders()
	{
		return bgmProviders;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event)
    {
    	CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL.setRenderLayer(RenderType.cutout());
        LayerDefinition minecartBody = MinecartModel.createBodyLayer();
    	ForgeHooksClient.registerLayerDefinition(CustomBGMModelLayers.BGM_MINECART, () -> minecartBody);
    	ForgeHooksClient.registerLayerDefinition(CustomBGMModelLayers.ENTITY_TESTER_MINECART, () -> minecartBody);
    	ForgeHooksClient.registerLayerDefinition(CustomBGMModelLayers.BOSS_SPAWNER_MINECART, () -> minecartBody);
    	EntityRenderers.register(CustomBGMEntities.BGM_MINECART.get(), (context) -> new MinecartRenderer(context, CustomBGMModelLayers.BGM_MINECART));
    	EntityRenderers.register(CustomBGMEntities.ENTITY_TESTER_MINECART.get(), (context) -> new MinecartRenderer(context, CustomBGMModelLayers.ENTITY_TESTER_MINECART));
    	EntityRenderers.register(CustomBGMEntities.BOSS_SPAWNER_MINECART.get(), (context) -> new MinecartRenderer(context, CustomBGMModelLayers.BOSS_SPAWNER_MINECART));
    }

	@SubscribeEvent
	public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event)
	{
		event.registerReloadListener(new ReloadListener());
		event.registerReloadListener(bgmProviders = Providers.forResourcePacks());
	}
}