package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.client.model.CustomBGMModelLayers;
import com.firemerald.custombgm.entity.BGMMinecart;
import com.firemerald.custombgm.entity.BossSpawnerMinecart;
import com.firemerald.custombgm.entity.EntityTesterMinecart;
import com.firemerald.fecore.init.registry.RegistryUtil;

import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomBGMEntities
{
	private static DeferredRegister<EntityType<?>> registry = DeferredRegister.create(Registries.ENTITY_TYPE, CustomBGMAPI.MOD_ID);

	public static final DeferredHolder<EntityType<?>, EntityType<BGMMinecart<?, ?>>> BGM_MINECART = RegistryUtil.registerEntityType(registry, "bgm_minecart", () -> EntityType.Builder.<BGMMinecart<?, ?>>of(BGMMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityTesterMinecart<?, ?>>> ENTITY_TESTER_MINECART = RegistryUtil.registerEntityType(registry, "entity_tester_minecart", () -> EntityType.Builder.<EntityTesterMinecart<?, ?>>of(EntityTesterMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
	public static final DeferredHolder<EntityType<?>, EntityType<BossSpawnerMinecart<?, ?>>> BOSS_SPAWNER_MINECART = RegistryUtil.registerEntityType(registry, "boss_spawner_minecart", () -> EntityType.Builder.<BossSpawnerMinecart<?, ?>>of(BossSpawnerMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));

	public static void init(IEventBus bus)
	{
		registry.register(bus);
		registry = null;
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(BGM_MINECART.get(), context -> new MinecartRenderer(context, CustomBGMModelLayers.BGM_MINECART));
		event.registerEntityRenderer(ENTITY_TESTER_MINECART.get(), context -> new MinecartRenderer(context, CustomBGMModelLayers.ENTITY_TESTER_MINECART));
		event.registerEntityRenderer(BOSS_SPAWNER_MINECART.get(), context -> new MinecartRenderer(context, CustomBGMModelLayers.BOSS_SPAWNER_MINECART));
	}
}