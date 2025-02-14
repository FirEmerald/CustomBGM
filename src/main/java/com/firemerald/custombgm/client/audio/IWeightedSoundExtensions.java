package com.firemerald.custombgm.client.audio;

import java.util.Collection;

import com.firemerald.custombgm.CustomBGM;

import net.minecraft.client.resources.sounds.Sound;

public interface IWeightedSoundExtensions {
	public default void getSounds(Collection<Sound> set) {
		CustomBGM.LOGGER.error("Failed to grab sounds from unknown Weighted instance " + this.toString() + " of class " + this.getClass().toString());
	}

	public default boolean containsSound(Sound sound) {
		CustomBGM.LOGGER.error("Failed to check if sound is contained in unknown Weighted instance " + this.toString() + " of class " + this.getClass().toString());
		return false;
	}
}
