package com.firemerald.custombgm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.firemerald.custombgm.client.ClientState;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
	@Inject(method = "getSituationalMusic", at = @At("RETURN"), cancellable = true)
	public void getSituationalMusic(CallbackInfoReturnable<Music> ci)
	{
		ci.setReturnValue(ClientState.getCustomMusic(ci.getReturnValue(), (Minecraft) (Object) this));
		//ci.cancel();
	}
}