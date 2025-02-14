package com.firemerald.custombgm.common;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.attachments.BossTracker;
import com.firemerald.custombgm.attachments.ServerPlayerData;
import com.firemerald.custombgm.init.CustomBGMAttachments;
import com.firemerald.custombgm.network.clientbound.MusicSyncPacket;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.custombgm.providers.OverrideResults;
import com.firemerald.custombgm.providers.Providers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEventHandler {

	private static Providers bgmProviders;

	public static Providers getBGMProviders() {
		return bgmProviders;
	}

	@SubscribeEvent
	public static void registerServerReloadListeners(AddServerReloadListenersEvent event) {
		event.addListener(CustomBGMAPI.id("datapack_providers"), bgmProviders = Providers.forDataPacks(event.getConditionContext()));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST) //we want to cancel the event as soon as possible
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) { //do not load boss entities
		if (event.loadedFromDisk()) {
			BossTracker tracker = event.getEntity().getData(CustomBGMAttachments.BOSS_TRACKER);
			if (tracker.isBoss()) {
				tracker.setNoBossSpawner(); //no longer tracking
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void leaveWorld(EntityLeaveLevelEvent event) { //boss despawned
		BossTracker tracker = event.getEntity().getData(CustomBGMAttachments.BOSS_TRACKER);
		Object blockEntity = tracker.getBossSpawnerObject();
		if (blockEntity instanceof IOperatorSource<?, ?> source) {
			OperatorBase<?, ?, ?> spawner = source.getOperator();
			if (spawner instanceof BossSpawnerOperator<?, ?> operator) operator.setBoss(null);
		}
		tracker.setNoBossSpawner(); //no longer tracking
		if (event.getEntity() instanceof IOperatorSource<?, ?> source) source.getOperator().onRemoved();
		if (event.getEntity() instanceof LivingEntity livingEntity) unTarget(livingEntity);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void livingDeath(LivingDeathEvent event) { //boss killed
		BossTracker tracker = event.getEntity().getData(CustomBGMAttachments.BOSS_TRACKER);
		Object blockEntity = tracker.getBossSpawnerObject();
		if (blockEntity instanceof IOperatorSource<?, ?> source) {
			OperatorBase<?, ?, ?> spawner = source.getOperator();
			if (spawner instanceof BossSpawnerOperator<?, ?> operator) {
				operator.setKilled(true);
				operator.setBoss(null);
			}
		}
		tracker.setNoBossSpawner(); //no longer tracking
		unTarget(event.getEntity());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void livingChangeTarget(LivingChangeTargetEvent event) {
		unTarget(event.getEntity());
		if (event.getNewAboutToBeSetTarget() instanceof ServerPlayer player) {
			event.getEntity().setData(CustomBGMAttachments.PLAYER_TARGET, player);
			player.getData(CustomBGMAttachments.SERVER_PLAYER_DATA).onTargeted(event.getEntity());
		}
	}

	public static void unTarget(LivingEntity targeter) {
		ServerPlayer target = targeter.getData(CustomBGMAttachments.PLAYER_TARGET);
		if (target != null) target.getData(CustomBGMAttachments.SERVER_PLAYER_DATA).onUntargeted(targeter);
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void tickServerPlayer(EntityTickEvent.Pre event) //we must use this method as any mod could cancel this and prevent all other events from firing
	{
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			ServerPlayerData serverData = serverPlayer.getData(CustomBGMAttachments.SERVER_PLAYER_DATA);
			PlayerConditionData playerData = new PlayerConditionData(serverPlayer);
			OverrideResults synchronize = serverData.setMusicOverride(bgmProviders.getMusic(playerData, serverData), serverPlayer);
			if (synchronize != null) new MusicSyncPacket(synchronize).sendToClient(serverPlayer);
			serverData.resetTickMusic();
			serverData.tickTargeters();
		}
	}

	@SubscribeEvent
	public static void livingDamage(LivingIncomingDamageEvent event) {
		if (event.getSource().getDirectEntity() instanceof ServerPlayer player) {
			ServerPlayerData playerData = player.getData(CustomBGMAttachments.SERVER_PLAYER_DATA);
			playerData.onAttack(event.getEntity());
		}
	}
	
	@SubscribeEvent
	public static void playerAttack(AttackEntityEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof LivingEntity entity) {
			ServerPlayerData playerData = player.getData(CustomBGMAttachments.SERVER_PLAYER_DATA);
			playerData.onAttack(entity);
		}
	}
}
