package com.firemerald.custombgm.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.firemerald.custombgm.client.audio.IWeightedSoundExtensions;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;

@Mixin(targets = "net.minecraft.client.sounds.SoundManager$Preparations$1")
public class MixinSoundManagerPreparations1 implements IWeightedSoundExtensions {
	//a bug in ObjectWeb ASM prevents these fields from being shadowed, so we duplicate them and set them using a constructor injector
	private SoundManager.Preparations preparations;
	private ResourceLocation resourcelocation;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructorHead(SoundManager.Preparations preparations, ResourceLocation resourcelocation, Sound sound, CallbackInfo ci) {
    	this.preparations = preparations;
    	this.resourcelocation = resourcelocation;
    }


	@Override
	public void getSounds(Collection<Sound> set) {
		WeighedSoundEvents wrapped = preparations.registry.get(resourcelocation);
		if (wrapped != null) ((IWeightedSoundExtensions) wrapped).getSounds(set);
	}

	@Override
	public boolean containsSound(Sound sound) {
		WeighedSoundEvents wrapped = preparations.registry.get(resourcelocation);
		if (wrapped != null) return ((IWeightedSoundExtensions) wrapped).containsSound(sound);
		else return false;
	}
}
