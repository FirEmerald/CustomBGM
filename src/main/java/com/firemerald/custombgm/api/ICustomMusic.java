package com.firemerald.custombgm.api;

import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.distribution.IDistribution;

import net.minecraft.resources.ResourceLocation;

/**
 * Interface to be implemented on GUI screens that changes the background music to a custom loop.
 *
 * @author FirEmerald
 *
 */
public interface ICustomMusic
{
	/**
	 * Get the music to play
	 *
	 * @param player the player entity
	 * @param currentMusic the currently playing BGM. null if none or vanilla.
	 * @return the music to play
	 */
	public ResourceLocation getMusic(@Nullable PlayerConditionData player, @Nullable ResourceLocation currentMusic);

	/**
	 * Helper method to pick a music from a random list, or return the current music if it is already in the list.
	 *
	 * @param rand an instance of Random
	 * @param currentMusic the currently playing BGM. null if none or vanilla.
	 * @param options the list of music to pick from
	 * @return a randomly chosen music, or the currently playing one if it was in the list
	 */
	public default ResourceLocation pickOne(Random rand, @Nullable ResourceLocation currentMusic, ResourceLocation... options)
	{
		if (options.length == 0) return null;
		for (ResourceLocation option : options) if (Objects.equals(option, currentMusic)) return option;
		return options[rand.nextInt(options.length)];
	}

	/**
	 * Helper method to pick a music from a distribution, or return the current music if it is already in the list.
	 *
	 * @param rand an instance of Random
	 * @param currentMusic the currently playing BGM. null if none or vanilla.
	 * @param options the list of music to pick from
	 * @return a randomly chosen music, or the currently playing one if it was in the list
	 */
	public default ResourceLocation pickOne(Random rand, @Nullable ResourceLocation currentMusic, IDistribution<ResourceLocation> options)
	{
		if (options.isEmpty()) return null;
		else if (currentMusic != null && options.contains(currentMusic)) return currentMusic;
		return options.getRandom(rand);
	}
}