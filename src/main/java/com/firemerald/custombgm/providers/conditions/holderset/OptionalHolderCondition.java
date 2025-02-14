package com.firemerald.custombgm.providers.conditions.holderset;

import java.util.Optional;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public abstract class OptionalHolderCondition<T> extends OptionalHolderSetCondition<T> {

	public OptionalHolderCondition(Optional<HolderSet<T>> holderSet) {
		super(holderSet);
	}

	public abstract Holder<T> getHolder(PlayerConditionData playerData);

	@Override
	public boolean test(PlayerConditionData playerData) {
		if (holderSet.isPresent()) {
			Holder<T> holder = getHolder(playerData);
			if (holder == null) return false;
			else if (!holderSet.get().contains(holder)) return false;
		}
		return true;
	}
}