package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.firemerald.custombgm.providers.conditions.DimensionTypeCondition;
import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class DimensionConditionBuilder extends ProviderConditionBuilder
{
	private final List<ResourceLocation> dimensions = new ArrayList<>();
	
	public DimensionConditionBuilder addDimensions(ResourceLocation... dimensions)
	{
		Arrays.stream(dimensions).forEach(this.dimensions::add);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public DimensionConditionBuilder addDimensions(ResourceKey<Level>... dimensions)
	{
		Arrays.stream(dimensions).map(ResourceKey::location).forEach(this.dimensions::add);
		return this;
	}
	
	public DimensionConditionBuilder addDimensions(Level... dimensions)
	{
		Arrays.stream(dimensions).map(Level::dimension).map(ResourceKey::location).forEach(this.dimensions::add);
		return this;
	}
	
	public DimensionConditionBuilder addDimension(ResourceLocation dimension)
	{
		dimensions.add(dimension);
		return this;
	}
	
	public DimensionConditionBuilder addDimension(ResourceKey<Level> dimension)
	{
		return addDimension(dimension.location());
	}
	
	public DimensionConditionBuilder addDimension(Level dimension)
	{
		return addDimension(dimension.dimension());
	}

	@Override
	public ResourceLocation getID()
	{
		return DimensionTypeCondition.SERIALIZER_ID;
	}

	@Override
	public void compile(JsonObject obj)
	{
		obj.add("dimensions", dimensions.isEmpty() ? new JsonArray() : GsonUtil.toArrayOrPrimitive(dimensions));
	}
}