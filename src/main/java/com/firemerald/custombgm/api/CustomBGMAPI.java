package com.firemerald.custombgm.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

public abstract class CustomBGMAPI
{
	public static final String MOD_ID = "custombgm";
    public static final String API_VERSION = "2.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("Custom BGM API");

    public static ResourceLocation id(String name) {
		return new ResourceLocation(MOD_ID, name);
	}
}