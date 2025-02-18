package com.firemerald.custombgm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.firemerald.custombgm.client.BGMEngine;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
	@Inject(method = "getSituationalMusic()Lnet/minecraft/sounds/Music;", at = @At("RETURN"), cancellable = true)
	public void getSituationalMusic(CallbackInfoReturnable<Music> ci)
	{
		if (BGMEngine.musicTick(ci.getReturnValue(), (Minecraft) (Object) this)) ci.setReturnValue(BGMEngine.EMPTY);
	}
}