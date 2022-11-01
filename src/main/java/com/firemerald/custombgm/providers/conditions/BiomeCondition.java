package com.firemerald.custombgm.providers.conditions;

import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class BiomeCondition implements Predicate<Player>
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "biome");

	@SuppressWarnings("unchecked")
	public static BiomeCondition serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		if (!json.has("tags") && !json.has("biomes")) throw new JsonSyntaxException("Missing \"tags\" or \"biomes\", must have one or both, expected to find a string or array of strings");
		TagKey<Biome>[] tags;
		if (json.has("tags"))
		{
			JsonElement tagsEl = json.get("tags");
			if (tagsEl.isJsonArray())
			{
				JsonArray tagsAr = tagsEl.getAsJsonArray();
				if (tagsAr.isEmpty()) throw new JsonSyntaxException("Invalid \"tags\", expected to find a string or array of strings");
				tags = new TagKey[tagsAr.size()];
				for (int i = 0; i < tags.length; ++i)
				{
					JsonElement el = tagsAr.get(i);
					if (!el.isJsonPrimitive()) throw new JsonSyntaxException("Invalid \"tags\", expected to find a string or array of strings");
					tags[i] = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(el.getAsString()));
				}
			}
			else if (tagsEl.isJsonPrimitive())
			{
				tags = new TagKey[] {TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(tagsEl.getAsString()))};
			}
			else throw new JsonSyntaxException("Invalid \"tags\", expected to find a string or array of strings");
		}
		else tags = new TagKey[0];
		ResourceLocation[] biomes;
		if (json.has("biomes"))
		{
			JsonElement biomesEl = json.get("biomes");
			if (biomesEl.isJsonArray())
			{
				JsonArray biomesAr = biomesEl.getAsJsonArray();
				if (biomesAr.isEmpty()) throw new JsonSyntaxException("Invalid \"biomes\", expected to find a string or array of strings");
				biomes = new ResourceLocation[biomesAr.size()];
				for (int i = 0; i < tags.length; ++i)
				{
					JsonElement el = biomesAr.get(i);
					if (!el.isJsonPrimitive()) throw new JsonSyntaxException("Invalid \"biomes\", expected to find a string or array of strings");
					biomes[i] = new ResourceLocation(el.getAsString());
				}
			}
			else if (biomesEl.isJsonPrimitive())
			{
				biomes = new ResourceLocation[] {new ResourceLocation(biomesEl.getAsString())};
			}
			else throw new JsonSyntaxException("Invalid \"biomes\", expected to find a string or array of strings");
		}
		else biomes = new ResourceLocation[0];
		return new BiomeCondition(tags, biomes);
	}

	public final TagKey<Biome>[] tags;
	public final ResourceLocation[] biomes;

	public BiomeCondition(TagKey<Biome>[] tags, ResourceLocation[] biomes)
	{
		this.tags = tags;
		this.biomes = biomes;
	}

	@Override
	public boolean test(Player player)
	{
		Holder<Biome> biome = player.getLevel().getBiome(player.blockPosition());
		for (TagKey<Biome> tag : tags)
			if (biome.containsTag(tag)) return true;
		for (ResourceLocation biome2 : biomes)
			if (biome.is(biome2)) return true;
		return false;
	}
}