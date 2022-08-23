package com.firemerald.custombgm.client.audio;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import com.firemerald.fecore.util.FECoreUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class OggSound extends SoundBase
{
	public static String getOutputDir(ResourceLocation name)
	{
		return "custombgm_cache/" + name.getNamespace() + "/" + name.getPath();
	}
	
	public long decoder;
	public STBVorbisInfo info;
	public int loopStart = 0;
	public int loopEnd = -1;
	private int position;
	public final boolean disablePan;

	public OggSound(ResourceLocation resource, SoundSource category, int loopStart, int loopEnd, boolean disablePan)
	{
		super(FECoreUtil.getURLForResource(resource), category, "looping streamed OGG");
		IntBuffer err = IntBuffer.allocate(1);
		info = STBVorbisInfo.malloc();
		decoder = STBVorbis.stb_vorbis_open_filename(getOutputDir(resource), err, null);
		STBVorbis.stb_vorbis_get_info(decoder, info);
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        this.disablePan = disablePan;
	}
	
	public void loadData(int channels, int rate, int position, ShortBuffer buffer)
	{
		buffer.limit((position + rate > loopEnd ? loopEnd - position : rate) * channels);
		int loaded = STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, buffer);
		buffer.limit(loaded * channels);
		position += loaded;
	}

	@Override
	protected void play()
	{
		position = 0;
        int channels = info.channels();
        int rate = info.sample_rate();
        ShortBuffer data1 = MemoryUtil.memAllocShort(rate);
        loadData(channels, rate, position, data1);
        ShortBuffer data2 = MemoryUtil.memAllocShort(rate);
        loadData(channels, rate, position, data2);
        ShortBuffer data3 = MemoryUtil.memAllocShort(rate);
        loadData(channels, rate, position, data3);
		int format = channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
		int[] buffers = new int[0];
		int buffer1, buffer2, buffer3;
		if (data1.limit() > 0)
		{
			buffer1 = AL10.alGenBuffers();
			AL10.alBufferData(buffer1, format, data1, rate);
			AL10.alSourceQueueBuffers(src, buffer1);
			if (data2.limit() > 0)
			{
				buffer2 = AL10.alGenBuffers();
				AL10.alBufferData(buffer2, format, data2, rate);
				AL10.alSourceQueueBuffers(src, buffer2);
				if (data3.limit() > 0)
				{
					buffer3 = AL10.alGenBuffers();
					AL10.alBufferData(buffer3, format, data3, rate);
					AL10.alSourceQueueBuffers(src, buffer3);
					buffers = new int[] {buffer1, buffer2, buffer3};
				}
				else buffers = new int[] {buffer1, buffer2};
			}
			else buffers = new int[] {buffer1};
		}
        loadData(channels, rate, position, data1);
		if (!paused) AL10.alSourcePlay(src);
		while (!stop && AL10.alGetSourcei(src, AL10.AL_SOURCE_STATE) != AL10.AL_STOPPED)
		{
			if (data1.limit() != 0)
			{
				int p = AL10.alGetSourcei(src, AL10.AL_BUFFERS_PROCESSED);
				while (p != 0)
				{
					int b = AL10.alSourceUnqueueBuffers(src);
					AL10.alBufferData(b, format, data1, rate);
					AL10.alSourceQueueBuffers(src, b);
			        loadData(channels, rate, position, data1);
					if (data1.limit() == 0)
					{
						MemoryUtil.memFree(data1);
						STBVorbis.stb_vorbis_seek(decoder, position = loopStart);
				        data1 = MemoryUtil.memAllocShort(rate);
				        loadData(channels, rate, position, data1);
					}
			        p = AL10.alGetSourcei(src, AL10.AL_BUFFERS_PROCESSED);
				}
				try
				{
					Thread.sleep(20);
				} catch (InterruptedException e) {}
			}
		}
		AL10.alSourceStop(src);
		AL10.alDeleteSources(src);
		AL10.alDeleteBuffers(buffers);
		MemoryUtil.memFree(data1);
		MemoryUtil.memFree(data2);
		MemoryUtil.memFree(data3);
		STBVorbis.stb_vorbis_close(decoder);
		info.free();
		stopped = true;
	}
}