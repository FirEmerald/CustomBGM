package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.firemerald.custombgm.providers.conditions.DimensionTypeCondition;
import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypeConditionBuilder extends ProviderConditionBuilder
{
	private final List<ResourceLocation> tags = new ArrayList<>();
	private final List<ResourceLocation> dimensionTypes = new ArrayList<>();

	public DimensionTypeConditionBuilder addTags(ResourceLocation... tags)
	{
		Arrays.stream(tags).forEach(this.tags::add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public DimensionTypeConditionBuilder addTags(TagKey<DimensionType>... tags)
	{
		Arrays.stream(tags).map(TagKey::location).forEach(this.tags::add);
		return this;
	}

	public DimensionTypeConditionBuilder addDimensionTypes(ResourceLocation... dimensionTypes)
	{
		Arrays.stream(dimensionTypes).forEach(this.dimensionTypes::add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public DimensionTypeConditionBuilder addDimensionTypes(ResourceKey<DimensionType>... dimensionTypes)
	{
		Arrays.stream(dimensionTypes).map(ResourceKey::location).forEach(this.dimensionTypes::add);
		return this;
	}

	public DimensionTypeConditionBuilder addDimensionTypes(DimensionType... dimensionTypes)
	{
		Arrays.stream(dimensionTypes).map(RegistryAccess.BUILTIN.get().registry(Registry.DIMENSION_TYPE_REGISTRY).get()::getKey).forEach(this.dimensionTypes::add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public DimensionTypeConditionBuilder addDimensionTypes(Holder<DimensionType>... dimensionTypes)
	{
		Arrays.stream(dimensionTypes).map(Holder::unwrapKey).map(Optional::get).map(ResourceKey::location).forEach(this.dimensionTypes::add);
		return this;
	}

	public DimensionTypeConditionBuilder addTag(ResourceLocation tag)
	{
		tags.add(tag);
		return this;
	}

	public DimensionTypeConditionBuilder addTag(TagKey<DimensionType> tag)
	{
		return addTag(tag.location());
	}

	public DimensionTypeConditionBuilder addDimensionType(ResourceLocation dimensionType)
	{
		dimensionTypes.add(dimensionType);
		return this;
	}

	public DimensionTypeConditionBuilder addDimensionType(DimensionType dimensionType)
	{
		return addDimensionType(RegistryAccess.BUILTIN.get().registry(Registry.DIMENSION_TYPE_REGISTRY).get().getKey(dimensionType));
	}

	public DimensionTypeConditionBuilder addDimensionType(ResourceKey<DimensionType> dimensionType)
	{
		return addDimensionType(dimensionType.location());
	}

	public DimensionTypeConditionBuilder addDimensionType(Holder<DimensionType> dimensionType)
	{
		return addDimensionType(dimensionType.unwrapKey().get());
	}

	@Override
	public ResourceLocation getID()
	{
		return DimensionTypeCondition.SERIALIZER_ID;
	}

	@Override
	public void compile(JsonObject obj)
	{
		if (tags.isEmpty()) obj.add("dimensionTypes", dimensionTypes.isEmpty() ? new JsonArray() : GsonUtil.toArrayOrPrimitive(dimensionTypes));
		else
		{
			if (!dimensionTypes.isEmpty()) obj.add("dimensionTypes", GsonUtil.toArrayOrPrimitive(dimensionTypes));
			obj.add("tags", GsonUtil.toArrayOrPrimitive(tags));
		}
	}
}