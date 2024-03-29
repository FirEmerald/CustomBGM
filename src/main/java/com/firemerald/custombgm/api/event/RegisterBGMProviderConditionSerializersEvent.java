package com.firemerald.custombgm.api.event;

import java.util.function.BiPredicate;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderConditionSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class RegisterBGMProviderConditionSerializersEvent extends Event
{
	private final BiPredicate<ResourceLocation, BGMProviderConditionSerializer> register;

	public RegisterBGMProviderConditionSerializersEvent(BiPredicate<ResourceLocation, BGMProviderConditionSerializer> register)
	{
		this.register = register;
	}

	public boolean register(ResourceLocation id, BGMProviderConditionSerializer serializer)
	{
		return register.test(id, serializer);
	}
}