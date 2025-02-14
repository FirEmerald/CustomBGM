package com.firemerald.custombgm.api.providers.volume;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record ConstantVolume(float volume) implements BGMProviderVolume {
	public static final ConstantVolume DEFAULT = new ConstantVolume(1);

	public static final MapCodec<ConstantVolume> CODEC = Codec.FLOAT.fieldOf("volume").xmap(ConstantVolume::new, ConstantVolume::volume);

	@Override
	public MapCodec<ConstantVolume> codec() {
		return CODEC;
	}

	@Override
	public float getVolume(PlayerConditionData playerData) {
		return volume;
	}
}