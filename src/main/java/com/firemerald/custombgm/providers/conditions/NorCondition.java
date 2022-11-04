package com.firemerald.custombgm.providers.conditions;

import java.util.Arrays;
import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class NorCondition
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "nor");

	public static Predicate<Player> serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		return CompoundCondition.serialize(json, conditionContext, conditions -> player -> Arrays.stream(conditions).noneMatch(c -> c.test(player)));
	}
}