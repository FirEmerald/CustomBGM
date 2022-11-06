package com.firemerald.custombgm.providers.conditions;

import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class DimensionTypeCondition implements Predicate<PlayerConditionData>
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "dimension_type");

	@SuppressWarnings("unchecked")
	public static DimensionTypeCondition serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		if (!json.has("tags") && !json.has("dimensionTypes")) throw new JsonSyntaxException("Missing \"tags\" or \"dimensionTypes\", must have one or both, expected to find a string or array of strings");
		TagKey<DimensionType>[] tags;
		if (json.has("tags")) tags = GsonUtil.arrayFromArrayOrSingle(json.get("tags"), "tags", v -> TagKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(v)), TagKey[]::new);
		else tags = new TagKey[0];
		ResourceLocation[] types;
		if (json.has("dimensionTypes")) types = GsonUtil.arrayFromArrayOrSingle(json.get("dimensionTypes"), "dimensionTypes", ResourceLocation::new, ResourceLocation[]::new);
		else types = new ResourceLocation[0];
		return new DimensionTypeCondition(tags, types);
	}

	public final TagKey<DimensionType>[] tags;
	public final ResourceLocation[] types;

	public DimensionTypeCondition(TagKey<DimensionType>[] tags, ResourceLocation[] types)
	{
		this.tags = tags;
		this.types = types;
	}

	@Override
	public boolean test(PlayerConditionData player)
	{
		Holder<DimensionType> type = player.player.level.dimensionTypeRegistration();
		for (TagKey<DimensionType> tag : tags)
			if (type.containsTag(tag)) return true;
		for (ResourceLocation type2 : types)
			if (type.is(type2)) return true;
		return false;
	}
}