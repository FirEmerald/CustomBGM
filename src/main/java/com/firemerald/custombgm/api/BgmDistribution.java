package com.firemerald.custombgm.api;

import com.firemerald.custombgm.codecs.CustomBGMCodecs;
import com.firemerald.fecore.codec.stream.StreamCodec;
import com.firemerald.fecore.distribution.EmptyDistribution;
import com.firemerald.fecore.distribution.IDistribution;

public record BgmDistribution(IDistribution<BGM> distribution, float volume) {
	public static final StreamCodec<BgmDistribution> STREAM_CODEC = StreamCodec.composite(
			CustomBGMCodecs.BGM_DISTRIBUTION_CODEC, BgmDistribution::distribution,
			StreamCodec.FLOAT, BgmDistribution::volume,
			BgmDistribution::new
			);

	public static final BgmDistribution EMPTY = new BgmDistribution(EmptyDistribution.get(), 1f);

}
