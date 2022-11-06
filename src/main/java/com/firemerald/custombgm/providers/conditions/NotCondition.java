package com.firemerald.custombgm.providers.conditions;

import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class NotCondition
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "not");

	public static Predicate<PlayerConditionData> serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		if (!json.has("condition")) throw new JsonSyntaxException("Missing \"condition\", expected to find an objects");
		return Conditions.serialize(GsonHelper.getAsJsonObject(json, "condition"), conditionContext).negate();
	}
}