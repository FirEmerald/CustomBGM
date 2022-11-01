package com.firemerald.custombgm.providers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.http.util.Asserts;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.BGMProvider;
import com.firemerald.custombgm.api.BGMProviderSerializer;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.RegisterBGMProviderSerializersEvent;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.common.CommonEventHandler;
import com.google.gson.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class Providers implements ResourceManagerReloadListener
{
	private static final List<BGMProvider> DATA_PACK_PROVIDERS = new ArrayList<>();
	private static final List<BGMProvider> RESOURCE_PACK_PROVIDERS = new ArrayList<>();
	private static final List<BGMProvider> PROVIDERS_SORTED = new ArrayList<>();
	private static final Gson GSON = new Gson();
	public static final ResourceLocation MUSIC_LOCATION = new ResourceLocation(CustomBGMAPI.MOD_ID, "custom_bgm.json");
	
	private static void load(ResourceManager resourceManager, ICondition.IContext conditionContext, List<BGMProvider> list)
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
					IOUtils.closeQuietly((Closeable)resource);
				}
			}
			catch (IOException e)
			{
				CustomBGMMod.LOGGER.error("Error parsing custom music properties from " + resourceLocation, e);
			}
		});
		updateProviderList();
	}
	
	private static void updateProviderList()
	{
		PROVIDERS_SORTED.clear();
		PROVIDERS_SORTED.addAll(DATA_PACK_PROVIDERS);
		PROVIDERS_SORTED.addAll(RESOURCE_PACK_PROVIDERS);
		PROVIDERS_SORTED.sort((v1, v2) -> v2.compareTo(v1)); //descending order
	}
	
	public static void setMusic(Player player, IPlayer iPlayer)
	{
		int currentPriority = iPlayer.getCurrentPriority();
		ResourceLocation currentMusic = iPlayer.getLastMusicOverride();
		for (BGMProvider provider : PROVIDERS_SORTED)
		{
			int priority = provider.priority;
			if (priority <= currentPriority) return;
			ResourceLocation music = provider.getMusic(player, currentMusic);
			if (music != null)
			{
				iPlayer.addMusicOverride(music, priority);
				return;
			}
		}
	}
	
	private static final Map<String, BGMProviderSerializer> PROVIDERS = new HashMap<>();

	public static void registerProviders()
	{
		CustomBGMAPI.LOGGER.info("Now registering BGM provider serializers");
		PROVIDERS.clear();
		RegisterBGMProviderSerializersEvent event = new RegisterBGMProviderSerializersEvent((id, provider) -> {
			Asserts.notNull(id, "id");
			Asserts.notNull(provider, "provider");
			String idStr = id.toString();
			if (PROVIDERS.containsKey(idStr))
			{
				CustomBGMAPI.LOGGER.error("Tried to register BGM provider serializer with existing name " + idStr);
				return false;
			}
			else
			{
				CustomBGMAPI.LOGGER.debug("Registering BGM provider serializer with name " + idStr);
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
		return serializer.serialize(json, priority, conditionContext);
	}
	
	public static Providers forDataPacks(ICondition.IContext context)
	{
		return new Providers(context, DATA_PACK_PROVIDERS);
	}
	
	public static Providers forResourcePacks()
	{
		return new Providers(ICondition.IContext.EMPTY, RESOURCE_PACK_PROVIDERS);
	}
	
	public final ICondition.IContext context;
	private final List<BGMProvider> list;
	
	private Providers(ICondition.IContext context, List<BGMProvider> list)
	{
		this.context = context;
		this.list = list;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
		load(resourceManager, context, list);
	}
}