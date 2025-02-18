package com.firemerald.custombgm.providers.conditions;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;

public class PlayBossMusicCondition implements BGMProviderCondition {
	public static final MapCodec<PlayBossMusicCondition> CODEC = Codec.BOOL.optionalFieldOf("play_music", true).xmap(PlayBossMusicCondition::of, condition -> condition.playBossMusic);

	public static final PlayBossMusicCondition TRUE = new PlayBossMusicCondition(true), FALSE = new PlayBossMusicCondition(false);

	public static PlayBossMusicCondition of(boolean playBossMusic) {
		return playBossMusic ? TRUE : FALSE;
	}

	public final boolean playBossMusic;

	private PlayBossMusicCondition(boolean playBossMusic) {
		this.playBossMusic = playBossMusic;
	}

	@Override
	public boolean test(PlayerConditionData t) {
		return EffectiveSide.get() == LogicalSide.CLIENT && getClient();
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public boolean getClient() {
		return Minecraft.getInstance().gui.getBossOverlay().shouldPlayMusic() == playBossMusic;
	}

	@Override
	public MapCodec<PlayBossMusicCondition> codec() {
		return CODEC;
	}

	@Override
	public PlayBossMusicCondition not() {
		return of(!playBossMusic);
	}
}