package com.firemerald.custombgm.providers.conditions.player.attributes;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class GameModeCondition implements BGMProviderPlayerCondition {
	public static final MapCodec<GameModeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			GameType.CODEC.fieldOf("mode").forGetter(condition -> condition.type),
			Codec.BOOL.optionalFieldOf("is_mode", true).forGetter(condition -> condition.isType)
			)
			.apply(instance, GameModeCondition::of)
	);

	private static final GameModeCondition[] IS_TYPE, IS_NOT_TYPE;

	static {
		GameType[] types = GameType.values();
		IS_TYPE = new GameModeCondition[types.length];
		IS_NOT_TYPE = new GameModeCondition[types.length];
		for (GameType type : GameType.values()) {
			IS_TYPE[type.ordinal()] = new GameModeCondition(type, true);
			IS_NOT_TYPE[type.ordinal()] = new GameModeCondition(type, false);
		}
	}

	public static GameModeCondition of(GameType type, boolean isType) {
		return (isType ? IS_TYPE : IS_NOT_TYPE)[type.ordinal()];
	}

	public static GameModeCondition of(GameType type) {
		return of(type, true);
	}

	public final GameType type;
	public final boolean isType;

	private GameModeCondition(GameType type, boolean isType) {
		this.type = type;
		this.isType = isType;
	}

	@Override
	public MapCodec<GameModeCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		GameType currentType = gameType(player);
		return currentType != null && ((currentType == type) == isType);
	}

	@SuppressWarnings("resource")
	public GameType gameType(Player player) {
		return player.level().isClientSide ? gameTypeClient() : player instanceof ServerPlayer serverPlayer ? gameTypeServer(serverPlayer) : null;
	}

	public GameType gameTypeServer(ServerPlayer player) {
		return player.gameMode.getGameModeForPlayer();
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public GameType gameTypeClient() {
		return Minecraft.getInstance().gameMode.getPlayerMode();
	}

	@Override
	public GameModeCondition simpleNot() {
		return of(type, !isType);
	}
}
