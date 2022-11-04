package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.firemerald.custombgm.providers.conditions.CombatCondition;
import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class CombatConditionBuilder extends ProviderConditionBuilder
{
	private final List<ResourceLocation> tags = new ArrayList<>();
	private final List<ResourceLocation> entities = new ArrayList<>();
	private int minEntities = 1, maxEntities = Integer.MAX_VALUE;
	
	public CombatConditionBuilder addTags(ResourceLocation... tags)
	{
		Arrays.stream(tags).forEach(this.tags::add);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public CombatConditionBuilder addTags(TagKey<EntityType<?>>... tags)
	{
		Arrays.stream(tags).map(TagKey::location).forEach(this.tags::add);
		return this;
	}
	
	public CombatConditionBuilder addEntities(ResourceLocation... entities)
	{
		Arrays.stream(entities).forEach(this.entities::add);
		return this;
	}
	
	public CombatConditionBuilder addBiomes(EntityType<?>... entities)
	{
		Arrays.stream(entities).map(EntityType::getRegistryName).forEach(this.entities::add);
		return this;
	}
	
	public CombatConditionBuilder addTag(ResourceLocation tag)
	{
		tags.add(tag);
		return this;
	}
	
	public CombatConditionBuilder addTag(TagKey<EntityType<?>> tag)
	{
		return addTag(tag.location());
	}
	
	public CombatConditionBuilder addEntity(ResourceLocation entity)
	{
		entities.add(entity);
		return this;
	}
	
	public CombatConditionBuilder addEntity(EntityType<?> entity)
	{
		return addEntity(entity.getRegistryName());
	}
	
	public CombatConditionBuilder setMinEntities(int minEntities)
	{
		this.minEntities = minEntities;
		return this;
	}
	
	public CombatConditionBuilder setMaxEntities(int maxEntities)
	{
		this.maxEntities = maxEntities;
		return this;
	}

	@Override
	public ResourceLocation getID()
	{
		return CombatCondition.SERIALIZER_ID;
	}

	@Override
	public void compile(JsonObject obj)
	{
		if (!entities.isEmpty()) obj.add("entities", GsonUtil.toArrayOrPrimitive(entities));
		if (!tags.isEmpty()) obj.add("tags", GsonUtil.toArrayOrPrimitive(tags));
		if (minEntities != 1) obj.addProperty("minEntities", minEntities);
		if (maxEntities != Integer.MAX_VALUE) obj.addProperty("maxEntities", maxEntities);
	}
}