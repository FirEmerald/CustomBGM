package com.firemerald.custombgm.common;

import java.util.Objects;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.ICustomMusic;
import com.firemerald.custombgm.api.capabilities.IBossTracker;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.capability.PlayerBase;
import com.firemerald.custombgm.capability.PlayerServer;
import com.firemerald.custombgm.networking.client.SelfDataSyncPacket;
import com.firemerald.custombgm.networking.server.InitializedPacket;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEventHandler
{
	@SubscribeEvent(priority = EventPriority.HIGHEST) //we want to cancel the event as soon as possible
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) //do not load boss entities
	{
		if (event.loadedFromDisk()) IBossTracker.get(event.getEntity()).ifPresent(tracker -> {
			if (tracker.isBoss())
			{
				tracker.setNoBossSpawner(); //no longer tracking
				event.setCanceled(true);
			}
		});
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLeaveWorld(EntityLeaveWorldEvent event) //boss despawned
	{
		IBossTracker.get(event.getEntity()).ifPresent(tracker -> {
			Object blockEntity = tracker.getBossSpawnerObject();
			if (blockEntity instanceof IOperatorSource)
			{
				OperatorBase<?, ?, ?> spawner = ((IOperatorSource<?, ?>) blockEntity).getOperator();
				if (spawner instanceof BossSpawnerOperator) ((BossSpawnerOperator<?, ?>) spawner).setBoss(null);
			}
			tracker.setNoBossSpawner(); //no longer tracking
		});
		if (event.getEntity() instanceof IOperatorSource) ((IOperatorSource<?, ?>) event.getEntity()).getOperator().onRemoved();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDeath(LivingDeathEvent event) //boss killed
	{
		IBossTracker.get(event.getEntity()).ifPresent(tracker -> {
			Object blockEntity = tracker.getBossSpawnerObject();
			if (blockEntity instanceof IOperatorSource)
			{
				OperatorBase<?, ?, ?> spawner = ((IOperatorSource<?, ?>) blockEntity).getOperator();
				if (spawner instanceof BossSpawnerOperator)
				{
					BossSpawnerOperator<?, ?> operator = (BossSpawnerOperator<?, ?>) spawner;
					operator.setKilled(true);
					operator.setBoss(null);
				}
			}
			tracker.setNoBossSpawner(); //no longer tracking
		});
	}

	@SubscribeEvent
	public static void AttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof Player) event.addCapability(IPlayer.NAME, event.getObject().level.isClientSide() ? new PlayerBase() : new PlayerServer());
		event.addCapability(IBossTracker.NAME, new IBossTracker.Impl());
	}

	@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
	public static void onLivingUpdatePostAlways(LivingUpdateEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof Player)
		{
			Player player = (Player) entity;
			IPlayer.get(player).ifPresent(iPlayer -> {
				if (player.level.isClientSide)
				{
					if (!iPlayer.getInit())
					{
						CustomBGMMod.NETWORK.sendToServer(new InitializedPacket());
						iPlayer.setInit(true);
					}
				}
				else if (iPlayer.getInit())
				{
					Holder<Biome> biome = player.level.getBiome(player.blockPosition());
					ICustomMusic<Holder<Biome>> music = CustomBGMAPI.instance.getBiomeMusic(biome);
					if (music != null)
					{
						ResourceLocation mus = music.getMusic(player, iPlayer.getLastMusicOverride(), biome);
						if (mus != null) iPlayer.addMusicOverride(mus, 0);
					}
					if (entity instanceof ServerPlayer && !Objects.equals(iPlayer.getMusicOverride(), iPlayer.getLastMusicOverride()))
					{
						iPlayer.setLastMusicOverride(iPlayer.getMusicOverride());
						CustomBGMMod.NETWORK.sendTo(new SelfDataSyncPacket(iPlayer), (ServerPlayer) entity);
					}
					iPlayer.clearMusicOverride();
				}
			});
		}
	}
}