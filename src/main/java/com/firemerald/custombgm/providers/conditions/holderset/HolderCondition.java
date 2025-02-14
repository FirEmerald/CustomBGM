package com.firemerald.custombgm.providers.conditions.holderset;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public abstract class HolderCondition<T> extends HolderSetCondition<T> {

	public HolderCondition(HolderSet<T> holderSet) {
		super(holderSet);
	}

	public abstract Holder<T> getHolder(PlayerConditionData playerData);

	@Override
	public boolean test(PlayerConditionData playerData) {
		Holder<T> holder = getHolder(playerData);
		if (holder == null) return false;
		else return holderSet.contains(holder);
	}
}