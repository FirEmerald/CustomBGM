package com.firemerald.custombgm.providers;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.distribution.SingletonWeightedDistribution;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.Music;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;

public class ScreenMusicProvider extends BuiltInMusicProvider
{
	public static final MapCodec<ScreenMusicProvider> CODEC = getCodec(ScreenMusicProvider::new);

	protected ScreenMusicProvider(int priority, BGMProviderCondition condition, LoopType loop, float weight) {
		super(priority, condition, loop, weight);
	}

	protected ScreenMusicProvider(int priority, BGMProviderCondition condition, LoopType loop) {
		super(priority, condition, loop);
	}

	protected ScreenMusicProvider(int priority, BGMProviderCondition condition, float weight) {
		super(priority, condition, weight);
	}

	protected ScreenMusicProvider(int priority, BGMProviderCondition condition) {
		super(priority, condition);
	}

	@Override
	public BgmDistribution getMusic(PlayerConditionData player) {
		return EffectiveSide.get() == LogicalSide.CLIENT ? getMusicClient(player) : null;
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public BgmDistribution getMusicClient(PlayerConditionData player) {
		Screen screen = Minecraft.getInstance().screen;
		if (screen == null) return null;
		Music music = screen.getBackgroundMusic();
		return music == null || !condition.test(player) ? null : new BgmDistribution(new SingletonWeightedDistribution<>(new BGM(music, loop), weight), 1f);
	}

	@Override
	public MapCodec<ScreenMusicProvider> codec() {
		return CODEC;
	}

	public Builder derive() {
		return new Builder(this);
	}

	public static class Builder extends BuilderBase<ScreenMusicProvider, Builder> {
		public Builder() {
			super();
		}

		public Builder(ScreenMusicProvider derive) {
			super(derive);
		}

		@Override
		public ScreenMusicProvider build() {
			return new ScreenMusicProvider(priority, condition, loop, weight);
		}
	}
}