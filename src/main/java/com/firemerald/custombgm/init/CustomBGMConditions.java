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
import com.firemerald.custombgm.providers.conditions.player.level.LightLevelCondition;
import com.firemerald.custombgm.providers.conditions.player.level.RegionalDifficultyCondition;
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

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class CustomBGMConditions {
	private static DeferredRegister<MapCodec<? extends BGMProviderCondition>> registry = DeferredRegister.create(CustomBGMRegistries.Keys.CONDITION_CODECS, CustomBGMAPI.MOD_ID);

	static {
		CustomBGMRegistries.conditionCodecs = registry.makeRegistry(() -> RegistryBuilder.of(CustomBGMRegistries.Keys.CONDITION_CODECS.location()));
	}

	//constant
	public static final RegistryObject<MapCodec<FalseCondition>> NEVER = registry.register("never", () -> FalseCondition.CODEC);
	public static final RegistryObject<MapCodec<FalseCondition>> FALSE = registry.register("false", () -> FalseCondition.CODEC2);
	public static final RegistryObject<MapCodec<TrueCondition>> ALWAYS = registry.register("always", () -> TrueCondition.CODEC);
	public static final RegistryObject<MapCodec<TrueCondition>> TRUE = registry.register("true", () -> TrueCondition.CODEC2);

	//modifier
	public static final RegistryObject<MapCodec<AndCondition>> AND = registry.register("and", () -> AndCondition.CODEC);
	public static final RegistryObject<MapCodec<NandCondition>> NAND = registry.register("nand", () -> NandCondition.CODEC);
	public static final RegistryObject<MapCodec<NorCondition>> NOR = registry.register("nor", () -> NorCondition.CODEC);
	public static final RegistryObject<MapCodec<NotCondition>> NOT = registry.register("not", () -> NotCondition.CODEC);
	public static final RegistryObject<MapCodec<OrCondition>> OR = registry.register("or", () -> OrCondition.CODEC);
	public static final RegistryObject<MapCodec<XnorCondition>> XNOR = registry.register("xnor", () -> XnorCondition.CODEC);
	public static final RegistryObject<MapCodec<XorCondition>> XOR = registry.register("xor", () -> XorCondition.CODEC);

	//player.attributes
	public static final RegistryObject<MapCodec<BreathCondition>> BREATH = registry.register("breath", () -> BreathCondition.CODEC);
	public static final RegistryObject<MapCodec<CrouchingCondition>> CROUCHING = registry.register("crouching", () -> CrouchingCondition.CODEC);
	public static final RegistryObject<MapCodec<FlyingCondition>> FLYING = registry.register("flying", () -> FlyingCondition.CODEC);
	public static final RegistryObject<MapCodec<GameModeCondition>> GAME_MODE = registry.register("game_mode", () -> GameModeCondition.CODEC);
	public static final RegistryObject<MapCodec<HealthCondition>> HEALTH = registry.register("health", () -> HealthCondition.CODEC);
	public static final RegistryObject<MapCodec<OnFireCondition>> ON_FIRE = registry.register("on_fire", () -> OnFireCondition.CODEC);
	public static final RegistryObject<MapCodec<ScaleCondition>> SCALE = registry.register("scale", () -> ScaleCondition.CODEC);
	public static final RegistryObject<MapCodec<SprintingCondition>> SPRINTING = registry.register("sprinting", () -> SprintingCondition.CODEC);
	public static final RegistryObject<MapCodec<StatisticCondition>> STATS = registry.register("stats", () -> StatisticCondition.CODEC);
	public static final RegistryObject<MapCodec<SwimmingCondition>> SWIMMING = registry.register("swimming", () -> SwimmingCondition.CODEC);

	//player.inventory
	public static final RegistryObject<MapCodec<EquipmentCondition>> EQUIPMENT = registry.register("equipment", () -> EquipmentCondition.CODEC);
	public static final RegistryObject<MapCodec<SlotsCondition>> SLOTS = registry.register("slots", () -> SlotsCondition.CODEC);

	//player.level
	public static final RegistryObject<MapCodec<DifficultyCondition>> DIFFICULTY = registry.register("difficulty", () -> DifficultyCondition.CODEC);
	public static final RegistryObject<MapCodec<LightLevelCondition>> LIGHT_LEVEL = registry.register("light_level", () -> LightLevelCondition.CODEC);
	public static final RegistryObject<MapCodec<RegionalDifficultyCondition>> EFFECTIVE_DIFFICULTY = registry.register("regional_difficulty", () -> RegionalDifficultyCondition.CODEC);
	public static final RegistryObject<MapCodec<TimeCondition>> TIME = registry.register("time", () -> TimeCondition.CODEC);
	public static final RegistryObject<MapCodec<WeatherCondition>> WEATHER = registry.register("weather", () -> WeatherCondition.CODEC);

	//player.location
	public static final RegistryObject<MapCodec<BiomeCondition>> BIOME = registry.register("biome", () -> BiomeCondition.CODEC);
	public static final RegistryObject<MapCodec<DimensionCondition>> DIMENSION = registry.register("dimension", () -> DimensionCondition.CODEC);
	public static final RegistryObject<MapCodec<DimensionTypeCondition>> DIMENSION_TYPE = registry.register("dimension_type", () -> DimensionTypeCondition.CODEC);
	public static final RegistryObject<MapCodec<HeightCondition>> HEIGHT = registry.register("height", () -> HeightCondition.CODEC);
	public static final RegistryObject<MapCodec<InFluidCondition>> IN_FLUID = registry.register("in_fluid", () -> InFluidCondition.CODEC);
	public static final RegistryObject<MapCodec<InShapeCondition>> IN_SHAPE = registry.register("in_shape", () -> InShapeCondition.CODEC);
	public static final RegistryObject<MapCodec<LastDeathPositionCondition>> LAST_DEATH_POSITION = registry.register("last_death_position", () -> LastDeathPositionCondition.CODEC);
	public static final RegistryObject<MapCodec<SeesSkyCondition>> SEES_SKY = registry.register("sees_sky", () -> SeesSkyCondition.CODEC);
	public static final RegistryObject<MapCodec<SpawnpointCondition>> SPAWNPOINT = registry.register("spawnpoint", () -> SpawnpointCondition.CODEC);
	public static final RegistryObject<MapCodec<StructureCondition>> STRUCTURE = registry.register("structure", () -> StructureCondition.CODEC);
	public static final RegistryObject<MapCodec<UnderwaterCondition>> UNDERWATER = registry.register("underwater", () -> UnderwaterCondition.CODEC);

	//player
	public static final RegistryObject<MapCodec<CombatCondition>> COMBAT = registry.register("combat", () -> CombatCondition.CODEC);
	public static final RegistryObject<MapCodec<EntityCondition>> ENTITY_PREDICATE = registry.register("entity_predicate", () -> EntityCondition.CODEC);
	public static final RegistryObject<MapCodec<InGameCondition>> IN_GAME = registry.register("in_game", () -> InGameCondition.CODEC);
	public static final RegistryObject<MapCodec<MobEffectCondition>> MOB_EFFECT = registry.register("mob_effect", () -> MobEffectCondition.CODEC);
	public static final RegistryObject<MapCodec<RaidCondition>> RAID = registry.register("raid", () -> RaidCondition.CODEC);
	public static final RegistryObject<MapCodec<TeamCondition>> TEAM = registry.register("team", () -> TeamCondition.CODEC);

	//
	public static final RegistryObject<MapCodec<PlayBossMusicCondition>> PLAY_BOSS_MUSIC_BGM = registry.register("play_boss_music", () -> PlayBossMusicCondition.CODEC);
	public static final RegistryObject<MapCodec<VanillaBGMCondition>> VANILLA_BGM = registry.register("vanilla_bgm", () -> VanillaBGMCondition.CODEC);

	public static void init(IEventBus bus) {
		registry.register(bus);
		registry = null;
	}
}
