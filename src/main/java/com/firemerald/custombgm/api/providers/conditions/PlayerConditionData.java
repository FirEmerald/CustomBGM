package com.firemerald.custombgm.api.providers.conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.google.common.base.Optional;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerConditionData {
	private static final ConditionKey<Holder<Biome>> BIOME_KEY = new ConditionKey<>(CustomBGMAPI.id("biome"));
	private static final ConditionKey<Boolean> SEES_SKY_KEY = new ConditionKey<>(CustomBGMAPI.id("sees_sky"));
	private static final ConditionKey<FluidState> INSIDE_FLUID = new ConditionKey<>(CustomBGMAPI.id("inside_fluid"));
	private static final ConditionKey<Music> VANILLA_BGM_KEY = new ConditionKey<>(CustomBGMAPI.id("current_bgm"));
	private static final ConditionKey<DifficultyInstance> DIFFICULTY_INSTANCE = new ConditionKey<>(CustomBGMAPI.id("effective_difficulty"));
	private static final ConditionKey<GlobalPos> RESPAWN_POINT = new ConditionKey<>(CustomBGMAPI.id("respawn_point"));
	private static final ConditionKey<Raid> ACTIVE_RAID = new ConditionKey<>(CustomBGMAPI.id("raid"));

	public final @Nullable Player player;
	private final Map<ConditionKey<?>, Object> conditionData = new HashMap<>();

	public PlayerConditionData(@Nullable Player player) {
		this.player = player;
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getOptionalData(ConditionKey<T> key) {
		return conditionData.containsKey(key) ? Optional.of((T) conditionData.get(key)) : Optional.absent();
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(ConditionKey<T> key, Supplier<T> value) {
		return (T) conditionData.computeIfAbsent(key, k -> value.get());
	}

	public <T> T getData(ConditionKey<T> key, Function<PlayerConditionData, T> compose) {
		return getData(key, () -> compose.apply(this));
	}

	public <T> T getPlayerData(ConditionKey<T> key, Function<Player, T> compose) {
		return getData(key, () -> player == null ? null : compose.apply(player));
	}

	public <T> void setData(ConditionKey<T> key, T value) {
		if (conditionData.containsKey(key)) CustomBGMAPI.LOGGER.error("Tried to set an existing data key: " + key.id.toString());
		else conditionData.put(key, value);
	}

	public Holder<Biome> getBiome() {
		return getPlayerData(BIOME_KEY, player -> player == null ? null : player.level().getBiome(player.blockPosition()));
	}

	public boolean seesSky() {
		return getPlayerData(SEES_SKY_KEY, player -> player == null ? false : player.level().canSeeSky(player.blockPosition()));
	}

	public FluidState insideFluid() { //TODO all fluids and not just block position fluid
		return getPlayerData(INSIDE_FLUID, player -> player == null || !player.level().isLoaded(player.blockPosition()) ? null : player.level().getFluidState(player.blockPosition()));
	}

	public DifficultyInstance getDifficultyInstance() {
		return getPlayerData(DIFFICULTY_INSTANCE, player -> player == null ? null : player.level().getCurrentDifficultyAt(player.blockPosition()));
	}

	public GlobalPos getRespawnPoint() {
		return getPlayerData(RESPAWN_POINT, PlayerConditionData::respawnPoint);
	}

	@SuppressWarnings("resource")
	private static GlobalPos respawnPoint(Player player) {
		if (player == null) return null;
		else if (player.level().isClientSide) return GlobalPos.of(player.level().dimension(), player.level().getSharedSpawnPos());
		else if (player instanceof ServerPlayer serverPlayer) return GlobalPos.of(serverPlayer.getRespawnDimension(), serverPlayer.getRespawnPosition());
		else return null;
	}

	public Raid getRaid() {
		return getPlayerData(ACTIVE_RAID, player -> player != null && player.level() instanceof ServerLevel level ? level.getRaidAt(player.blockPosition()) : null);
	}

	@OnlyIn(Dist.CLIENT)
	public void setVanillaBGM(Music bgm) {
		setData(VANILLA_BGM_KEY, bgm);
	}

	public Optional<Music> getVanillaBGM() {
		return getOptionalData(VANILLA_BGM_KEY);
	}
}
