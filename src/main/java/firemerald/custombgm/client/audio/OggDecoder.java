package firemerald.custombgm.client.audio;

import java.net.URL;

import javax.sound.sampled.AudioFormat;

import paulscode.sound.SoundBuffer;
import paulscode.sound.codecs.CodecJOrbis;

public class OggDecoder
{
	public CodecJOrbis codec = new CodecJOrbis();
	public SoundBuffer buffer;
	public int samples = 0, realSamples = 0;
	boolean open = false;

	public void open(URL url)
	{
		codec.initialize(url);
		open = true;
		buffer = null;
		samples = realSamples = 0;
	}

	public AudioFormat getFormat()
	{
		if (buffer != null) return buffer.audioFormat;
		else return null;
	}

	public void close()
	{
		open = false;
		if (codec != null && codec.initialized()) codec.cleanup();
		buffer = null;
		samples = realSamples = 0;
	}

	public void readFrame()
	{
		if (open) try
		{
			buffer = codec.read();
			if (buffer != null) samples = (realSamples = buffer.audioData.length * 8 / buffer.audioFormat.getSampleSizeInBits()) / (buffer.audioFormat.getChannels());
			else samples = realSamples = 0;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void closeFrame()
	{
		if (open)
		{
			buffer = null;
			samples = realSamples = 0;
		}
	}
}