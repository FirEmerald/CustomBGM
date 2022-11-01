package com.firemerald.custombgm.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.BGMProvider;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.providers.conditions.Conditions;
import com.firemerald.fecore.util.distribution.IDistribution;
import com.firemerald.fecore.util.distribution.SingletonDistribution;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class BaseMusicProvider extends BGMProvider
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "base");

	public static BaseMusicProvider serialize(JsonObject json, int priority, ICondition.IContext conditionContext)
	{
		if (!json.has("music")) throw new JsonSyntaxException("Missing \"music\", expected to find a string, array of strings, or set of float values");
		JsonElement musicEl = json.get("music");
		IDistribution<ResourceLocation> tracks;
		if (musicEl.isJsonObject())
		{
			Set<Entry<String, JsonElement>> musicEntries = musicEl.getAsJsonObject().entrySet();
			if (musicEntries.isEmpty()) throw new JsonSyntaxException("Invalid \"music\", expected to find a string, array of strings, or set of float values");
			Map<ResourceLocation, Float> tracksMap = new HashMap<>(musicEntries.size());
			for (Entry<String, JsonElement> entry : musicEntries)
			{
				if (!entry.getValue().isJsonPrimitive()) throw new JsonSyntaxException("Invalid \"music\", expected to find a string, array of strings, or set of float values");
				else try
				{
					float weight = entry.getValue().getAsJsonPrimitive().getAsFloat();
					tracksMap.compute(new ResourceLocation(entry.getKey()), (o, w) -> w == null ? weight : (weight + w));
				}
				catch (NumberFormatException e)
				{
					throw new JsonSyntaxException("Invalid \"music\", expected to find a string, array of strings, or set of float values", e);
				}
			}
			tracks = IDistribution.get(tracksMap);
		}
		else if (musicEl.isJsonArray())
		{
			JsonArray musicAr = musicEl.getAsJsonArray();
			if (musicAr.isEmpty()) throw new JsonSyntaxException("Invalid \"music\", expected to find a string, array of strings, or set of float values");
			ResourceLocation[] tracksAr = new ResourceLocation[musicAr.size()];
			for (int i = 0; i < tracksAr.length; ++i)
			{
				JsonElement el = musicAr.get(i);
				if (!el.isJsonPrimitive()) throw new JsonSyntaxException("Invalid \"music\", expected to find a string, array of strings, or set of float values");
				else tracksAr[i] = new ResourceLocation(el.getAsString());
			}
			tracks = IDistribution.get(tracksAr);
		}
		else if (musicEl.isJsonPrimitive()) tracks = new SingletonDistribution<>(new ResourceLocation(musicEl.getAsString()));
		else throw new JsonSyntaxException("Invalid \"music\", expected to find a string, array of strings, or set of float values");
		Predicate<Player> condition;
		if (json.has("condition"))
		{
			JsonObject obj = GsonHelper.getAsJsonObject(json, "condition");
			condition = Conditions.serialize(obj, conditionContext);
		}
		else condition = Conditions.ALWAYS;
		return new BaseMusicProvider(priority, tracks, condition);
	}

	public final IDistribution<ResourceLocation> tracks;
	public final Predicate<Player> condition;

	public BaseMusicProvider(int priority, IDistribution<ResourceLocation> tracks, Predicate<Player> condition)
	{
		super(priority);
		this.tracks = tracks;
		this.condition = condition;
	}

	@Override
	public ResourceLocation getMusic(Player player, @Nullable ResourceLocation currentMusic)
	{
		if (condition.test(player)) return pickOne(player.getRandom(), currentMusic, tracks);
		else return null;
	}
}