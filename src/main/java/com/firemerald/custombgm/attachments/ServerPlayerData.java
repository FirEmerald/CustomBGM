package com.firemerald.custombgm.attachments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.conditions.ConditionKey;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.config.ServerConfig;
import com.firemerald.custombgm.init.CustomBGMAttachments;
import com.firemerald.custombgm.providers.IOverrideResults;
import com.firemerald.custombgm.providers.OverrideResults;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ServerPlayerData implements IOverrideResults {
	public static final ConditionKey<ServerPlayerData> KEY = new ConditionKey<>(CustomBGMAPI.id("server_player_data"));

	public static ServerPlayerData getServerPlayerData(PlayerConditionData playerData) {
		return playerData.getPlayerData(KEY, player -> player.getData(CustomBGMAttachments.SERVER_PLAYER_DATA));
	}

	private int thisTickPriority = Integer.MIN_VALUE;
	private List<BgmDistribution> thisTickMusic = new ArrayList<>();
	private OverrideResults synchronizedMusic = null;
	private final Map<LivingEntity, Targeter> targeters = new HashMap<>();
	private int nextSyncTick = 0;

	public static class Targeter {
		protected boolean targeted = false;
		protected int untargetedTime = 0;
		protected int attackTime = ServerConfig.attackTimeout;

		protected void targeted() {
			targeted = true;
		}

		protected void onAttack() {
			attackTime = 0;
		}

		protected void untargeted() {
			targeted = false;
			untargetedTime = 0;
		}

		protected boolean tickInvalid() {
			return tickUntargeted() && tickAttack();
		}

		protected boolean tickUntargeted() {
			if (targeted) return false;
			else if (untargetedTime >= ServerConfig.trackingTimeout) return true;
			else {
				untargetedTime++;
				return false;
			}
		}

		protected boolean tickAttack() {
			if (attackTime >= ServerConfig.attackTimeout) return true;
			else {
				attackTime++;
				return false;
			}
		}
	}

	public void onTargeted(LivingEntity targeter) {
		targeters.computeIfAbsent(targeter, entity -> new Targeter()).targeted();
	}

	public void onAttack(LivingEntity targeter) {
		targeters.computeIfAbsent(targeter, entity -> new Targeter()).onAttack();
	}

	public void onUntargeted(LivingEntity targeter) {
		Targeter target = targeters.get(targeter);
		if (target != null) target.untargeted();
	}

	public void tickTargeters() {
		List<LivingEntity> toRemove = targeters.entrySet().parallelStream().filter(entry -> entry.getKey().isDeadOrDying() || entry.getValue().tickInvalid()).map(Map.Entry::getKey).toList();
		toRemove.forEach(targeters::remove);
	}

	public Collection<LivingEntity> getTargeters() {
		return targeters.keySet();
	}

	public OverrideResults setMusicOverride(OverrideResults overrideResults, ServerPlayer player) {
		if (player.tickCount >= nextSyncTick || !overrideResults.equals(synchronizedMusic)) {
			nextSyncTick = player.tickCount + 10 * 20; //every 10 seconds
			return synchronizedMusic = overrideResults;
		} else return null;
	}

	public void resetTickMusic() {
		thisTickPriority = Integer.MIN_VALUE;
		thisTickMusic.clear();
	}

	public void addMusicOverride(BgmDistribution music, int priority) {
		if (priority >= thisTickPriority) {
			if (priority > thisTickPriority) { //new highest priority
				thisTickMusic.clear();
				thisTickPriority = priority;
			}
			thisTickMusic.add(music);
		}
	}

	@Override
	public int priority() {
		return thisTickPriority;
	}

	@Override
	public List<BgmDistribution> overrides() {
		return thisTickMusic;
	}
}
