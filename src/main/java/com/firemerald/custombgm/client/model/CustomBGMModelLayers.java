package com.firemerald.custombgm.client.model;

import java.util.function.Supplier;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class CustomBGMModelLayers {
	public static final ModelLayerLocation BGM_MINECART = register("bgm_minecart");
	public static final ModelLayerLocation ENTITY_TESTER_MINECART = register("entity_tester_minecart");
	public static final ModelLayerLocation BOSS_SPAWNER_MINECART = register("boss_spawner_minecart");

	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		LayerDefinition layerDef = MinecartModel.createBodyLayer();
		Supplier<LayerDefinition> sup = () -> layerDef;
		event.registerLayerDefinition(BGM_MINECART, sup);
		event.registerLayerDefinition(ENTITY_TESTER_MINECART, sup);
		event.registerLayerDefinition(BOSS_SPAWNER_MINECART, sup);
	}

    private static ModelLayerLocation register(String path) {
        return register(path, "main");
    }

    private static ModelLayerLocation register(String path, String model) {
        return createLocation(path, model);
    }

    private static ModelLayerLocation createLocation(String path, String model) {
        return new ModelLayerLocation(CustomBGMAPI.id(path), model);
    }
}
