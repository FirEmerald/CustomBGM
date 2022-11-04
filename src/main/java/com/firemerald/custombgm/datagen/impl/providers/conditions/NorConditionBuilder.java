package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.Collection;

import com.firemerald.custombgm.providers.conditions.NorCondition;

import net.minecraft.resources.ResourceLocation;

public class NorConditionBuilder extends CompoundConditionBuilder<NorConditionBuilder>
{
	public NorConditionBuilder() {}
	
	public NorConditionBuilder(ProviderConditionBuilder... conditions)
	{
		super(conditions);
	}
	
	public NorConditionBuilder(Collection<ProviderConditionBuilder> conditions)
	{
		super(conditions);
	}
	
	@Override
	public ResourceLocation getID()
	{
		return NorCondition.SERIALIZER_ID;
	}
}