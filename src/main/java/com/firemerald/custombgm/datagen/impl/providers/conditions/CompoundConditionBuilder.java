package com.firemerald.custombgm.datagen.impl.providers.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonObject;

public abstract class CompoundConditionBuilder<T extends CompoundConditionBuilder<T>> extends ProviderConditionBuilder
{
	private final List<ProviderConditionBuilder> conditions = new ArrayList<>();
	
	public CompoundConditionBuilder() {}
	
	public CompoundConditionBuilder(ProviderConditionBuilder... conditions)
	{
		Arrays.stream(conditions).forEach(this.conditions::add);
	}
	
	public CompoundConditionBuilder(Collection<ProviderConditionBuilder> conditions)
	{
		this.conditions.addAll(conditions);
	}
	
	@SuppressWarnings("unchecked")
	public T addConditions(ProviderConditionBuilder... conditions)
	{
		Arrays.stream(conditions).forEach(this.conditions::add);
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T addConditions(Collection<ProviderConditionBuilder> conditions)
	{
		this.conditions.addAll(conditions);
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T addCondition(ProviderConditionBuilder condition)
	{
		this.conditions.add(condition);
		return (T) this;
	}

	@Override
	public void compile(JsonObject obj)
	{
		obj.add("conditions", GsonUtil.toArrayOrPrimitive(conditions, ProviderConditionBuilder::compile));
	}
}