package com.firemerald.custombgm.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.firemerald.custombgm.client.audio.IWeightedSoundExtensions;

import net.minecraft.client.sounds.Weighted;

@Mixin(Weighted.class)
public interface MixinWeighted extends IWeightedSoundExtensions {}
