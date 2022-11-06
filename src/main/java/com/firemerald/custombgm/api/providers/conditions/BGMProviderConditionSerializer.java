package com.firemerald.custombgm.api.providers.conditions;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraftforge.common.crafting.conditions.ICondition;

@FunctionalInterface
public interface BGMProviderConditionSerializer
{
	public Predicate<PlayerConditionData> serialize(JsonObject json, ICondition.IContext conditionContext);
}