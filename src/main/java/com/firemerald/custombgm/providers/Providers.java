package com.firemerald.custombgm.providers;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.firemerald.custombgm.CustomBGM;
import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.util.LookupContext;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class Providers implements ResourceManagerReloadListener {
	private static final Gson GSON = new Gson();

	public static Providers forDataPacks(ICondition.IContext context, HolderLookup.Provider provider) {
		return new Providers(context, provider);
	}

	@OnlyIn(Dist.CLIENT)
	public static Providers forResourcePacks() {
		return new Providers(LookupContext.SIMPLE);
	}

	private ICondition.IContext context;
	private HolderLookup.Provider provider;
	private final List<BGMProvider> list = new ArrayList<>();

	private Providers(ICondition.IContext context, HolderLookup.Provider provider) {
		this.context = context;
		this.provider = provider;
	}

	private Providers(LookupContext context) {
		this.context = context;
		this.provider = context.provider();
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		load(resourceManager, context);
	}

	@OnlyIn(Dist.CLIENT)
	public void onTagsLoaded(HolderLookup.Provider provider) {
		setContext(new LookupContext(provider));
	}

	@OnlyIn(Dist.CLIENT)
	public void onTagsUnloaded() {
		setContext(LookupContext.SIMPLE);
	}

	@OnlyIn(Dist.CLIENT)
	public void setContext(LookupContext context) {
		this.provider = context.provider();
		load(Minecraft.getInstance().getResourceManager(), this.context = context);
	}

	private void load(ResourceManager resourceManager, ICondition.IContext conditionContext) {
        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, provider);
		Map<ResourceLocation, Resource> resourceLocations = resourceManager.listResources("custom_bgm", p -> p.getPath().endsWith(".json"));
		list.clear();
		resourceLocations.forEach((resourceLocation, resource) -> {
			CustomBGM.LOGGER.debug("Loading custom music from " + resourceLocation + " in pack " + resource.sourcePackId());
            try (Reader reader = resource.openAsReader())
            {
            	JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);
            	if (json == null) {
            		CustomBGM.LOGGER.error("Couldn't load custom music properties from " + resourceLocation + " in pack " + resource.sourcePackId() + " as it is empty or null");
            	}
            	else {
            		if (CraftingHelper.processConditions(json, "conditions", conditionContext)) {
            			DataResult<BGMProvider> parsed = BGMProvider.CODEC.parse(registryOps, json);
            			parsed.get().ifLeft(provider -> {
        					list.add(provider);
            			}).ifRight(err -> {
        					CustomBGM.LOGGER.debug("Could not load custom music properties from " + resourceLocation + " in pack " + resource.sourcePackId() + ": failed to parse provider instance: " + err.message());
            			});
            		} else {
        				CustomBGM.LOGGER.debug("Skipping loading custom music properties from " + resourceLocation + " in pack " + resource.sourcePackId() + " as it's conditions were not met");
            		}
            	}
            }
            catch (Throwable t) {
            	CustomBGM.LOGGER.error("Couldn't read custom music properties from " + resourceLocation + " in pack " + resource.sourcePackId(), t);
            }
		});
		list.sort((v1, v2) -> v2.compareTo(v1)); //descending order
	}

	private OverrideResults getMusic(PlayerConditionData player, int currentPriority, List<BgmDistribution> distributions) {
		for (BGMProvider provider : list) {
			int priority = provider.priority;
			if (priority < currentPriority) break; //below current priority, abort loop
			else {
				BgmDistribution music = provider.getMusic(player);
				if (music != null) {
					if (priority > currentPriority) { //new highest priority
						distributions.clear();
						currentPriority = priority;
					}
					distributions.add(music);
				}
			}
		}
		return new OverrideResults(distributions, currentPriority);
	}

	public OverrideResults getMusic(PlayerConditionData player, IOverrideResults currentOverride) {
		if (currentOverride == null) return getMusic(player, Integer.MIN_VALUE, new ArrayList<>());
		else return getMusic(player, currentOverride.priority(), new ArrayList<>(currentOverride.overrides()));
	}
}