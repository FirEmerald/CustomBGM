package com.firemerald.custombgm.client;

import javax.annotation.Nullable;

import com.firemerald.custombgm.CustomBGM;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.client.audio.CustomBGMSoundInstance;
import com.firemerald.custombgm.config.ClientConfig;
import com.firemerald.custombgm.providers.OverrideResults;
import com.firemerald.custombgm.util.VolumedBGM;
import com.firemerald.fecore.distribution.DistributionUtil;
import com.firemerald.fecore.distribution.EmptyDistribution;
import com.firemerald.fecore.distribution.IDistribution;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BGMEngine {
	public static final RandomSource RANDOM = RandomSource.create();
	private static BGMInstance currentBGMInstance = null;
	private static VolumedBGM currentBGM = null, targetBGM = null;
	private static Sound targetSound = null;
	public static OverrideResults serverOverride = null;
	public static IDistribution<VolumedBGM> clientOverride = EmptyDistribution.get();
	private static int trackSkip;

	public static class BGMInstance {
		private final SoundManager soundManager;
		private BGMInstance previous;
		public final SoundInstance bgm;
		private float transition;
		private static final float TRANSITION_PER_TICK = 1 / 40f;
		private float volume = 1;

		public BGMInstance(@Nullable BGMInstance previous, @Nullable SoundInstance bgm) {
			soundManager = Minecraft.getInstance().getSoundManager();
			this.previous = previous;
			this.bgm = bgm;
			transition = 0;
		}

		public void stopSound() {
			if (bgm != null) soundManager.stop(bgm);
			if (previous != null) previous.stopSound();
		}

		public void instantStart() {
			if (previous != null) {
				previous.stopSound();
				previous = null;
			}
			if (bgm != null) {
				if (transition == 0) soundManager.play(bgm);
				soundManager.setVolume(bgm, volume);
			}
			transition = 1;
		}

		public boolean tick(boolean isRoot) {
			if (previous != null && !previous.tick(false)) {
				previous = null;
				if (bgm == null) return false;
			}
			if (transition < 1) {
				if (transition == 0 && bgm != null) soundManager.play(bgm);
				if (previous == null && transition == 0) {
					transition = 1;
					if (bgm != null) soundManager.setVolume(bgm, volume);
				} else {
					if ((transition += TRANSITION_PER_TICK) > 1) transition = 1;
					if (isRoot) updateVolume(1f);
				}
			}
			if (transition >= 1) {
				if (previous != null) {
					previous.stopSound();
					previous = null;
				}
				if (bgm != null) {
					soundManager.setVolume(bgm, volume);
					return soundManager.isActive(bgm) || previous != null;
				} else return false;
			}
			return true;
		}

		public void setVolume(float volume) {
			this.volume = volume;
			updateVolume(1f);
		}

		public void updateVolume(float volumeFromParent) {
			float prevVol = flattenedHann(transition);
			float curVol = 1 - prevVol;
			if (previous != null) previous.updateVolume(Mth.sqrt(prevVol) * volumeFromParent);
			if (bgm != null) soundManager.setVolume(bgm, Mth.sqrt(curVol) * volumeFromParent * this.volume);
		}

		public static float cubic(float f) {
			return ((4 * f - 6) * f + 3) * f;
		}

		public static float flattenedHann(float f) {
			return (float) ((9 * Math.sin((f + 0.5) * Math.PI) / 16d) + (Math.sin((f + 0.5) * 3 * Math.PI) / 16d) + 0.5);
		}
	}

	public static void setTarget(VolumedBGM bgm) {
		targetBGM = bgm;
		targetSound = null;
	}

	public static void setTarget(VolumedBGM bgm, Sound sound) {
		targetBGM = bgm;
		targetSound = sound;
	}

	public static boolean isPlaying() {
		return currentBGM != null;
	}

	public static boolean isPlaying(VolumedBGM bgm) {
		return currentBGM.equals(bgm);
	}

	public static boolean isPlaying(Sound sound, LoopType loop) {
		return currentBGMInstance != null && currentBGMInstance.bgm != null && currentBGM.loop() == loop && currentBGMInstance.bgm.getSound().getLocation().equals(sound.getLocation());
	}

	public static void nextTrack() {
		skip(1);
	}

	public static void previousTrack() {
		skip(-1);
	}

	public static void skip(int toSkip) {
		trackSkip += toSkip;
	}

	public static void randomTrack() {
		currentBGM = null;
	}

	public static void clientTick() {
		if (currentBGMInstance != null && !currentBGMInstance.tick(true)) {
			currentBGMInstance = null;
			if (currentBGM != null && currentBGM.loop() != LoopType.SHUFFLE) currentBGM = null;
		}
		if (clientOverride.isEmpty()) { //no tracks
			if (currentBGM != null && ClientConfig.logMusic) CustomBGM.LOGGER.info("CustomBGM stopping playback");
			currentBGM = null;
			trackSkip = 0;
			if (currentBGMInstance != null && currentBGMInstance.bgm != null) {
				currentBGMInstance = new BGMEngine.BGMInstance(currentBGMInstance, null); //go to nothing
			}
		} else {
			VolumedBGM newBGM;
			if (targetBGM != null) newBGM = clientOverride.pickOne(RANDOM, targetBGM::is, trackSkip);
			else if (currentBGM == null) newBGM = clientOverride.pickOne(RANDOM);
			else newBGM = clientOverride.pickOne(RANDOM, currentBGM::is, trackSkip);
			if (!newBGM.is(targetBGM)) {
				targetBGM = null;
				targetSound = null;
			} else if (targetSound != null && (newBGM == null || !newBGM.containsSound(targetSound))) targetSound = null;
			trackSkip = 0;
			if (currentBGMInstance == null || targetSound != null || targetBGM != null || !newBGM.is(currentBGM)) {
				CustomBGMSoundInstance instance;
				if (targetSound != null) instance = CustomBGMSoundInstance.of(
							newBGM.sound(),
							newBGM.loop() == LoopType.TRUE,
							targetSound);
				else instance = CustomBGMSoundInstance.of(
							newBGM.sound(),
							newBGM.loop() == LoopType.TRUE,
							SoundInstance.createUnseededRandom(),
							Minecraft.getInstance().getSoundManager());
				if (ClientConfig.logMusic) CustomBGM.LOGGER.info("CustomBGM now playing " + newBGM + " with sound " + instance.getSound());
				currentBGMInstance = new BGMEngine.BGMInstance(currentBGMInstance, instance); //change
			}
			currentBGM = newBGM;
			if (currentBGMInstance != null && currentBGM != null) currentBGMInstance.setVolume(currentBGM.volume());
		}
		targetBGM = null;
		targetSound = null;
	}

	public static boolean musicTick(MusicInfo musicInfo, Minecraft mc) {
		Player player;
		OverrideResults currentOverride;
		if (mc.screen instanceof WinScreen) { //fix for credits screen - act as if we are not on a server
			player = null;
			currentOverride = null;
		} else {
			player = mc.player;
			currentOverride = serverOverride;
		}
		PlayerConditionData playerData = new PlayerConditionData(player);
		playerData.setVanillaBGM(musicInfo);
		clientOverride = DistributionUtil.sortedMerge(ClientModEventHandler.getBGMProviders().getMusic(playerData, currentOverride).overrides().stream().map(dist ->
			dist.distribution().map(bgm -> new VolumedBGM(bgm, dist.volume()))
		));
		return !clientOverride.isEmpty();
	}
}