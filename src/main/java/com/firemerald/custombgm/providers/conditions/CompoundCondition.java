package com.firemerald.custombgm.providers.conditions;

import java.util.function.Function;
import java.util.function.Predicate;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public abstract class CompoundCondition
{
	@SuppressWarnings("unchecked")
	public static Predicate<PlayerConditionData> serialize(JsonObject json, ICondition.IContext conditionContext, Function<Predicate<PlayerConditionData>[], Predicate<PlayerConditionData>> constructor)
	{
		if (!json.has("conditions")) throw new JsonSyntaxException("Missing \"conditions\", expected to find an array of objects");
		JsonArray condAr = GsonHelper.getAsJsonArray(json, "conditions");
		if (condAr.isEmpty()) throw new JsonSyntaxException("Invalid \"conditions\", expected to find an array of objects");
		Predicate<PlayerConditionData>[] conditions = new Predicate[condAr.size()];
		for (int i = 0; i < conditions.length; ++i)
		{
			JsonElement el = condAr.get(i);
			if (!el.isJsonObject()) throw new JsonSyntaxException("Invalid \"conditions\", expected to find an array of objects");
			conditions[i] = Conditions.serialize(el.getAsJsonObject(), conditionContext);
		}
		return constructor.apply(conditions);
	}
}