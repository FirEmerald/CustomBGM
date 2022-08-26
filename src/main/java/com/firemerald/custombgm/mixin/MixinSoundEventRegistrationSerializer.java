package com.firemerald.custombgm.mixin;

import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.firemerald.custombgm.client.audio.IExtendedSound;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.util.GsonHelper;

@Mixin(SoundEventRegistrationSerializer.class)
public class MixinSoundEventRegistrationSerializer
{
	@Inject(method = "getSound", at = @At("RETURN"))
	public void getSound(JsonObject obj, CallbackInfoReturnable<Sound> callback)
	{
		Sound sound = callback.getReturnValue();
		if (sound != null)
		{
			int start;
			if (obj.has("loopStart"))
			{
				start = GsonHelper.getAsInt(obj, "loopStart", 0);
				Validate.isTrue(start >= 0, "Invalid loop start");
				((IExtendedSound) sound).setLoopStart(start);
			}
			else start = 0;
			if (obj.has("loopEnd"))
			{
				int end = GsonHelper.getAsInt(obj, "loopEnd", 0);
				Validate.isTrue(end == 0 || end > start, "Invalid loop end");
				((IExtendedSound) sound).setLoopEnd(end);
			}
		}
	}
}
