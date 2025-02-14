package com.firemerald.custombgm.providers.conditions.player.level;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;

public class DifficultyCondition implements BGMProviderPlayerCondition {
	public static final MapCodec<DifficultyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Difficulty.CODEC.fieldOf("difficulty").forGetter(condition -> condition.difficulty),
			Codec.BOOL.optionalFieldOf("is_difficulty", true).forGetter(condition -> condition.isDifficulty)
			)
			.apply(instance, DifficultyCondition::of)
	);

	private static final DifficultyCondition[] IS_TYPE, IS_NOT_TYPE;

	static {
		Difficulty[] types = Difficulty.values();
		IS_TYPE = new DifficultyCondition[types.length];
		IS_NOT_TYPE = new DifficultyCondition[types.length];
		for (Difficulty type : Difficulty.values()) {
			IS_TYPE[type.ordinal()] = new DifficultyCondition(type, true);
			IS_NOT_TYPE[type.ordinal()] = new DifficultyCondition(type, false);
		}
	}

	public static DifficultyCondition of(Difficulty difficulty, boolean isDifficulty) {
		return (isDifficulty ? IS_TYPE : IS_NOT_TYPE)[difficulty.ordinal()];
	}

	public static DifficultyCondition of(Difficulty difficulty) {
		return of(difficulty, true);
	}

	public final Difficulty difficulty;
	public final boolean isDifficulty;

	private DifficultyCondition(Difficulty difficulty, boolean isDifficulty) {
		this.difficulty = difficulty;
		this.isDifficulty = isDifficulty;
	}

	@Override
	public MapCodec<DifficultyCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return (player.level().getDifficulty() == difficulty) == isDifficulty;
	}
	
	@Override
	public DifficultyCondition simpleNot() {
		return of(difficulty, !isDifficulty);
	}
}
