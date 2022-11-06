package com.firemerald.custombgm.datagen.impl.providers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.TriConsumer;

import com.firemerald.custombgm.CustomBGMMod;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public abstract class MusicProviderProviders implements DataProvider
{
	protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	protected final DataGenerator generator;
	
	public MusicProviderProviders(DataGenerator generator)
	{
		this.generator = generator;
	}

	@Override
	public void run(HashCache cache)
	{
		Path path = this.generator.getOutputFolder();
		Set<Pair<ResourceLocation, PackType>> set = Sets.newHashSet();
		buildProviders((id, type, generator) -> {
			if (!set.add(Pair.of(id, type))) throw new IllegalStateException("Duplicate provider " + id + " for " + type);
			else saveProvider(cache, generator.compile(), path.resolve(type.getDirectory() + "/" + id.getNamespace() + "/custom_bgm/" + id.getPath() + ".json"));
		});
	}
	
	public abstract void buildProviders(TriConsumer<ResourceLocation, PackType, MusicProviderBuilder<?>> register);
	
	private static void saveProvider(HashCache cache, JsonObject json, Path path)
	{
		try
		{
			String jsonString = GSON.toJson(json);
			String jsonHash = SHA1.hashUnencodedChars(jsonString).toString();
			if (!Objects.equals(cache.getHash(path), jsonHash) || !Files.exists(path))
			{
				Files.createDirectories(path.getParent());
				BufferedWriter writer = Files.newBufferedWriter(path);
				try
				{
					writer.write(jsonString);
				}
				catch (Throwable t)
				{
					if (writer != null)
					{
						try
						{
							writer.close();
						}
						catch (Throwable t2)
						{
							t.addSuppressed(t2);
						}
					}
					throw t;
				}
				if (writer != null) writer.close();
			}
			cache.putNew(path, jsonHash);
		}
		catch (IOException e)
		{
			CustomBGMMod.LOGGER.error("Couldn't save provider " + path, e);
		}
		
	}

	@Override
	public String getName()
	{
		return "Background Music Providers";
	}
}