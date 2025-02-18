package com.firemerald.custombgm.api.providers.volume;

import java.util.function.Function;

import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public interface BGMProviderVolume {
    public static final Codec<BGMProviderVolume> CODEC = Codecs.byNameCodec(CustomBGMRegistries.volumeCodecs).dispatch(BGMProviderVolume::codec, MapCodec::codec);
    public static final Codec<BGMProviderVolume> ADAPTABLE_CODEC = Codec.either(Codec.FLOAT, CODEC).xmap(decode -> {
    	return decode.map(ConstantVolume::new, Function.identity());
    }, encode -> {
    	return encode instanceof ConstantVolume constant ? Either.left(constant.volume()) : Either.right(encode);
    });

    public float getVolume(PlayerConditionData playerData);

	public MapCodec<? extends BGMProviderVolume> codec();
}
