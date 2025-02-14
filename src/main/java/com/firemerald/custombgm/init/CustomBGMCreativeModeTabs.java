package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.fecore.init.registry.RegistryUtil;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomBGMCreativeModeTabs
{
	private static DeferredRegister<CreativeModeTab> registry = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CustomBGMAPI.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CUSTOM_BGM = RegistryUtil.registerTab(registry, "custombgm", CustomBGMObjects.BGM,
			CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL,
			CustomBGMObjects.BGM,
			CustomBGMObjects.ENTITY_TESTER,
			CustomBGMObjects.BOSS_SPAWNER,
			CustomBGMObjects.BGM_MINECART,
			CustomBGMObjects.ENTITY_TESTER_MINECART,
			CustomBGMObjects.BOSS_SPAWNER_MINECART);

	public static void init(IEventBus bus)
	{
		registry.register(bus);
		registry = null;
	}
}