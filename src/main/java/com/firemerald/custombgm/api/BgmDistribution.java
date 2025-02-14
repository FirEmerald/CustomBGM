package com.firemerald.custombgm.api;

import com.firemerald.custombgm.codecs.CustomBGMCodecs;
import com.firemerald.fecore.distribution.EmptyDistribution;
import com.firemerald.fecore.distribution.IDistribution;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BgmDistribution(IDistribution<BGM> distribution, float volume) {
	public static final StreamCodec<ByteBuf, BgmDistribution> STREAM_CODEC = StreamCodec.composite(
			CustomBGMCodecs.BGM_DISTRIBUTION_CODEC, BgmDistribution::distribution,
			ByteBufCodecs.FLOAT, BgmDistribution::volume,
			BgmDistribution::new
			);

	public static final BgmDistribution EMPTY = new BgmDistribution(EmptyDistribution.get(), 1f);

}
