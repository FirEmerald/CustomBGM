package com.firemerald.custombgm.providers;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.distribution.IDistribution;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class BaseMusicProvider extends BGMProvider
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "base");

	public static BaseMusicProvider serialize(JsonObject json, int priority, Predicate<PlayerConditionData> condition, ICondition.IContext conditionContext)
	{
		return new BaseMusicProvider(priority, condition, IDistribution.get(json, "music", ResourceLocation::new, ResourceLocation[]::new));
	}

	public final IDistribution<ResourceLocation> tracks;

	public BaseMusicProvider(int priority, Predicate<PlayerConditionData> condition, IDistribution<ResourceLocation> tracks)
	{
		super(priority, condition);
		this.tracks = tracks;
	}

	@Override
	public ResourceLocation getTheMusic(PlayerConditionData player, @Nullable ResourceLocation currentMusic)
	{
		return pickOne(player.player.getRandom(), currentMusic, tracks);
	}
}