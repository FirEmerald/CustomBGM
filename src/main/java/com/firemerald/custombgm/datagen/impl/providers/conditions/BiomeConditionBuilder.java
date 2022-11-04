package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.firemerald.custombgm.providers.conditions.BiomeCondition;
import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeConditionBuilder extends ProviderConditionBuilder
{
	private final List<ResourceLocation> tags = new ArrayList<>();
	private final List<ResourceLocation> biomes = new ArrayList<>();
	
	public BiomeConditionBuilder addTags(ResourceLocation... tags)
	{
		Arrays.stream(tags).forEach(this.tags::add);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public BiomeConditionBuilder addTags(TagKey<Biome>... tags)
	{
		Arrays.stream(tags).map(TagKey::location).forEach(this.tags::add);
		return this;
	}
	
	public BiomeConditionBuilder addBiomes(ResourceLocation... biomes)
	{
		Arrays.stream(biomes).forEach(this.biomes::add);
		return this;
	}
	
	public BiomeConditionBuilder addBiomes(Biome... biomes)
	{
		Arrays.stream(biomes).map(Biome::getRegistryName).forEach(this.biomes::add);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public BiomeConditionBuilder addBiomes(Holder<Biome>... biomes)
	{
		Arrays.stream(biomes).map(Holder::value).map(Biome::getRegistryName).forEach(this.biomes::add);
		return this;
	}
	
	public BiomeConditionBuilder addTag(ResourceLocation tag)
	{
		tags.add(tag);
		return this;
	}
	
	public BiomeConditionBuilder addTag(TagKey<Biome> tag)
	{
		return addTag(tag.location());
	}
	
	public BiomeConditionBuilder addBiome(ResourceLocation biome)
	{
		biomes.add(biome);
		return this;
	}
	
	public BiomeConditionBuilder addBiome(Biome biome)
	{
		return addBiome(biome.getRegistryName());
	}
	
	public BiomeConditionBuilder addBiome(Holder<Biome> biome)
	{
		return addBiome(biome.value().getRegistryName());
	}

	@Override
	public ResourceLocation getID()
	{
		return BiomeCondition.SERIALIZER_ID;
	}

	@Override
	public void compile(JsonObject obj)
	{
		if (tags.isEmpty()) obj.add("biomes", biomes.isEmpty() ? new JsonArray() : GsonUtil.toArrayOrPrimitive(biomes));
		else
		{
			if (!biomes.isEmpty()) obj.add("biomes", GsonUtil.toArrayOrPrimitive(biomes));
			obj.add("tags", GsonUtil.toArrayOrPrimitive(tags));
		}
	}
}