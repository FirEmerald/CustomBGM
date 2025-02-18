package com.firemerald.custombgm.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.firemerald.custombgm.client.audio.ISoundExtensions;
import com.firemerald.custombgm.client.audio.LoopedPreloadedAudioStream;
import com.firemerald.custombgm.client.audio.LoopedStreamingAudioStream;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.OggAudioStream;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {
	@WrapOperation(
			method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/resources/sounds/Sound;shouldStream()Z"),
			require = 2)
	private boolean playWrapShouldStream(Sound instance, Operation<Boolean> original, @Local(index = 13) boolean isLooped) {
		return (isLooped && ((ISoundExtensions) instance).hasLoop()) || original.call(instance);
	}

	@WrapOperation(
			method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getStream(Lnet/minecraft/client/sounds/SoundBufferLibrary;Lnet/minecraft/client/resources/sounds/Sound;Z)Ljava/util/concurrent/CompletableFuture;",
					remap = false),
			require = 1)
	private CompletableFuture<AudioStream> playWrapGetStream(SoundInstance instance, SoundBufferLibrary soundBuffers, Sound sound, boolean looping, Operation<CompletableFuture<AudioStream>> original) {
		if (looping) {
			ISoundExtensions extensions = (ISoundExtensions) sound;
			if (extensions.hasLoop()) {
				if (sound.shouldStream()) {
			        return CompletableFuture.supplyAsync(() -> {
			            try {
			                InputStream inputstream = soundBuffers.resourceManager.open(sound.getPath());
			                return new LoopedStreamingAudioStream(OggAudioStream::new, inputstream, extensions.loopStart(), extensions.loopEnd());
			            } catch (IOException ioexception) {
			                throw new CompletionException(ioexception);
			            }
			        });
				} else {
					return soundBuffers
							.getCompleteBuffer(sound.getPath())
							.thenApply(buffer -> new LoopedPreloadedAudioStream(buffer.data, buffer.format, extensions.loopStart(), extensions.loopEnd()));
				}
			}
		}
		return original.call(instance, soundBuffers, sound, looping);
	}
}
