package com.firemerald.custombgm.common;

import com.firemerald.custombgm.CustomBGM;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.capabilities.BossTracker;
import com.firemerald.custombgm.capabilities.ServerPlayerData;
import com.firemerald.custombgm.capabilities.Targeter;
import com.firemerald.custombgm.network.clientbound.MusicSyncPacket;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.custombgm.providers.OverrideResults;
import com.firemerald.custombgm.providers.Providers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {

	private static Providers bgmProviders;

	public static Providers getBGMProviders() {
		return bgmProviders;
	}

	@SubscribeEvent
	public static void registerServerReloadListeners(AddReloadListenerEvent event) {
		event.addListener(bgmProviders = Providers.forDataPacks(event.getConditionContext(), event.getRegistryAccess()));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST) //we want to cancel the event as soon as possible
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) { //do not load boss entities
		if (event.loadedFromDisk()) {
			BossTracker.get(event.getEntity()).ifPresent(tracker -> {
				if (tracker.isBoss()) {
					tracker.setNoBossSpawner(); //no longer tracking
					event.setCanceled(true);
				}
			});
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void leaveWorld(EntityLeaveLevelEvent event) { //boss despawned
		BossTracker.get(event.getEntity()).ifPresent(tracker -> {
			Object blockEntity = tracker.getBossSpawnerObject();
			if (blockEntity instanceof IOperatorSource<?, ?> source) {
				OperatorBase<?, ?, ?> spawner = source.getOperator();
				if (spawner instanceof BossSpawnerOperator<?, ?> operator) operator.setBoss(null);
			}
			tracker.setNoBossSpawner(); //no longer tracking
		});
		if (event.getEntity() instanceof IOperatorSource<?, ?> source) source.getOperator().onRemoved();
		if (event.getEntity() instanceof LivingEntity livingEntity) unTarget(livingEntity);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void livingDeath(LivingDeathEvent event) { //boss killed
		BossTracker.get(event.getEntity()).ifPresent(tracker -> {
			Object blockEntity = tracker.getBossSpawnerObject();
			if (blockEntity instanceof IOperatorSource<?, ?> source) {
				OperatorBase<?, ?, ?> spawner = source.getOperator();
				if (spawner instanceof BossSpawnerOperator<?, ?> operator) {
					operator.setKilled(true);
					operator.setBoss(null);
				}
			}
			tracker.setNoBossSpawner(); //no longer tracking
		});
		unTarget(event.getEntity());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void livingChangeTarget(LivingChangeTargetEvent event) {
		unTarget(event.getEntity());
		if (event.getNewTarget() instanceof ServerPlayer player) {
			Targeter.get(event.getEntity()).ifPresent(targeter -> targeter.target = player);
			ServerPlayerData.get(player).ifPresent(data -> data.onTargeted(event.getEntity()));
		} else Targeter.get(event.getEntity()).ifPresent(targeter -> targeter.target = null);
	}

	public static void unTarget(LivingEntity targeter) {
		Targeter.get(targeter).ifPresent(targeterCap -> {
			if (targeterCap.target != null) ServerPlayerData.get(targeterCap.target).ifPresent(player -> player.onUntargeted(targeter));
		});
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void tickServerPlayer(LivingTickEvent event) //we must use this method as any mod could cancel this and prevent all other events from firing
	{
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			ServerPlayerData.get(serverPlayer).ifPresent(serverData -> {
				PlayerConditionData playerData = new PlayerConditionData(serverPlayer);
				OverrideResults synchronize = serverData.setMusicOverride(bgmProviders.getMusic(playerData, serverData), serverPlayer);
				if (synchronize != null) CustomBGM.NETWORK.sendTo(new MusicSyncPacket(synchronize), serverPlayer);
				serverData.resetTickMusic();
				serverData.tickTargeters();
			});
		}
	}

	@SubscribeEvent
	public static void livingDamage(LivingDamageEvent event) {
		if (event.getSource().getDirectEntity() instanceof ServerPlayer player) {
			ServerPlayerData.get(player).ifPresent(playerData -> playerData.onAttack(event.getEntity()));
		}
	}

	@SubscribeEvent
	public static void playerAttack(AttackEntityEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof LivingEntity entity) {
			ServerPlayerData.get(player).ifPresent(playerData -> playerData.onAttack(entity));
		}
	}

	@SubscribeEvent
	public static void AttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof ServerPlayer) event.addCapability(ServerPlayerData.NAME, new ServerPlayerData());
		if (event.getObject() instanceof LivingEntity) event.addCapability(Targeter.NAME, new Targeter());
		event.addCapability(BossTracker.NAME, new BossTracker());
	}
}
