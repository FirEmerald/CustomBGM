package com.firemerald.custombgm.api.providers;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.firemerald.custombgm.api.ICustomMusic;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;

import net.minecraft.resources.ResourceLocation;

public abstract class BGMProvider implements Comparable<BGMProvider>, ICustomMusic
{
	public final int priority;
	public final Predicate<PlayerConditionData> condition;

	public BGMProvider(int priority, Predicate<PlayerConditionData> condition)
	{
		this.priority = priority;
		this.condition = condition;
	}

	@Override
	public final int compareTo(BGMProvider other)
	{
		return priority - other.priority;
	}

	@Override
	public ResourceLocation getMusic(@Nonnull PlayerConditionData player, @Nullable ResourceLocation currentMusic)
	{
		return condition.test(player) ? getTheMusic(player, currentMusic) : null;
	}

	public abstract ResourceLocation getTheMusic(PlayerConditionData player, @Nullable ResourceLocation currentMusic);
}