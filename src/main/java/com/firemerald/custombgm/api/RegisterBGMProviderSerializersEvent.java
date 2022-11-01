package com.firemerald.custombgm.api;

import java.util.function.BiPredicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class RegisterBGMProviderSerializersEvent extends Event
{
	private final BiPredicate<ResourceLocation, BGMProviderSerializer> register;
	
	public RegisterBGMProviderSerializersEvent(BiPredicate<ResourceLocation, BGMProviderSerializer> register)
	{
		this.register = register;
	}
	
	public boolean register(ResourceLocation id, BGMProviderSerializer serializer)
	{
		return register.test(id, serializer);
	}
}