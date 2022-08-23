package com.firemerald.custombgm.common;

import java.util.Objects;

import com.firemerald.custombgm.api.CustomBGMCapabilities;
import com.firemerald.custombgm.api.IBossTracker;
import com.firemerald.custombgm.api.ICustomMusic;
import com.firemerald.custombgm.api.IPlayer;
import com.firemerald.custombgm.blockentity.BlockEntityBossSpawner;
import com.firemerald.custombgm.capability.PlayerBase;
import com.firemerald.custombgm.capability.PlayerServer;
import com.firemerald.custombgm.init.CustomBGMBlocks;
import com.firemerald.custombgm.init.CustomBGMItems;
import com.firemerald.custombgm.init.CustomBGMSounds;
import com.firemerald.custombgm.networking.CustomBGMNetwork;
import com.firemerald.custombgm.networking.client.SelfDataSyncPacket;
import com.firemerald.custombgm.networking.server.InitializedPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEventHandler
{
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEntityLeaveWorld(EntityLeaveWorldEvent event) //boss despawn removal
	{
		LazyOptional<IBossTracker> cap = event.getEntity().getCapability(CustomBGMCapabilities.BOSS_TRACKER);
		if (cap.isPresent())
		{
			IBossTracker tracker = cap.resolve().get();
			BlockEntity blockEntity = tracker.getBossBlock();
			if (blockEntity instanceof BlockEntityBossSpawner) ((BlockEntityBossSpawner) blockEntity).setBoss(null);
			event.getEntity().discard(); //forcibly despawn entity
			tracker.setBossBlock(null, null);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDeath(LivingDeathEvent event) //boss killed
	{
		LazyOptional<IBossTracker> cap = event.getEntity().getCapability(CustomBGMCapabilities.BOSS_TRACKER);
		if (cap.isPresent())
		{
			IBossTracker tracker = cap.resolve().get();
			BlockEntity blockEntity = tracker.getBossBlock();
			if (blockEntity instanceof BlockEntityBossSpawner)
			{
				BlockEntityBossSpawner spawner = (BlockEntityBossSpawner) blockEntity;
				spawner.setKilled(true);
				spawner.setBoss(null);
			}
			tracker.setBossBlock(null, null); //no longer tracking
		}
	}

	@SubscribeEvent
	public static void AttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof Player)
		{
			event.addCapability(IPlayer.CAPABILITY_NAME, event.getObject().level.isClientSide() ? new PlayerBase() : new PlayerServer());
		}
		event.addCapability(IBossTracker.CAPABILITY_NAME, new IBossTracker.Impl());
	}

	@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
	public static void onLivingUpdatePostAlways(LivingUpdateEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof Player)
		{
			Player player = (Player) entity;
			LazyOptional<IPlayer> cap = player.getCapability(CustomBGMCapabilities.MUSIC_PLAYER);
			if (cap.isPresent())
			{
				IPlayer lsPlayer = cap.resolve().get();
				if (player.level.isClientSide)
				{
					if (!lsPlayer.getInit())
					{
						CustomBGMNetwork.INSTANCE.sendToServer(new InitializedPacket());
						lsPlayer.setInit(true);
					}
				}
				else if (lsPlayer.getInit())
				{
					Biome biome = player.level.getBiome(player.blockPosition()).value();
					if (biome instanceof ICustomMusic)
					{
						ResourceLocation mus = ((ICustomMusic) biome).getMusic(player, lsPlayer.getLastMusicOverride());
						if (mus != null) lsPlayer.addMusicOverride(mus, 0);
					}
					if (entity instanceof ServerPlayer && !Objects.equals(lsPlayer.getMusicOverride(), lsPlayer.getLastMusicOverride()))
					{
						lsPlayer.setLastMusicOverride(lsPlayer.getMusicOverride());
						Main.network().sendTo(new SelfDataSyncPacket(lsPlayer), (EntityPlayerMP) entity);
					}
					lsPlayer.clearMusicOverride();
				}
			}
		}
	}
}