package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.config.ClientConfig;

import net.minecraft.client.gui.components.WidgetSprites;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = CustomBGMAPI.MOD_ID, dist = Dist.CLIENT)
public class CustomBGMClient
{
	public static WidgetSprites makeWidgetSprites(String name) {
		return new WidgetSprites(
				CustomBGMAPI.id("icon/" + name),
				CustomBGMAPI.id("icon/" + name + "_disabled"),
				CustomBGMAPI.id("icon/" + name + "_hovered"));
	}

	public static final WidgetSprites PREVIOUS = makeWidgetSprites("previous");
	public static final WidgetSprites RANDOM = makeWidgetSprites("random");
	public static final WidgetSprites NEXT = makeWidgetSprites("next");
	public static final WidgetSprites TRACKS = makeWidgetSprites("tracks");

	public CustomBGMClient(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
