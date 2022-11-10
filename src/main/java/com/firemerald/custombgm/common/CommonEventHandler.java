package com.firemerald.custombgm.common;

import java.util.Objects;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.capabilities.IBossTracker;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.api.event.RegisterBGMProviderConditionSerializersEvent;
import com.firemerald.custombgm.api.event.RegisterBGMProviderSerializersEvent;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.capability.PlayerClient;
import com.firemerald.custombgm.capability.PlayerServer;
import com.firemerald.custombgm.capability.Targeter;
import com.firemerald.custombgm.networking.client.SelfDataSyncPacket;
import com.firemerald.custombgm.networking.server.InitializedPacket;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.custombgm.providers.BaseMusicProvider;
import com.firemerald.custombgm.providers.Providers;
import com.firemerald.custombgm.providers.conditions.*;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CustomBGMAPI.MOD_ID)
public class CommonEventHandler
{
	private static Providers bgmProviders;

	public static Providers getBGMProviders()
	{
		return bgmProviders;
	}

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
		unTarget(event.getEntityLiving());
	}

	@SubscribeEvent
	public static void AttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof Player) event.addCapability(IPlayer.NAME, event.getObject().level.isClientSide() ? new PlayerClient() : new PlayerServer());
		if (event.getObject() instanceof LivingEntity) event.addCapability(Targeter.NAME, new Targeter());
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
					PlayerConditionData playerData = new PlayerConditionData(player, iPlayer);
					bgmProviders.setMusic(playerData);
					if (entity instanceof ServerPlayer && !Objects.equals(iPlayer.getMusicOverride(), iPlayer.getLastMusicOverride()))
					{
						iPlayer.setLastMusicOverride(iPlayer.getMusicOverride());
						CustomBGMMod.NETWORK.sendTo(new SelfDataSyncPacket(iPlayer), (ServerPlayer) entity);
					}
					iPlayer.clearMusicOverride();
				}
				if (iPlayer instanceof PlayerServer) ((PlayerServer) iPlayer).tickTargeters();
			});
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST) //TODO WHY ISN'T THIS FIRING!!!
	public static void onRegisterBGMProviderSerializers(RegisterBGMProviderSerializersEvent event)
	{
		//CustomBGMMod.LOGGER.debug("TESTING onRegisterBGMProviderSerializers");
		event.register(BaseMusicProvider.SERIALIZER_ID, BaseMusicProvider::serialize);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST) //TODO WHY ISN'T THIS FIRING!!!
	public static void onRegisterBGMProviderConditionSerializers(RegisterBGMProviderConditionSerializersEvent event)
	{
		//CustomBGMMod.LOGGER.debug("TESTING onRegisterBGMProviderConditionSerializers");
		event.register(Conditions.ALWAYS_ID, (json, context) -> Conditions.ALWAYS);
		event.register(Conditions.TRUE_ID, (json, context) -> Conditions.ALWAYS);
		event.register(Conditions.NEVER_ID, (json, context) -> Conditions.NEVER);
		event.register(Conditions.FALSE_ID, (json, context) -> Conditions.NEVER);
		event.register(AndCondition.SERIALIZER_ID, AndCondition::serialize);
		event.register(OrCondition.SERIALIZER_ID, OrCondition::serialize);
		event.register(NandCondition.SERIALIZER_ID, NandCondition::serialize);
		event.register(NorCondition.SERIALIZER_ID, NorCondition::serialize);
		event.register(NotCondition.SERIALIZER_ID, NotCondition::serialize);
		event.register(BiomeCondition.SERIALIZER_ID, BiomeCondition::serialize);
		event.register(CombatCondition.SERIALIZER_ID, CombatCondition::serialize);
		event.register(DimensionTypeCondition.SERIALIZER_ID, DimensionTypeCondition::serialize);
		event.register(DimensionCondition.SERIALIZER_ID, DimensionCondition::serialize);
	}

	@SubscribeEvent
	public static void onRegisterServerReloadListeners(AddReloadListenerEvent event)
	{
		event.addListener(bgmProviders = Providers.forDataPacks(event.getConditionContext()));
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		Targeter targeter = Targeter.getOrNull(event.getEntityLiving());
		if (targeter != null)
		{
			unTarget(event.getEntityLiving(), targeter);
			LivingEntity target = event.getTarget();
			if (target instanceof Player)
			{
				IPlayer iPlayer = IPlayer.getOrNull(target);
				if (iPlayer instanceof PlayerServer) ((PlayerServer) iPlayer).onTargeted(event.getEntityLiving());
			}
			targeter.target = target;
		}
	}

	@SubscribeEvent
	public static void onEntityLeaveWorld(EntityLeaveWorldEvent event)
	{
		if (event.getEntity() instanceof LivingEntity) unTarget((LivingEntity) event.getEntity());
	}

	public static void unTarget(LivingEntity targeter)
	{
		Targeter targeter2 = Targeter.getOrNull(targeter);
		if (targeter2 != null) unTarget(targeter, targeter2);
	}

	public static void unTarget(LivingEntity targeter, Targeter targeterCap)
	{
		LivingEntity target = targeterCap.target;
		if (target instanceof Player)
		{
			IPlayer iPlayer = IPlayer.getOrNull(target);
			if (iPlayer instanceof PlayerServer) ((PlayerServer) iPlayer).onUntargeted(targeter);
		}
	}
}