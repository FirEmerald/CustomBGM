package com.firemerald.custombgm.api.capabilities;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

/**
 * this is the capability used to facilitate handling of the BGM as set by the SERVER
 *
 * @author FirEmerald
 *
 */
public interface IPlayer extends ICapabilityProvider
{
	public static final ResourceLocation NAME = new ResourceLocation(CustomBGMAPI.MOD_ID, "player");
	public static final Capability<IPlayer> CAP = CapabilityManager.get(new CapabilityToken<>(){});

	public static LazyOptional<IPlayer> get(ICapabilityProvider obj)
	{
		return obj.getCapability(CAP);
	}

	public static LazyOptional<IPlayer> get(ICapabilityProvider obj, @Nullable Direction side)
	{
		return obj.getCapability(CAP, side);
	}

	public static IPlayer getOrNull(ICapabilityProvider obj)
	{
		return get(obj).resolve().orElse(null);
	}

	public static IPlayer getOrNull(ICapabilityProvider obj, @Nullable Direction side)
	{
		return get(obj, side).resolve().orElse(null);
	}

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