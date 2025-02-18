package com.firemerald.custombgm.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;

import com.firemerald.custombgm.client.audio.ISoundExtensions;
import com.firemerald.custombgm.client.audio.IWeightedSoundExtensions;

import net.minecraft.client.resources.sounds.Sound;

@Mixin(Sound.class)
public class MixinSound implements ISoundExtensions, IWeightedSoundExtensions {
	private int loopStart = -1, loopEnd = -1;

	@Override
	public void setLoop(int start, int end) {
		loopStart = start;
		loopEnd = end;
	}

	@Override
	public int loopStart() {
		return loopStart;
	}

	@Override
	public int loopEnd() {
		return loopEnd;
	}

	@Override
	public boolean hasLoop() {
		return loopStart > 0 || loopEnd > 0;
	}

	@Override
	public void getSounds(Collection<Sound> set) {
		set.add((Sound) (Object) this);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean containsSound(Sound sound) {
		return sound.equals(this);
	}
}
