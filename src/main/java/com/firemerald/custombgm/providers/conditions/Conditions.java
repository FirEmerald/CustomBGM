package com.firemerald.custombgm.providers.conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.BGMProviderConditionSerializer;
import com.firemerald.custombgm.api.RegisterBGMProviderConditionSerializersEvent;
import com.firemerald.custombgm.common.CommonEventHandler;
import com.google.gson.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class Conditions
{
	public static final Predicate<Player>
	ALWAYS = player -> true,
	NEVER = player -> false;
	private static final Map<String, BGMProviderConditionSerializer> PROVIDERS = new HashMap<>();

	public static void registerProviderConditions()
	{
		CustomBGMMod.LOGGER.info("Now registering BGM provider condition serializers");
		PROVIDERS.clear();
		RegisterBGMProviderConditionSerializersEvent event = new RegisterBGMProviderConditionSerializersEvent((id, provider) -> {
			//CustomBGMAPI.LOGGER.debug("Attempting to register BGM provider condition serializer with name " + id);
			if (id == null)
			{
				CustomBGMMod.LOGGER.error("Tried to register BGM provider condition serializer with null name");
				return false;
			}
			String idStr = id.toString();
			if (provider == null)
			{
				CustomBGMMod.LOGGER.error("Tried to register null BGM provider condition serializer with name " + idStr);
				return false;
			}
			if (PROVIDERS.containsKey(idStr))
			{
				CustomBGMMod.LOGGER.error("Tried to register BGM provider serializer with existing name " + idStr);
				return false;
			}
			else
			{
				CustomBGMMod.LOGGER.debug("Registering BGM provider serializer with name " + idStr);
				PROVIDERS.put(idStr, provider);
				return true;
			}
		});
		CommonEventHandler.onRegisterBGMProviderConditionSerializers(event); //TODO WHY IN THE HECK ISN'T THE EVENT LISTENER CATCHING THE EVENT!!!!!!
		MinecraftForge.EVENT_BUS.post(event);
	}

	@Nullable
	public static BGMProviderConditionSerializer getSerializer(ResourceLocation id)
	{
		return getSerializer(id.toString());
	}

	@Nullable
	public static BGMProviderConditionSerializer getSerializer(String id)
	{
		return PROVIDERS.get(id.toString());
	}

	@Nullable
	public static Predicate<Player> serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		String type = GsonHelper.getAsString(json, "type");
		BGMProviderConditionSerializer serializer = getSerializer(type);
		if (serializer == null) throw new JsonParseException(type + " is not a registered BGMProviderCondition serializer");
		return serializer.serialize(json, conditionContext);
	}
}
