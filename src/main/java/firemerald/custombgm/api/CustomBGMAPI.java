package firemerald.custombgm.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

/**
 * API class to manage the playing of custom sounds outside of just BGM.
 *
 * @author FirEmerald
 *
 */
public abstract class CustomBGMAPI
{
	public static final String MOD_ID = "custombgm";

	/**
	 * API implementation instance. null if the mod is not installed, so use null checks if the mod is not strictly required.
	 */
	public static CustomBGMAPI instance;

	/**
	 * Grab a playable instance to play later
	 *
	 * @param name the sound name
	 * @param category the sound category
	 * @param disablePan disable audio panning (left/right positional variance)
	 * @return the sound instance
	 */
	public abstract ISoundLoop grabSound(ResourceLocation name, SoundCategory category, boolean disablePan);

	/**
	 * Play a sound and return the sound instance
	 *
	 * @param name the sound name
	 * @param category the sound category
	 * @param disablePan disable audio panning (left/right positional variance)
	 * @return the sound instance
	 */
	public abstract ISoundLoop playSound(ResourceLocation name, SoundCategory category, boolean disablePan);
}