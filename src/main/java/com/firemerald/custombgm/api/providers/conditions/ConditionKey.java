package com.firemerald.custombgm.api.providers.conditions;

import net.minecraft.resources.ResourceLocation;

public abstract class ConditionKey<T> extends ResourceLocation
{
	/**
	 * @deprecated use {@link #ConditionKey(String, String)}
	 */
	@Deprecated
	public ConditionKey(String location)
	{
		super(location);
	}

	public ConditionKey(String nameSpace, String path)
	{
		super(nameSpace, path);
	}

	public abstract T compose(PlayerConditionData playerData);
}