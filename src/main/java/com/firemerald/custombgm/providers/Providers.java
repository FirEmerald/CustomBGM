package com.firemerald.custombgm.providers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.event.RegisterBGMProviderSerializersEvent;
import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.api.providers.BGMProviderSerializer;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.common.CommonEventHandler;
import com.firemerald.custombgm.providers.conditions.Conditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class Providers implements ResourceManagerReloadListener
{
	private static final Gson GSON = new Gson();

	private void load(ResourceManager resourceManager, ICondition.IContext conditionContext)
	{
		Collection<ResourceLocation> resourceLocations = resourceManager.listResources("custom_bgm", p -> p.endsWith(".json"));
		list.clear();
		resourceLocations.forEach(resourceLocation -> {
			try
			{
				Resource resource = resourceManager.getResource(resourceLocation);
				CustomBGMMod.LOGGER.debug("Loading custom music from " + resourceLocation + " in pack " + resource.getSourceName());
				try
				{
	                  InputStream in = resource.getInputStream();
	                  try
	                  {
	                     Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
	                     try
	                     {
	                        JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);
	                        if (json == null)
	                        {
	                        	CustomBGMMod.LOGGER.error("Couldn't load custom music properties from " + resourceLocation + " in pack " + resource.getSourceName() + " as it is empty or null");
	                        }
	                        else
	                        {
	                        	try
                				{
                					if (!CraftingHelper.processConditions(json, "conditions", conditionContext)) CustomBGMMod.LOGGER.debug("Skipping loading custom music properties from " + resourceLocation + " in pack " + resource.getSourceName() + " as it's conditions were not met");
                					else list.add(serialize(json, conditionContext));
                				}
                				catch (Exception e)
                				{
                					CustomBGMMod.LOGGER.error("Error parsing custom music properties from " + resourceLocation + " in pack " + resource.getSourceName(), e);
                				}
	                        }
	                     }
	                     catch (Throwable t)
	                     {
	                        try
	                        {
	                           reader.close();
	                        }
	                        catch (Throwable t2)
	                        {
	                           t.addSuppressed(t2);
	                        }
	                        throw t;
	                     }
	                     reader.close();
	                  }
	                  catch (Throwable t)
	                  {
	                     if (in != null)
	                     {
	                        try
	                        {
	                           in.close();
	                        }
	                        catch (Throwable t2)
	                        {
	                           t.addSuppressed(t2);
	                        }
	                     }
	                     throw t;
	                  }
	                  if (in != null) in.close();
				}
				catch (RuntimeException | IOException e)
				{
					CustomBGMMod.LOGGER.error("Couldn't read custom music properties from " + resourceLocation + " in pack " + resource.getSourceName(), e);
				}
				finally
				{
					IOUtils.closeQuietly(resource);
				}
			}
			catch (IOException e)
			{
				CustomBGMMod.LOGGER.error("Error parsing custom music properties from " + resourceLocation, e);
			}
		});
		list.sort((v1, v2) -> v2.compareTo(v1)); //descending order
	}

	public void setMusic(PlayerConditionData player)
	{
		int currentPriority = player.iPlayer.getCurrentPriority();
		ResourceLocation currentMusic = player.iPlayer.getLastMusicOverride();
		for (BGMProvider provider : list)
		{
			int priority = provider.priority;
			if (priority <= currentPriority) return;
			ResourceLocation music = provider.getMusic(player, currentMusic);
			if (music != null)
			{
				player.iPlayer.addMusicOverride(music, priority);
				return;
			}
		}
	}

	private static final Map<String, BGMProviderSerializer> PROVIDERS = new HashMap<>();

	public static void registerProviders()
	{
		CustomBGMMod.LOGGER.info("Now registering BGM provider serializers");
		PROVIDERS.clear();
		RegisterBGMProviderSerializersEvent event = new RegisterBGMProviderSerializersEvent((id, provider) -> {
			//CustomBGMAPI.LOGGER.debug("Attempting to register BGM provider serializer with name " + id);
			if (id == null)
			{
				CustomBGMMod.LOGGER.error("Tried to register BGM provider serializer with null name");
				return false;
			}
			String idStr = id.toString();
			if (provider == null)
			{
				CustomBGMMod.LOGGER.error("Tried to register null BGM provider serializer with name " + idStr);
				return false;
			}
			if (PROVIDERS.containsKey(idStr))
			{
				CustomBGMMod.LOGGER.error("Tried to register BGM provider serializer with existing name " + idStr);
				return false;
			}
			else
			{
				CustomBGMMod.LOGGER.debug("Registering BGM provider serializer with name " + idStr);
				PROVIDERS.put(idStr, provider);
				return true;
			}
		});
		CommonEventHandler.onRegisterBGMProviderSerializers(event); //TODO WHY IN THE HECK ISN'T THE EVENT LISTENER CATCHING THE EVENT!!!!!!
		MinecraftForge.EVENT_BUS.post(event);
	}

	@Nullable
	public static BGMProviderSerializer getSerializer(ResourceLocation id)
	{
		return getSerializer(id.toString());
	}

	@Nullable
	public static BGMProviderSerializer getSerializer(String id)
	{
		return PROVIDERS.get(id.toString());
	}

	@Nullable
	public static BGMProvider serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		String type = GsonHelper.getAsString(json, "type", BaseMusicProvider.SERIALIZER_ID.toString());
		BGMProviderSerializer serializer = getSerializer(type);
		if (serializer == null) throw new JsonParseException(type + " is not a registered BGMProvider serializer");
		int priority = GsonHelper.getAsInt(json, "priority", 0);
		Predicate<PlayerConditionData> condition;
		if (json.has("condition"))
		{
			JsonObject obj = GsonHelper.getAsJsonObject(json, "condition");
			condition = Conditions.serialize(obj, conditionContext);
		}
		else condition = Conditions.ALWAYS;
		return serializer.serialize(json, priority, condition, conditionContext);
	}

	public static Providers forDataPacks(ICondition.IContext context)
	{
		return new Providers(context);
	}

	public static Providers forResourcePacks()
	{
		return new Providers(ICondition.IContext.EMPTY);
	}

	public final ICondition.IContext context;
	private final List<BGMProvider> list = new ArrayList<>();

	private Providers(ICondition.IContext context)
	{
		this.context = context;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
		load(resourceManager, context);
	}
}