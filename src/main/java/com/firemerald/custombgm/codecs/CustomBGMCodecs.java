package com.firemerald.custombgm.codecs;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.fecore.codec.DistributionStreamCodec;
import com.firemerald.fecore.distribution.IDistribution;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class CustomBGMCodecs {
	public static final StreamCodec<ByteBuf, IDistribution<BGM>> BGM_DISTRIBUTION_CODEC = new DistributionStreamCodec<>(BGM.STREAM_CODEC);
}
