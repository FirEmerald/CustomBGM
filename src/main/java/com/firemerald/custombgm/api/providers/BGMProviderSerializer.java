package com.firemerald.custombgm.api.providers;

import java.util.function.Predicate;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.google.gson.JsonObject;

import net.minecraftforge.common.crafting.conditions.ICondition;

@FunctionalInterface
public interface BGMProviderSerializer
{
	public BGMProvider serialize(JsonObject json, int priority, Predicate<PlayerConditionData> condition, ICondition.IContext conditionContext);
}