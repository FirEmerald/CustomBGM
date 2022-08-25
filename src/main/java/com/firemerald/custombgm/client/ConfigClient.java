package com.firemerald.custombgm.client;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ConfigClient
{	
	public final ConfigValue<List<String>> titleMusic;
	public final BooleanValue cacheAll;

	@SuppressWarnings("unchecked")
	public ConfigClient(ForgeConfigSpec.Builder builder)
	{
        builder.comment("Client only settings").push("client");
        titleMusic = (ConfigValue<List<String>>) (Object) builder
        		.comment("A list of loops to override the default menu music.")
        		.translation("custombgm.config.titlemusic")
        		.defineList("title_music", Collections.emptyList(), o -> {
        			return o instanceof String;
        		});
        cacheAll = builder
        		.comment("Whether all BGM files should be cached on disk at load or when they are played. This value does not update mid-game.")
        		.translation("custombgm.config.cacheall")
        		.define("cache_all", false);
	}
}