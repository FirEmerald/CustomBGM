package com.firemerald.custombgm.providers.conditions;

import java.util.Arrays;
import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class NandCondition
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "nand");

	public static Predicate<PlayerConditionData> serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		return CompoundCondition.serialize(json, conditionContext, conditions -> player -> !Arrays.stream(conditions).allMatch(c -> c.test(player)));
	}
}