package com.firemerald.custombgm.datagen.impl.providers.conditions;

import com.firemerald.custombgm.providers.conditions.Conditions;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public class AlwaysConditionBuilder extends ProviderConditionBuilder
{
	public static AlwaysConditionBuilder INSTANCE = new AlwaysConditionBuilder();
	
	private AlwaysConditionBuilder() {}
	
	@Override
	public ResourceLocation getID()
	{
		return Conditions.ALWAYS_ID;
	}

	@Override
	public void compile(JsonObject obj) {}
}