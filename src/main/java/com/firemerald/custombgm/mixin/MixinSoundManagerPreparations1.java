package com.firemerald.custombgm.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.firemerald.custombgm.client.audio.IWeightedSoundExtensions;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;

@Mixin(targets = "net.minecraft.client.sounds.SoundManager$Preparations$1")
public class MixinSoundManagerPreparations1 implements IWeightedSoundExtensions {
	@Shadow(aliases = {"val$resourcelocation", "val$soundLocation"})
	private ResourceLocation resourcelocation;

	@Shadow(aliases = {"this$0", "field_5597"})
	private SoundManager.Preparations preparations;

	@Override
	public void getSounds(Collection<Sound> set) {
		WeighedSoundEvents wrapped = preparations.registry.get(resourcelocation);
		if (wrapped != null) wrapped.getSounds(set);
	}

	@Override
	public boolean containsSound(Sound sound) {
		WeighedSoundEvents wrapped = preparations.registry.get(resourcelocation);
		if (wrapped != null) return wrapped.containsSound(sound);
		else return false;
	}
}
