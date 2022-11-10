package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.Collection;

import com.firemerald.custombgm.providers.conditions.OrCondition;

import net.minecraft.resources.ResourceLocation;

public class OrConditionBuilder extends CompoundConditionBuilder<OrConditionBuilder>
{
	public OrConditionBuilder() {}

	public OrConditionBuilder(ProviderConditionBuilder... conditions)
	{
		super(conditions);
	}

	public OrConditionBuilder(Collection<ProviderConditionBuilder> conditions)
	{
		super(conditions);
	}

	@Override
	public ResourceLocation getID()
	{
		return OrCondition.SERIALIZER_ID;
	}
}