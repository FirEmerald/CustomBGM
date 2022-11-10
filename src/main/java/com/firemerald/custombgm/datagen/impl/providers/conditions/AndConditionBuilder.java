package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.Collection;

import com.firemerald.custombgm.providers.conditions.AndCondition;

import net.minecraft.resources.ResourceLocation;

public class AndConditionBuilder extends CompoundConditionBuilder<AndConditionBuilder>
{
	public AndConditionBuilder() {}

	public AndConditionBuilder(ProviderConditionBuilder... conditions)
	{
		super(conditions);
	}

	public AndConditionBuilder(Collection<ProviderConditionBuilder> conditions)
	{
		super(conditions);
	}

	@Override
	public ResourceLocation getID()
	{
		return AndCondition.SERIALIZER_ID;
	}
}