package com.firemerald.custombgm.api;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.crafting.conditions.ICondition;

@FunctionalInterface
public interface BGMProviderSerializer
{
	public BGMProvider serialize(JsonObject json, int priority, Predicate<Player> condition, ICondition.IContext conditionContext);
}