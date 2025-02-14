package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public class SprintingCondition  implements BGMProviderPlayerCondition {
	public static final MapCodec<SprintingCondition> CODEC = Codec.BOOL.optionalFieldOf("sprinting", true).xmap(SprintingCondition::of, condition -> condition.sprinting);

	public static final SprintingCondition TRUE = new SprintingCondition(true), FALSE = new SprintingCondition(false);

	public static SprintingCondition of(boolean sprinting) {
		return sprinting ? TRUE : FALSE;
	}

	public final boolean sprinting;

	private SprintingCondition(boolean sprinting) {
		this.sprinting = sprinting;
	}

	@Override
	public MapCodec<SprintingCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return player.isSprinting() == sprinting;
	}

	@Override
	public SprintingCondition simpleNot() {
		return of(!sprinting);
	}

}
