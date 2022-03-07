package firemerald.custombgm.client.audio;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import firemerald.custombgm.client.ConfigClientOptions;
import net.minecraft.util.SoundCategory;

public class OggSound extends SoundBase
{
	public final OggDecoder decoder;
	public int loopStart = 0;
	public int loopEnd = -1;
	public final boolean disablePan;

	public OggSound(URL url, SoundCategory category, int loopStart, int loopEnd, boolean disablePan)
	{
		super(url, category, "looping streamed OGG");
		this.decoder = new OggDecoder();
		decoder.open(url);
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        this.disablePan = disablePan;
	}

	@Override
	protected void play()
	{
		Queue<Integer> freeBuffers = new ConcurrentLinkedQueue<>();
		int numBuffers = ConfigClientOptions.INSTANCE.preloadedBuffers.val;
		IntBuffer buffers = BufferUtils.createIntBuffer(numBuffers);
		AL10.alGenBuffers(buffers);
		for (int i = 0; i < numBuffers; i++) freeBuffers.add(buffers.get(i));
		int sample = 0;
		while (!stop)
		{
			while (!freeBuffers.isEmpty())
			{
				int prevSample = sample;
		        int start, length;
		        decoder.readFrame();
		        if (sample >= loopEnd || decoder.samples == 0/* || decoder.buffer == null not needed as a null buffer results in a sample count of 0*/)
		        {
			        sample = 0;
					decoder.open(url);
					do
					{
			        	prevSample = sample;
				        decoder.readFrame();
			        	sample += decoder.samples;
					}
			        while (sample < loopStart);
					start = (loopStart - prevSample);
		        }
		        else
		        {
		        	start = 0;
		        	sample += decoder.samples;
		        }
		        if (sample > loopEnd) length = loopEnd - (prevSample + start);
		        else length = decoder.samples - start;
		        int channels = decoder.getFormat().getChannels();
		        int rate = (int) decoder.getFormat().getSampleRate();
		        int size = decoder.getFormat().getSampleSizeInBits();
		        int format = (disablePan || channels > 1) ? (size == 16 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_STEREO8) : (size == 16 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_MONO8);
		        int b = freeBuffers.poll();
		        start *= (size / 8) * channels;
		        length *= (size / 8) * channels;
		        ByteBuffer data;
		        if (channels == 1 && disablePan)
		        {
			        data = BufferUtils.createByteBuffer(length * 2);
			        int end = start + length;
			        if (size == 16) for (int i = start; i < end; i += 2)
			        {
			        	data.put(decoder.buffer.audioData[i]);
			        	data.put(decoder.buffer.audioData[i + 1]);
			        	data.put(decoder.buffer.audioData[i]);
			        	data.put(decoder.buffer.audioData[i + 1]);
			        }
			        else for (int i = start; i < end; i++)
			        {
			        	data.put(decoder.buffer.audioData[i]);
			        	data.put(decoder.buffer.audioData[i]);
			        }
			        data.flip();
		        }
		        else
		        {
			        data = BufferUtils.createByteBuffer(length);
			        data.put(decoder.buffer.audioData, start, length);
			        data.flip();
		        }
				AL10.alBufferData(b, format, data, rate);
				AL10.alSourceQueueBuffers(src, b);
				if (!paused && AL10.alGetSourcei(src, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) AL10.alSourcePlay(src);
			}
			if (!paused && AL10.alGetSourcei(src, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) AL10.alSourcePlay(src);
			for (int p = AL10.alGetSourcei(src, AL10.AL_BUFFERS_PROCESSED); p > 0; p--)
			{
				int b = AL10.alSourceUnqueueBuffers(src);
				freeBuffers.add(b);
			}
			try
			{
				Thread.sleep(20);
			} catch (InterruptedException e) {}
		}
		AL10.alSourceStop(src);
		AL10.alDeleteSources(src);
		AL10.alDeleteBuffers(buffers);
		decoder.close();
		stopped = true;
	}
}