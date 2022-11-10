package com.firemerald.custombgm.datagen.impl.providers.conditions;

import com.firemerald.custombgm.providers.conditions.Conditions;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public class NeverConditionBuilder extends ProviderConditionBuilder
{
	public static NeverConditionBuilder INSTANCE = new NeverConditionBuilder();

	private NeverConditionBuilder() {}

	@Override
	public ResourceLocation getID()
	{
		return Conditions.NEVER_ID;
	}

	@Override
	public void compile(JsonObject obj) {}
}