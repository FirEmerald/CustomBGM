package firemerald.custombgm.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nullable;

import firemerald.custombgm.api.ICustomMusic;
import firemerald.custombgm.api.IPlayer;
import firemerald.custombgm.api.ISoundLoop;
import firemerald.custombgm.client.audio.LoopingSounds;
import firemerald.custombgm.init.LSSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public class ClientState
{
	public static final MusicType MUSIC_TYPE_EMPTY = EnumHelper.addEnum(MusicTicker.MusicType.class, "empty", new Class[] { SoundEvent.class, Integer.TYPE, Integer.TYPE }, new Object[] { LSSounds.EMPTY.sound, 0, 0 });
	public static final Queue<Runnable> QUEUED_ACTIONS = new ConcurrentLinkedQueue<>();
	public static IPlayer clientPlayer = null;
	public static MusicHandler currentBGM = null;
	public static ResourceLocation currentBGMName = null;
	public static int menuMus = -1;

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
			newVol = MathHelper.sqrt(newVol);
			oldVol = MathHelper.sqrt(oldVol);
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
		public SoundCategory getCategory()
		{
			return newMusic != null ? newMusic.getCategory() : oldMusic != null ? oldMusic.getCategory() : SoundCategory.MASTER;
		}

		@Override
		public void setPitch(float pitch)
		{
			if (newMusic != null) newMusic.setPitch(pitch);
			if (oldMusic != null) oldMusic.setPitch(pitch);
		}
	}

	public static MusicType getCustomMusic(MusicType type, Minecraft mc)
	{
		if (!SoundSystem.initialized()) return type;
		if (currentBGM != null && currentBGM.isStopped())
		{
			currentBGM = null;
			currentBGMName = null;
			menuMus = -1;
		}
		ResourceLocation loopSound;
		if (mc.currentScreen instanceof ICustomMusic)
		{
			loopSound = ((ICustomMusic) mc.currentScreen).getMusic(mc.player, currentBGMName);
		}
		else
		{
			loopSound = ClientState.clientPlayer != null ? ClientState.clientPlayer.getMusicOverride() : null;
			if (loopSound == null)
			{
				boolean isMenu;
				if (type == MusicType.MENU) isMenu = true;
				else
				{
					isMenu = false;
					menuMus = -1;
				}
				ConfigClientOptions config = ConfigClientOptions.INSTANCE;
				if (isMenu && config.titleMusic.val.length > 0)
				{
					if (menuMus == -1) menuMus = (int) (Math.random() * config.titleMusic.val.length);
					loopSound = config.titleMusic.val[menuMus];
				}
			}
		}
		if (loopSound == null) //is nothing
		{
			if (currentBGM != null && !currentBGM.isStopped()) currentBGM = new ClientState.MusicHandler(currentBGM, null); //go to nothing
		}
		else if (!loopSound.equals(currentBGMName)) //changed
		{
			currentBGM = new ClientState.MusicHandler(currentBGM, LoopingSounds.grabSound(loopSound, SoundCategory.MUSIC, true)); //change
		}
		currentBGMName = loopSound;
		return loopSound == null ? type : MUSIC_TYPE_EMPTY;
	}
}