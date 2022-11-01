package com.firemerald.custombgm.client;

import java.util.List;

import javax.annotation.Nullable;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.ICustomMusic;
import com.firemerald.custombgm.api.ISoundLoop;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.audio.LoopingSounds;
import com.firemerald.custombgm.init.CustomBGMSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientState
{
	private static Music none = null;
	public static IPlayer clientPlayer = null;
	public static MusicHandler currentBGM = null;
	public static ResourceLocation currentBGMName = null;
	public static int menuMus = -1;

	public static Music musicNone()
	{
		if (none == null) none = new Music(CustomBGMSounds.EMPTY.get(), 0, 0, true);
		return none;
	}

	public static class MusicHandler implements ISoundLoop
	{
		private ISoundLoop oldMusic;
		public final ISoundLoop newMusic;
		private float transition;
		private static final float TRANSITION_PER_TICK = 1 / 40f;
		private float volume = 1;

		public MusicHandler(@Nullable MusicHandler oldMusic, @Nullable ISoundLoop newMusic)
		{
			this.oldMusic = oldMusic;
			this.newMusic = newMusic;
			if (newMusic != null) newMusic.setVolume(0);
			transition = 0;
		}

		@Override
		public void stopSound()
		{
			if (newMusic != null) newMusic.stopSound();
			if (oldMusic != null) oldMusic.stopSound();
		}

		public void instantStart()
		{
			if (oldMusic != null)
			{
				oldMusic.stopSound();
				oldMusic = null;
			}
			if (newMusic != null)
			{
				if (transition == 0)
				{
					newMusic.playSound();
				}
				newMusic.setVolume(volume);
			}
			transition = 1;
		}

		public void tick(boolean isRoot)
		{
			if (oldMusic instanceof MusicHandler) ((MusicHandler) oldMusic).tick(false);
			if (transition < 1)
			{
				if (transition == 0 && newMusic != null)
				{
					newMusic.playSound();
				}
				transition += TRANSITION_PER_TICK;
				if (transition >= 1)
				{
					if (oldMusic != null)
					{
						oldMusic.stopSound();
						oldMusic = null;
					}
					if (newMusic != null) newMusic.setVolume(volume);
				}
				else if (isRoot) this.setVolume(volume);
			}
		}

		@Override
		public void setVolume(float volume)
		{
			this.volume = volume;
			float oldVol = flattenedHann(transition);
			float newVol = 1 - oldVol;
			newVol = Mth.sqrt(newVol);
			oldVol = Mth.sqrt(oldVol);
			if (oldMusic != null) oldMusic.setVolume(oldVol * volume);
			if (newMusic != null) newMusic.setVolume(newVol * volume);
		}

		public static float cubic(float f)
		{
			return ((4 * f - 6) * f + 3) * f;
		}

		public static float flattenedHann(float f)
		{
			return (float) ((9 * Math.sin((f + 0.5) * Math.PI) / 16d) + (Math.sin((f + 0.5) * 3 * Math.PI) / 16d) + 0.5);
		}

		@Override
		public boolean isStopped()
		{
			return (newMusic == null || newMusic.isStopped()) && (oldMusic == null || oldMusic.isStopped());
		}

		@Override
		public void playSound()
		{
			if (newMusic != null) newMusic.playSound();
			if (oldMusic != null) oldMusic.playSound();
		}

		@Override
		public void pauseSound()
		{
			if (newMusic != null) newMusic.pauseSound();
			if (oldMusic != null) oldMusic.pauseSound();
		}

		@Override
		public void resumeSound()
		{
			if (newMusic != null) newMusic.resumeSound();
			if (oldMusic != null) oldMusic.resumeSound();
		}

		@Override
		public float getVolume()
		{
			return volume;
		}

		@Override
		public void updateCategoryVolume()
		{
			if (newMusic != null) newMusic.updateCategoryVolume();
			if (oldMusic != null) oldMusic.updateCategoryVolume();
		}

		@Override
		public SoundSource getCategory()
		{
			return newMusic != null ? newMusic.getCategory() : oldMusic != null ? oldMusic.getCategory() : SoundSource.MASTER;
		}

		@Override
		public void setPitch(float pitch)
		{
			if (newMusic != null) newMusic.setPitch(pitch);
			if (oldMusic != null) oldMusic.setPitch(pitch);
		}
	}

	@SuppressWarnings("resource")
	public static Music getCustomMusic(Music type, Minecraft mc)
	{
		if (!Minecraft.getInstance().getSoundManager().soundEngine.loaded) return type;
		if (currentBGM != null && currentBGM.isStopped())
		{
			currentBGM = null;
			currentBGMName = null;
			menuMus = -1;
		}
		ResourceLocation loopSound;
		if (mc.screen instanceof ICustomMusic)
		{
			loopSound = ((ICustomMusic) mc.screen).getMusic(mc.player, currentBGMName);
		}
		else if (type == Musics.CREDITS)
		{
			loopSound = null;
		}
		else
		{
			loopSound = ClientState.clientPlayer != null ? ClientState.clientPlayer.getMusicOverride() : null;
			if (loopSound == null)
			{
				boolean isMenu;
				if (type == Musics.MENU) isMenu = true;
				else
				{
					isMenu = false;
					menuMus = -1;
				}
				ConfigClient config = CustomBGMMod.CLIENT;
				List<String> titleMusic = config.titleMusic.get();
				if (isMenu && !titleMusic.isEmpty())
				{
					if (menuMus == -1) menuMus = (int) (Math.random() * titleMusic.size());
					loopSound = new ResourceLocation(titleMusic.get(menuMus));
				}
			}
		}
		if (loopSound == null) //is nothing
		{
			if (currentBGM != null && !currentBGM.isStopped()) currentBGM = new ClientState.MusicHandler(currentBGM, null); //go to nothing
		}
		else if (!loopSound.equals(currentBGMName)) //changed
		{
			currentBGM = new ClientState.MusicHandler(currentBGM, LoopingSounds.grabSound(loopSound, SoundSource.MUSIC, true)); //change
		}
		currentBGMName = loopSound;
		return loopSound == null ? type : musicNone();
	}
}