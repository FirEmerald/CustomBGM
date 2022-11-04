package com.firemerald.custombgm.datagen.impl.providers.conditions;

import com.firemerald.custombgm.datagen.impl.BuilderBase;

public abstract class ProviderConditionBuilder extends BuilderBase
{
	public NotConditionBuilder not()
	{
		return new NotConditionBuilder(this);
	}
}