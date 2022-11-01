package com.firemerald.custombgm.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigServer
{
	public final IntValue combatTimeout;

	public ConfigServer(ForgeConfigSpec.Builder builder)
	{
        builder.comment("Server settings").push("server");
        combatTimeout = builder
        		.comment("How many ticks after an entity is no longer targeting a player to remove them from the targeting tracker.")
        		.translation("custombgm.config.combattimeout")
        		.defineInRange("combat_timeout", 0, 0, Integer.MAX_VALUE);
	}
}