package com.firemerald.custombgm.providers;

import java.util.List;

import com.firemerald.custombgm.api.BgmDistribution;

public interface IOverrideResults {
	public int priority();

	public List<BgmDistribution> overrides();
}
