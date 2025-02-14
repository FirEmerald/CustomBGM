package com.firemerald.custombgm.providers.conditions.player;

import java.util.Optional;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public record MobEffectCondition(Holder<MobEffect> effect, MinMaxBounds.Ints amplifier, MinMaxBounds.Ints duration, Optional<Boolean> ambient, Optional<Boolean> visible) implements BGMProviderPlayerCondition {
	public static final MapCodec<MobEffectCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectCondition::effect),
				MinMaxBounds.Ints.CODEC.optionalFieldOf("amplifier", MinMaxBounds.Ints.ANY).forGetter(MobEffectCondition::amplifier),
				MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(MobEffectCondition::duration),
				Codec.BOOL.optionalFieldOf("ambient").forGetter(MobEffectCondition::ambient),
				Codec.BOOL.optionalFieldOf("visible").forGetter(MobEffectCondition::visible)
				).apply(instance, MobEffectCondition::new)
			);

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		MobEffectInstance effectInstance = player.getActiveEffectsMap().get(effect);
		if (effectInstance == null) return false;
		else if (!this.amplifier.matches(effectInstance.getAmplifier())) return false;
        else if (!this.duration.matches(effectInstance.getDuration())) return false;
        else if (this.ambient.isPresent() && this.ambient.get() != effectInstance.isAmbient()) return false;
        else if (this.visible.isPresent() && this.visible.get() != effectInstance.isVisible()) return false;
        else return true;
	}

	@Override
	public MapCodec<MobEffectCondition> codec() {
		return CODEC;
	}
}
