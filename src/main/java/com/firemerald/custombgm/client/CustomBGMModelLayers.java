package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class CustomBGMModelLayers
{
	public static final ModelLayerLocation
	BGM_MINECART = new ModelLayerLocation(new ResourceLocation(CustomBGMAPI.MOD_ID, "bgm_minecart"), "main"),
	ENTITY_TESTER_MINECART = new ModelLayerLocation(new ResourceLocation(CustomBGMAPI.MOD_ID, "entity_tester_minecart"), "main"),
	BOSS_SPAWNER_MINECART = new ModelLayerLocation(new ResourceLocation(CustomBGMAPI.MOD_ID, "boss_spawner_minecart"), "main");
}
