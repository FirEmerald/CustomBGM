package firemerald.custombgm.api;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;

/**
 * Represents a playable sound loop instance
 *
 * @author FirEmerald
 *
 */
public interface ISoundLoop
{
	/**
	 * Start playing the sound. Should only be called once - to resume playing a paused sound, use {@link #resumeSound()}
	 */
	public void playSound();

	/**
	 * Stop playing a sound. Should only be called when you will no longer use the sound - to pause a sound, use {@link #pauseSound()}
	 */
	public void stopSound();

	/**
	 * Gets if the sound has been stopped.
	 *
	 * @return if the sound has stopped.
	 */
	public boolean isStopped();

	/**
	 * Pauses a sound. To stop a sound if it is no longer needed, use {@link #stopSound()}
	 */
	public void pauseSound();

	/**
	 * Resumes a paused sound. To play a sound initially, use {@link #playSound()}
	 */
	public void resumeSound();

	/**
	 * Sets the sound's volume.
	 *
	 * @param volume the volume
	 */
	public void setVolume(float volume);

	/**
	 * Gets the sound's volume
	 *
	 * @return the volume
	 */
	public float getVolume();

	/**
	 * Updates the playing sound's actual volume based on the sound category volumes. Internal use only.
	 */
	public void updateCategoryVolume();

	/**
	 * Gets the sound's category.
	 *
	 * @return the category
	 */
	public SoundCategory getCategory();

	/**
	 * Sets the sound's pitch
	 *
	 * @param pitch the pitch
	 */
	public void setPitch(float pitch);

	/**
	 * Helper method to get the volume for a sound category
	 *
	 * @param category the category
	 * @return the volume
	 */
	public static float getCategoryVolume(SoundCategory category)
	{
		return category == SoundCategory.MASTER ? 1 : Minecraft.getMinecraft().gameSettings.getSoundLevel(category);
	}
}