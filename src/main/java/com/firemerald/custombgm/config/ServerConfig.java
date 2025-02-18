package com.firemerald.custombgm.config;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ServerConfig {
	private static final IntValue TRACKING_TIMEOUT;
	private static final IntValue ATTACK_TIMEOUT;
	public static final ForgeConfigSpec SPEC;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Server settings").push("server");
        TRACKING_TIMEOUT = builder
        		.comment("How many ticks must pass after a player has been targeted by another entity before removing them from the targeting tracker.")
        		.translation("custombgm.config.tracking_timeout")
        		.defineInRange("tracking_timeout", 0, 0, Integer.MAX_VALUE);
        ATTACK_TIMEOUT = builder
        		.comment("How many ticks must pass after a player has been attacked by another entity before removing them from the targeting tracker.")
        		.translation("custombgm.config.attack_timeout")
        		.defineInRange("attack_timeout", 200, 0, Integer.MAX_VALUE);
        SPEC = builder.build();
	}

	public static int trackingTimeout;
	public static int attackTimeout;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    	if (event.getConfig().getSpec() == SPEC && SPEC.isLoaded()) loadConfig();
    }

    public static void loadConfig() {
    	trackingTimeout = TRACKING_TIMEOUT.get();
    	attackTimeout = ATTACK_TIMEOUT.get();
    }
}
