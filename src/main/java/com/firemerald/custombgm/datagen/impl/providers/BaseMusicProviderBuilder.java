package com.firemerald.custombgm.datagen.impl.providers;

import com.firemerald.custombgm.providers.BaseMusicProvider;
import com.firemerald.fecore.util.distribution.EmptyDistribution;
import com.firemerald.fecore.util.distribution.IDistribution;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public class BaseMusicProviderBuilder<T extends BaseMusicProviderBuilder<T>> extends MusicProviderBuilder<T>
{
	private IDistribution<ResourceLocation> music = EmptyDistribution.get();
	
	@SuppressWarnings("unchecked")
	public T setMusic(IDistribution<ResourceLocation> music)
	{
		this.music = music;
		return (T) this;
	}

	@Override
	public void compileMusic(JsonObject obj)
	{
		obj.add("music", music.toJson(ResourceLocation::toString));
	}

	@Override
	public ResourceLocation getID()
	{
		return BaseMusicProvider.SERIALIZER_ID;
	}
}