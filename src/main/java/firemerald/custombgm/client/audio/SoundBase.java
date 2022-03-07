package firemerald.custombgm.client.audio;

import java.net.URL;

import org.lwjgl.openal.AL10;

import firemerald.custombgm.api.ISoundLoop;
import net.minecraft.util.SoundCategory;

public abstract class SoundBase extends Thread implements ISoundLoop
{
	protected boolean stop = false;
	protected boolean paused = false;
	protected boolean stopped = false;
	public final int src;
	public final URL url;
	public final SoundCategory category;
	public float volume = 1;
	public float pitch = 1;

	public SoundBase(URL url, SoundCategory category, String name)
	{
		this.url = url;
		src = AL10.alGenSources();
    	AL10.alSourcef(src, AL10.AL_ROLLOFF_FACTOR, 0);
        this.category = category;
        this.updateCategoryVolume();
        this.setName(name + ": " + url.toString());
	}

	@Override
	public void playSound()
	{
		LoopingSounds.PLAYING.add(this);
		start();
	}

	@Override
	public void stopSound()
	{
		stop = true;
		AL10.alSourceStop(src);
		LoopingSounds.PLAYING.remove(this);
	}

	@Override
	public boolean isStopped()
	{
		return stopped;
	}

	@Override
	public void pauseSound()
	{
		if (!paused)
		{
			paused = true;
			AL10.alSourcePause(src);
		}
	}

	@Override
	public void resumeSound()
	{
		if (paused)
		{
			paused = false;
			AL10.alSourcePlay(src);
		}
	}

	@Override
	public void run()
	{
		play();
	}

	protected abstract void play();

	@Override
	public void setVolume(float volume)
	{
		AL10.alSourcef(src, AL10.AL_GAIN, (this.volume = volume) * ISoundLoop.getCategoryVolume(category));
	}

	@Override
	public void updateCategoryVolume()
	{
		AL10.alSourcef(src, AL10.AL_GAIN, volume * ISoundLoop.getCategoryVolume(category));
	}

	@Override
	public float getVolume()
	{
		return volume;
	}

	@Override
	public SoundCategory getCategory()
	{
		return category;
	}

	@Override
	public void setPitch(float pitch)
	{
		AL10.alSourcef(src, AL10.AL_PITCH, this.pitch = pitch);
	}
}