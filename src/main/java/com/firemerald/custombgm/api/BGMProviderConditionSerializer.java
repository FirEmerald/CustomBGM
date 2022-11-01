package com.firemerald.custombgm.api;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.crafting.conditions.ICondition;

@FunctionalInterface
public interface BGMProviderConditionSerializer
{
	public Predicate<Player> serialize(JsonObject json, ICondition.IContext conditionContext);
}