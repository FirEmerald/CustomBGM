package com.firemerald.custombgm.datagen.impl.providers;

import java.util.ArrayList;
import java.util.List;

import com.firemerald.custombgm.datagen.impl.BuilderBase;
import com.firemerald.custombgm.datagen.impl.providers.conditions.ProviderConditionBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class MusicProviderBuilder<T extends MusicProviderBuilder<T>> extends BuilderBase
{
	//TODO add loading conditions - currently no datagen is provided so we can't do it
	private final List<JsonObject> loadingConditions = new ArrayList<>();
	private ProviderConditionBuilder condition;
	private int priority;
	
	@SuppressWarnings("unchecked")
	public T setCondition(ProviderConditionBuilder condition)
	{
		this.condition = condition;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setPriority(int priority)
	{
		this.priority = priority;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T addLoadingCondition(JsonObject condition)
	{
		loadingConditions.add(condition);
		return (T) this;
	}
	
	@Override
	public void compile(JsonObject obj)
	{
		if (!loadingConditions.isEmpty())
		{
			JsonArray array = new JsonArray(loadingConditions.size());
			loadingConditions.forEach(array::add);
			obj.add("conditions", array);
		}
		if (condition != null) obj.add("condition", condition.compile());
		obj.addProperty("priority", priority);
		compileMusic(obj);
	}
	
	public abstract void compileMusic(JsonObject obj);
}