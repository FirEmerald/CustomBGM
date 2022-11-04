package com.firemerald.custombgm.datagen.impl.providers.conditions;

import com.firemerald.custombgm.datagen.impl.BuilderBase;
import com.firemerald.custombgm.providers.conditions.NotCondition;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public class NotConditionBuilder extends ProviderConditionBuilder
{
	private final BuilderBase condition;
	
	public NotConditionBuilder(ProviderConditionBuilder condition)
	{
		this.condition = condition;
	}

	@Override
	public ResourceLocation getID()
	{
		return NotCondition.SERIALIZER_ID;
	}

	@Override
	public void compile(JsonObject obj)
	{
		obj.add("condition", condition.compile());
	}
}