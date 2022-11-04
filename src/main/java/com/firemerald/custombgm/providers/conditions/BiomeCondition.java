package com.firemerald.custombgm.providers.conditions;

import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.fecore.util.GsonUtil;
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
		if (json.has("tags")) tags = GsonUtil.arrayFromArrayOrSingle(json.get("tags"), "tags", v -> TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(v)), TagKey[]::new);
		else tags = new TagKey[0];
		ResourceLocation[] biomes;
		if (json.has("biomes")) biomes = GsonUtil.arrayFromArrayOrSingle(json.get("biomes"), "biomes", ResourceLocation::new, ResourceLocation[]::new);
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