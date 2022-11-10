package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.Collection;

import com.firemerald.custombgm.providers.conditions.NandCondition;

import net.minecraft.resources.ResourceLocation;

public class NandConditionBuilder extends CompoundConditionBuilder<NandConditionBuilder>
{
	public NandConditionBuilder() {}

	public NandConditionBuilder(ProviderConditionBuilder... conditions)
	{
		super(conditions);
	}

	public NandConditionBuilder(Collection<ProviderConditionBuilder> conditions)
	{
		super(conditions);
	}

	@Override
	public ResourceLocation getID()
	{
		return NandCondition.SERIALIZER_ID;
	}
}