package com.firemerald.custombgm.datagen.impl;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public abstract class BuilderBase
{
	public abstract ResourceLocation getID();
	
	public abstract void compile(JsonObject obj);
	
	public JsonObject compile()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("type", getID().toString());
		compile(obj);
		return obj;
	}
}