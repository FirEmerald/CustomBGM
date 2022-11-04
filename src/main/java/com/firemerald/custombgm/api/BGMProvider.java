package com.firemerald.custombgm.api;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class BGMProvider implements Comparable<BGMProvider>, ICustomMusic
{
	public final int priority;
	public final Predicate<Player> condition;

	public BGMProvider(int priority, Predicate<Player> condition)
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
	public ResourceLocation getMusic(Player player, @Nullable ResourceLocation currentMusic)
	{
		return condition.test(player) ? getTheMusic(player, currentMusic) : null;
	}
	
	public abstract ResourceLocation getTheMusic(Player player, @Nullable ResourceLocation currentMusic);
}