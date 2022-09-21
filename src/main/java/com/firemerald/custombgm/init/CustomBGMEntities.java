package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.entity.BGMMinecart;
import com.firemerald.custombgm.entity.BossSpawnerMinecart;
import com.firemerald.custombgm.entity.EntityTesterMinecart;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CustomBGMEntities
{
	private static DeferredRegister<EntityType<?>> registry = DeferredRegister.create(ForgeRegistries.ENTITIES, CustomBGMAPI.MOD_ID);

	public static final RegistryObject<EntityType<?>> BGM_MINECART = registry.register(RegistryNames.MINECART_ENTITY_BGM, () -> EntityType.Builder.of(BGMMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build(RegistryNames.MINECART_ENTITY_BGM));
	public static final RegistryObject<EntityType<?>> ENTITY_TESTER_MINECART = registry.register(RegistryNames.MINECART_ENTITY_ENTITY_TESTER, () -> EntityType.Builder.of(EntityTesterMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build(RegistryNames.MINECART_ENTITY_ENTITY_TESTER));
	public static final RegistryObject<EntityType<?>> BOSS_SPAWNER_MINECART = registry.register(RegistryNames.MINECART_ENTITY_BOSS_SPAWNER, () -> EntityType.Builder.of(BossSpawnerMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build(RegistryNames.MINECART_ENTITY_BOSS_SPAWNER));

	public static void init(IEventBus bus)
	{
		registry.register(bus);
		registry = null;
	}
}