package com.firemerald.custombgm.providers;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.BGMProvider;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.providers.conditions.Conditions;
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
		if (!json.has("music")) throw new JsonSyntaxException("Missing \"music\", expected to find a string or array of strings"); //TODO weighted music list
		JsonElement musicEl = json.get("music");
		ResourceLocation[] tracks;
		if (musicEl.isJsonArray())
		{
			JsonArray musicAr = musicEl.getAsJsonArray();
			if (musicAr.isEmpty()) throw new JsonSyntaxException("Invalid \"music\", expected to find a string or array of strings");
			tracks = new ResourceLocation[musicAr.size()];
			for (int i = 0; i < tracks.length; ++i)
			{
				JsonElement el = musicAr.get(i);
				if (!el.isJsonPrimitive()) throw new JsonSyntaxException("Invalid \"music\", expected to find a string or array of strings");
				else tracks[i] = new ResourceLocation(el.getAsString());
			}
		}
		else if (musicEl.isJsonPrimitive()) tracks = new ResourceLocation[] {new ResourceLocation(musicEl.getAsString())};
		else throw new JsonSyntaxException("Invalid \"music\", expected to find a string or array of strings");
		Predicate<Player> condition;
		if (json.has("condition"))
		{
			JsonObject obj = GsonHelper.getAsJsonObject(json, "condition");
			condition = Conditions.serialize(obj, conditionContext);
		}
		else condition = Conditions.ALWAYS;
		return new BaseMusicProvider(priority, tracks, condition);
	}
	
	public final ResourceLocation[] tracks;
	public final Predicate<Player> condition;
	
	public BaseMusicProvider(int priority, ResourceLocation[] tracks, Predicate<Player> condition)
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