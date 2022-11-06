package com.firemerald.custombgm.capability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.firemerald.custombgm.CustomBGMMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class PlayerServer extends PlayerBase
{
	private ResourceLocation synchronizedMusic = null;
	private final Map<LivingEntity, Target> targeters = new HashMap<>();

	public static class Target
	{
		private boolean expired = false;
		private int time;

		public void expire()
		{
			expired = true;
			time = 0;
		}

		public void renew()
		{
			expired = false;
		}

		public boolean tickInvalid()
		{
			return expired && ++time >= CustomBGMMod.SERVER.combatTimeout.get();
		}
	}

	public void onTargeted(LivingEntity targeter)
	{
		targeters.computeIfAbsent(targeter, entity -> new Target()).renew();
	}

	public void onUntargeted(LivingEntity targeter)
	{
		targeters.computeIfPresent(targeter, (entity, target) -> {
			target.expire();
			return target;
		});
	}

	public void tickTargeters()
	{
		List<LivingEntity> toRemove = targeters.entrySet().parallelStream().filter(entry -> entry.getValue().tickInvalid()).map(Map.Entry::getKey).toList();
		toRemove.forEach(targeters::remove);
	}

	public Set<LivingEntity> getTargeters()
	{
		return targeters.keySet();
	}

	@Override
	public void clearMusicOverride()
	{
		this.musicOverride = null;
		this.musicOverridePriority = Integer.MIN_VALUE;
	}

	@Override
	public ResourceLocation getLastMusicOverride()
	{
		return synchronizedMusic;
	}

	@Override
	public void setLastMusicOverride(ResourceLocation music)
	{
		this.synchronizedMusic = music;
	}

	@Override
	public void setServerMusic(ResourceLocation music, int priority) {} //unsupported
}