package com.firemerald.custombgm.providers.conditions;

import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class DimensionCondition implements Predicate<PlayerConditionData>
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "dimension");

	public static DimensionCondition serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		if (!json.has("dimensions")) throw new JsonSyntaxException("Missing \"dimensions\", expected to find a string or array of strings");
		ResourceLocation[] dimensions = GsonUtil.arrayFromArrayOrSingle(json.get("dimensions"), "dimensions", ResourceLocation::new, ResourceLocation[]::new);
		return new DimensionCondition(dimensions);
	}

	public final ResourceLocation[] dimensions;

	public DimensionCondition(ResourceLocation[] dimensions)
	{
		this.dimensions = dimensions;
	}

	@Override
	public boolean test(PlayerConditionData player)
	{
		Level dimension = player.player.level;
		for (ResourceLocation dimension2 : dimensions)
			if (dimension.dimension().getRegistryName().equals(dimension2)) return true;
		return false;
	}
}