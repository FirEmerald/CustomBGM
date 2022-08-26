package com.firemerald.custombgm.client.audio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.fecore.data.ResourceLoader;
import com.firemerald.fecore.util.FECoreUtil;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class OggSound extends SoundBase
{
	private static final Set<ResourceLocation> CACHED = new HashSet<>();
	private static final Set<ResourceLocation> ERRORED = new HashSet<>();
	private static final String CACHE_DIR = "custombgmcache/";
	private static final File CACHE_FOLDER = new File(CACHE_DIR);

	static
	{
		CACHE_FOLDER.mkdirs();
		CACHE_FOLDER.deleteOnExit();
	}

	public static void clearCached()
	{
		CACHED.clear();
		ERRORED.clear();
	}

	private static String getOutputDir(ResourceLocation name)
	{
		return CACHE_DIR + name.getNamespace() + "/" + name.getPath();
	}

	private static void markDirForDeletion(File file)
	{
		Stack<File> stack = new Stack<>();
		File parent = file.getParentFile();
		while (!parent.equals(CACHE_FOLDER))
		{
			stack.add(parent);
			parent = parent.getParentFile();
		}
		while (!stack.isEmpty()) stack.pop().deleteOnExit();
	}

	public static void saveResource(ResourceLocation name) throws IOException
	{
		if (CACHED.contains(name)) return;
		String filename = OggSound.getOutputDir(name);
		File file = new File(filename);
		file.getParentFile().mkdirs();
		markDirForDeletion(file);
		file.deleteOnExit();
		try (InputStream in = ResourceLoader.getResource(name))
		{
			Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			throw e;
		}
		CACHED.add(name);
	}

	public static OggSound make(Sound sound, SoundSource category, boolean disablePan)
	{
		if (ERRORED.contains(sound.getLocation())) return null;
		int loopStart = ((IExtendedSound) sound).getLoopStart();
		int loopEnd = ((IExtendedSound) sound).getLoopEnd();
		try
		{
			return new OggSound(new ResourceLocation(sound.getLocation().getNamespace(), "sounds/" + sound.getLocation().getPath() + ".ogg"), category, loopStart, loopEnd, disablePan);
		}
		catch (Throwable t)
		{
			ERRORED.add(sound.getLocation());
			CustomBGMMod.LOGGER.error("Error grabbing sound loop", t);
			return null;
		}
	}

	public long decoder;
	public STBVorbisInfo info;
	public int loopStart = 0;
	public int loopEnd = -1;
	private int position;
	public final boolean disablePan;

	private OggSound(ResourceLocation resource, SoundSource category, int loopStart, int loopEnd, boolean disablePan)
	{
		super(FECoreUtil.getURLForResource(resource), category, "looping streamed OGG");
		try
		{
			saveResource(resource);
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Unable to cache " + resource, e);
		}
		IntBuffer err = IntBuffer.allocate(1);
		info = STBVorbisInfo.malloc();
		decoder = STBVorbis.stb_vorbis_open_filename(getOutputDir(resource), err, null);
		STBVorbis.stb_vorbis_get_info(decoder, info);
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        this.disablePan = disablePan;
	}

	public void loadData(int channels, int rate, ShortBuffer buffer)
	{
		buffer.limit(Math.max((loopEnd > 0 && ((position + rate) > loopEnd) ? (loopEnd - position) : rate), 0) * channels);
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
        ShortBuffer data1 = MemoryUtil.memAllocShort(rate * channels);
        loadData(channels, rate, data1);
        ShortBuffer data2 = MemoryUtil.memAllocShort(rate * channels);
        loadData(channels, rate, data2);
        ShortBuffer data3 = MemoryUtil.memAllocShort(rate * channels);
        loadData(channels, rate, data3);
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
        loadData(channels, rate, data1);
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
			        loadData(channels, rate, data1);
					if (data1.limit() == 0)
					{
						//MemoryUtil.memFree(data1);
						STBVorbis.stb_vorbis_seek(decoder, position = loopStart);
				        //data1 = MemoryUtil.memAllocShort(rate * channels);
				        loadData(channels, rate, data1);
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