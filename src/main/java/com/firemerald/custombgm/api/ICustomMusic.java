package com.firemerald.custombgm.api;

import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Interface to be implemented on custom biomes or GUI screens that changes the background music to a custom loop.
 *
 * @author FirEmerald
 *
 */
public interface ICustomMusic
{
	/**
	 * Get the music to play
	 *
	 * @param currentMusic the currently playing BGM. null if none or vanilla.
	 * @return the music to play
	 */
	public ResourceLocation getMusic(Player player, @Nullable ResourceLocation currentMusic);

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
}