package com.firemerald.custombgm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.firemerald.custombgm.client.audio.LoopingSounds;

import net.minecraft.client.sounds.SoundEngine;

@Mixin(SoundEngine.class)
public class MixinSoundEngine
{
	@Inject(method = "stopAll", at = @At("HEAD"))
	public void stopAll(CallbackInfo ci)
	{
		LoopingSounds.stopAll();
	}

	@Inject(method = "pause", at = @At("HEAD"))
	public void pause(CallbackInfo ci)
	{
		LoopingSounds.pauseAll();
	}

	@Inject(method = "resume", at = @At("HEAD"))
	public void resume(CallbackInfo ci)
	{
		LoopingSounds.resumeAll();
	}
}