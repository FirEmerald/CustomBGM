package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.CustomBGMRegistries;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.providers.conditions.PlayBossMusicCondition;
import com.firemerald.custombgm.providers.conditions.VanillaBGMCondition;
import com.firemerald.custombgm.providers.conditions.constant.FalseCondition;
import com.firemerald.custombgm.providers.conditions.constant.TrueCondition;
import com.firemerald.custombgm.providers.conditions.modifier.AndCondition;
import com.firemerald.custombgm.providers.conditions.modifier.NandCondition;
import com.firemerald.custombgm.providers.conditions.modifier.NorCondition;
import com.firemerald.custombgm.providers.conditions.modifier.NotCondition;
import com.firemerald.custombgm.providers.conditions.modifier.OrCondition;
import com.firemerald.custombgm.providers.conditions.modifier.XnorCondition;
import com.firemerald.custombgm.providers.conditions.modifier.XorCondition;
import com.firemerald.custombgm.providers.conditions.player.CombatCondition;
import com.firemerald.custombgm.providers.conditions.player.EntityCondition;
import com.firemerald.custombgm.providers.conditions.player.InGameCondition;
import com.firemerald.custombgm.providers.conditions.player.MobEffectCondition;
import com.firemerald.custombgm.providers.conditions.player.RaidCondition;
import com.firemerald.custombgm.providers.conditions.player.TeamCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.BreathCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.CrouchingCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.FlyingCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.GameModeCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.HealthCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.OnFireCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.ScaleCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.SprintingCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.StatisticCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.SwimmingCondition;
import com.firemerald.custombgm.providers.conditions.player.inventory.EquipmentCondition;
import com.firemerald.custombgm.providers.conditions.player.inventory.SlotsCondition;
import com.firemerald.custombgm.providers.conditions.player.level.DifficultyCondition;
import com.firemerald.custombgm.providers.conditions.player.level.RegionalDifficultyCondition;
import com.firemerald.custombgm.providers.conditions.player.level.LightLevelCondition;
import com.firemerald.custombgm.providers.conditions.player.level.TimeCondition;
import com.firemerald.custombgm.providers.conditions.player.level.WeatherCondition;
import com.firemerald.custombgm.providers.conditions.player.location.BiomeCondition;
import com.firemerald.custombgm.providers.conditions.player.location.DimensionCondition;
import com.firemerald.custombgm.providers.conditions.player.location.DimensionTypeCondition;
import com.firemerald.custombgm.providers.conditions.player.location.HeightCondition;
import com.firemerald.custombgm.providers.conditions.player.location.InFluidCondition;
import com.firemerald.custombgm.providers.conditions.player.location.InShapeCondition;
import com.firemerald.custombgm.providers.conditions.player.location.LastDeathPositionCondition;
import com.firemerald.custombgm.providers.conditions.player.location.SeesSkyCondition;
import com.firemerald.custombgm.providers.conditions.player.location.SpawnpointCondition;
import com.firemerald.custombgm.providers.conditions.player.location.StructureCondition;
import com.firemerald.custombgm.providers.conditions.player.location.UnderwaterCondition;
import com.mojang.serialization.MapCodec;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomBGMConditions {
	private static DeferredRegister<MapCodec<? extends BGMProviderCondition>> registry = DeferredRegister.create(CustomBGMRegistries.Keys.CONDITION_CODECS, CustomBGMAPI.MOD_ID);

	//constant
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<FalseCondition>> NEVER = registry.register("never", () -> FalseCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<TrueCondition>> ALWAYS = registry.register("always", () -> TrueCondition.CODEC);

	//modifier
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<AndCondition>> AND = registry.register("and", () -> AndCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<NandCondition>> NAND = registry.register("nand", () -> NandCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<NorCondition>> NOR = registry.register("nor", () -> NorCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<NotCondition>> NOT = registry.register("not", () -> NotCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<OrCondition>> OR = registry.register("or", () -> OrCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<XnorCondition>> XNOR = registry.register("xnor", () -> XnorCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<XorCondition>> XOR = registry.register("xor", () -> XorCondition.CODEC);

	//player.attributes
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<BreathCondition>> BREATH = registry.register("breath", () -> BreathCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<CrouchingCondition>> CROUCHING = registry.register("crouching", () -> CrouchingCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<FlyingCondition>> FLYING = registry.register("flying", () -> FlyingCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<GameModeCondition>> GAME_MODE = registry.register("game_mode", () -> GameModeCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<HealthCondition>> HEALTH = registry.register("health", () -> HealthCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<OnFireCondition>> ON_FIRE = registry.register("on_fire", () -> OnFireCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<ScaleCondition>> SCALE = registry.register("scale", () -> ScaleCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<SprintingCondition>> SPRINTING = registry.register("sprinting", () -> SprintingCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<StatisticCondition>> STATS = registry.register("stats", () -> StatisticCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<SwimmingCondition>> SWIMMING = registry.register("swimming", () -> SwimmingCondition.CODEC);

	//player.inventory
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<EquipmentCondition>> EQUIPMENT = registry.register("equipment", () -> EquipmentCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<SlotsCondition>> SLOTS = registry.register("slots", () -> SlotsCondition.CODEC);
	
	//player.level
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<DifficultyCondition>> DIFFICULTY = registry.register("difficulty", () -> DifficultyCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<LightLevelCondition>> LIGHT_LEVEL = registry.register("light_level", () -> LightLevelCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<RegionalDifficultyCondition>> EFFECTIVE_DIFFICULTY = registry.register("regional_difficulty", () -> RegionalDifficultyCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<TimeCondition>> TIME = registry.register("time", () -> TimeCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<WeatherCondition>> WEATHER = registry.register("weather", () -> WeatherCondition.CODEC);

	//player.location
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<BiomeCondition>> BIOME = registry.register("biome", () -> BiomeCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<DimensionCondition>> DIMENSION = registry.register("dimension", () -> DimensionCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<DimensionTypeCondition>> DIMENSION_TYPE = registry.register("dimension_type", () -> DimensionTypeCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<HeightCondition>> HEIGHT = registry.register("height", () -> HeightCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<InFluidCondition>> IN_FLUID = registry.register("in_fluid", () -> InFluidCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<InShapeCondition>> IN_SHAPE = registry.register("in_shape", () -> InShapeCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<LastDeathPositionCondition>> LAST_DEATH_POSITION = registry.register("last_death_position", () -> LastDeathPositionCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<SeesSkyCondition>> SEES_SKY = registry.register("sees_sky", () -> SeesSkyCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<SpawnpointCondition>> SPAWNPOINT = registry.register("spawnpoint", () -> SpawnpointCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<StructureCondition>> STRUCTURE = registry.register("structure", () -> StructureCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<UnderwaterCondition>> UNDERWATER = registry.register("underwater", () -> UnderwaterCondition.CODEC);

	//player
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<CombatCondition>> COMBAT = registry.register("combat", () -> CombatCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<EntityCondition>> ENTITY_PREDICATE = registry.register("entity_predicate", () -> EntityCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<InGameCondition>> IN_GAME = registry.register("in_game", () -> InGameCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<MobEffectCondition>> MOB_EFFECT = registry.register("mob_effect", () -> MobEffectCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<RaidCondition>> RAID = registry.register("raid", () -> RaidCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<TeamCondition>> TEAM = registry.register("team", () -> TeamCondition.CODEC);

	//
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<PlayBossMusicCondition>> PLAY_BOSS_MUSIC_BGM = registry.register("play_boss_music", () -> PlayBossMusicCondition.CODEC);
	public static final DeferredHolder<MapCodec<? extends BGMProviderCondition>, MapCodec<VanillaBGMCondition>> VANILLA_BGM = registry.register("vanilla_bgm", () -> VanillaBGMCondition.CODEC);

	public static void init(IEventBus bus) {
		registry.register(bus);
		registry.addAlias(CustomBGMAPI.id("false"), NEVER.getId());
		registry.addAlias(CustomBGMAPI.id("true"), ALWAYS.getId());
		registry = null;
	}
}
