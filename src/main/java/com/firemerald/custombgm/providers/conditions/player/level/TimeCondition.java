package com.firemerald.custombgm.providers.conditions.player.level;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.bounds.LongBounds;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.player.Player;

public record TimeCondition(LongBounds time, boolean ofDay, boolean ofGame) implements BGMProviderPlayerCondition {
	public static final MapCodec<TimeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			LongBounds.CODEC.fieldOf("time").forGetter(TimeCondition::time),
			Codec.BOOL.optionalFieldOf("ofDay", true).forGetter(TimeCondition::ofDay),
			Codec.BOOL.optionalFieldOf("ofGame", false).forGetter(TimeCondition::ofGame)
			)
			.apply(instance, TimeCondition::new)
	);
	
	public TimeCondition(LongBounds time) {
		this(time, true, false);
	}

	@Override
	public MapCodec<TimeCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		player.level().getDayTimeFraction();
		long time = ofGame ? player.level().getGameTime() : player.level().getDayTime();
		if (ofDay) time %= 24000L;
		return this.time.matches(time);
	}
}
