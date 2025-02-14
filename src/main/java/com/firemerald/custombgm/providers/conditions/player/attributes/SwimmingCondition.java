package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.entity.player.Player;

public class SwimmingCondition  implements BGMProviderPlayerCondition {
	public static final MapCodec<SwimmingCondition> CODEC = Codec.BOOL.optionalFieldOf("swimming", true).xmap(SwimmingCondition::of, condition -> condition.swimming);

	public static final SwimmingCondition TRUE = new SwimmingCondition(true), FALSE = new SwimmingCondition(false);

	public static SwimmingCondition of(boolean swimming) {
		return swimming ? TRUE : FALSE;
	}

	public final boolean swimming;

	private SwimmingCondition(boolean swimming) {
		this.swimming = swimming;
	}

	@Override
	public MapCodec<SwimmingCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return player.isSwimming() == swimming;
	}

	@Override
	public SwimmingCondition simpleNot() {
		return of(!swimming);
	}

}
