package com.firemerald.custombgm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.firemerald.custombgm.client.audio.ISoundExtensions;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.util.GsonHelper;

@Mixin(SoundEventRegistrationSerializer.class)
public class MixinSoundEventRegistrationSerializer {
	@Inject(method = "getSound(Lcom/google/gson/JsonObject;)Lnet/minecraft/client/resources/sounds/Sound;", at = @At("RETURN"))
    private void getSound(JsonObject object, CallbackInfoReturnable<Sound> cir) {
        int loopStart = GsonHelper.getAsInt(object, "loopStart", -1);
        int loopEnd = GsonHelper.getAsInt(object, "loopEnd", -1);
        if (loopStart > loopEnd && loopEnd >= 0) loopEnd = loopStart;
        ((ISoundExtensions) cir.getReturnValue()).setLoop(loopStart, loopEnd);
	}
}
