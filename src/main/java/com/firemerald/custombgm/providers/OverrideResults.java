package com.firemerald.custombgm.providers;

import java.util.ArrayList;
import java.util.List;

import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.fecore.codec.stream.StreamCodec;
import com.firemerald.fecore.util.CollectionUtils;

public record OverrideResults(List<BgmDistribution> overrides, int priority) implements IOverrideResults {
	public static final StreamCodec<OverrideResults> STREAM_CODEC = StreamCodec.composite(
			BgmDistribution.STREAM_CODEC.asList(), OverrideResults::overrides,
			StreamCodec.INT, OverrideResults::priority,
			OverrideResults::new
			);

	public OverrideResults() {
		this(new ArrayList<>(), Integer.MIN_VALUE);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		else if (o == this) return true;
		else if (o.getClass() != this.getClass()) return false;
		else {
			OverrideResults other = (OverrideResults) o;
			return other.priority == priority && CollectionUtils.equalUnordered(other.overrides, overrides);
		}
	}
}
