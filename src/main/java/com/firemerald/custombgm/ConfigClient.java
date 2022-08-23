package com.firemerald.custombgm;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ConfigClient
{	
	public final ConfigValue<List<String>> titleMusic;

	@SuppressWarnings("unchecked")
	public ConfigClient(ForgeConfigSpec.Builder builder)
	{
        builder.comment("Client only settings").push("client");
        titleMusic = (ConfigValue<List<String>>) (Object) builder
        		.comment("A list of loops to override the default menu music.")
        		.translation("custombgm.config.titlemusic")
        		.defineList("title_music", Collections.emptyList(), o -> {
        			List<?> l = (List<?>) o;
        			for (Object v : l) if (!(v instanceof String)) return false;
        			return true;
        		});
	}
}