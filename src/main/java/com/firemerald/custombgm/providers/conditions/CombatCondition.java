package com.firemerald.custombgm.providers.conditions;

import java.util.function.Predicate;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.capability.PlayerServer;
import com.firemerald.fecore.util.GsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class CombatCondition implements Predicate<PlayerConditionData>
{
	public static final ResourceLocation SERIALIZER_ID = new ResourceLocation(CustomBGMAPI.MOD_ID, "combat");

	@SuppressWarnings("unchecked")
	public static CombatCondition serialize(JsonObject json, ICondition.IContext conditionContext)
	{
		TagKey<EntityType<?>>[] tags;
		if (json.has("tags")) tags = GsonUtil.arrayFromArrayOrSingle(json.get("tags"), "tags", v -> TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(v)), TagKey[]::new);
		else tags = new TagKey[0];
		ResourceLocation[] entities;
		if (json.has("entities")) entities = GsonUtil.arrayFromArrayOrSingle(json.get("entities"), "entities", ResourceLocation::new, ResourceLocation[]::new);
		else entities = new ResourceLocation[0];
		int minEntities = GsonHelper.getAsInt(json, "minEntities", 1);
		if (minEntities < 1) throw new JsonSyntaxException("Invalid \"minEntities\", must be a positive integer");
		int maxEntities = GsonHelper.getAsInt(json, "maxEntities", Integer.MAX_VALUE);
		if (maxEntities < minEntities) throw new JsonSyntaxException("Invalid \"maxEntities\", must be greater than minEntities (" + minEntities + ")");
		return new CombatCondition(tags, entities, minEntities, maxEntities);
	}

	public final TagKey<EntityType<?>>[] tags;
	public final ResourceLocation[] entities;
	public final int minEntities, maxEntities;

	public CombatCondition(TagKey<EntityType<?>>[] tags, ResourceLocation[] entities, int minEntities, int maxEntities)
	{
		this.tags = tags;
		this.entities = entities;
		this.minEntities = minEntities;
		this.maxEntities = maxEntities;
	}

    private int getEntities(PlayerServer player)
    {
    	return (int) player.getTargeters().stream().map(Entity::getType).filter(this::matches).count();
    }

	public boolean matches(EntityType<?> type)
	{
		if (tags.length == 0 && entities.length == 0) return true;
		for (TagKey<EntityType<?>> tag : tags)
			if (type.is(tag)) return true;
		for (ResourceLocation element : entities)
			if (type.getRegistryName().equals(element)) return true;
		return false;
	}

	@Override
	public boolean test(PlayerConditionData player)
	{
		IPlayer iPlayer = player.iPlayer;
		if (iPlayer instanceof PlayerServer)
		{
			int numEntities = getEntities((PlayerServer) iPlayer);
			return numEntities >= minEntities && numEntities <= maxEntities;
		}
		else return false;
	}
}