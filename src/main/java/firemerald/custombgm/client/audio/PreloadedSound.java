package firemerald.custombgm.client.audio;

import java.net.URL;

import org.lwjgl.openal.AL10;

import net.minecraft.util.SoundCategory;

public class PreloadedSound extends SoundBase
{
	public final int introBuffer, loopBuffer;

	public PreloadedSound(URL url, int introBuffer, int loopBuffer, SoundCategory category) throws Exception
	{
		super(url, category, "looping preloaded");
        this.introBuffer = introBuffer;
        this.loopBuffer = loopBuffer;
	}

	@Override
	protected void play()
	{
		AL10.alSourceQueueBuffers(src, introBuffer);
		AL10.alSourceQueueBuffers(src, loopBuffer);
		AL10.alSourceQueueBuffers(src, loopBuffer);
		AL10.alSourceQueueBuffers(src, loopBuffer);
		while (!stop)
		{
			if (!paused && AL10.alGetSourcei(src, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) AL10.alSourcePlay(src);
			for (int p = AL10.alGetSourcei(src, AL10.AL_BUFFERS_PROCESSED); p > 0; p--) AL10.alSourceQueueBuffers(src, loopBuffer);
			try
			{
				Thread.sleep(20);
			} catch (InterruptedException e) {}
		}
		AL10.alSourceStop(src);
		AL10.alDeleteSources(src);
		stopped = true;
	}
}