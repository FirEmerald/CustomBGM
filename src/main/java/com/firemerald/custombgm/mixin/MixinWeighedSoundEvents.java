package com.firemerald.custombgm.mixin;

import java.util.Collection;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.firemerald.custombgm.client.audio.IWeightedSoundExtensions;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;

@Mixin(WeighedSoundEvents.class)
public class MixinWeighedSoundEvents implements IWeightedSoundExtensions {
	@Shadow
	@Final
	public List<Weighted<Sound>> list;

	@Override
	public void getSounds(Collection<Sound> set) {
		list.forEach(weighted -> weighted.getSounds(set));
	}

	@Override
	public boolean containsSound(Sound sound) {
		return list.stream().anyMatch(weighted -> weighted.containsSound(sound));
	}
}
