package com.firemerald.custombgm.config;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

@EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ServerConfig {
	private static final IntValue TRACKING_TIMEOUT;
	private static final IntValue ATTACK_TIMEOUT;
	public static final ModConfigSpec SPEC;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
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
    	trackingTimeout = TRACKING_TIMEOUT.getAsInt();
    	attackTimeout = ATTACK_TIMEOUT.getAsInt();
    }
}
