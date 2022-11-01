package com.firemerald.custombgm.api;

import com.google.gson.JsonObject;

import net.minecraftforge.common.crafting.conditions.ICondition;

@FunctionalInterface
public interface BGMProviderSerializer
{
	public BGMProvider serialize(JsonObject json, int priority, ICondition.IContext conditionContext);
}