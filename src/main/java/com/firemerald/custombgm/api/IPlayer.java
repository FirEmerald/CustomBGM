package com.firemerald.custombgm.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * this is the capability used to facilitate handling of the BGM as set by the SERVER
 *
 * @author FirEmerald
 *
 */
public interface IPlayer extends ICapabilityProvider
{
	/**
	 * The capability name when being attached to the player
	 */
	public static final ResourceLocation CAPABILITY_NAME = new ResourceLocation(CustomBGMAPI.MOD_ID, "player");

	/**
	 * Adds a music override to the player. default priority from normal BGM handling is 0, so a negative priority will be overridden by custom BGM, while a positive one will override custom BGM.
	 *
	 * @param music the music to play. may be null to disable.
	 * @param priority the override priority. the music will override any other overrides with a lower priority.
	 */
	public void addMusicOverride(ResourceLocation music, int priority);

	/**
	 * Clears the music override. This is called at the beginning of each tick. You should not call it yourself.
	 */
	public void clearMusicOverride();

	/**
	 * Gets the current music override.
	 *
	 * @return the music
	 */
	public ResourceLocation getMusicOverride();

	/**
	 * Gets the current synchronized music override.
	 *
	 * @return the music
	 */
	public ResourceLocation getLastMusicOverride();

	/**
	 * Sets the current synchronized music override.
	 *
	 * @return the music
	 */
	public void setLastMusicOverride(ResourceLocation music);
	
	public boolean getInit();
	
	public void setInit(boolean isInit);
}