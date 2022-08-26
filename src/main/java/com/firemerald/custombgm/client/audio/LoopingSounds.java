package com.firemerald.custombgm.client.audio;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import com.firemerald.custombgm.CustomBGMMod;
import com.firemerald.custombgm.api.ISoundLoop;
import com.firemerald.fecore.data.AbstractElement;
import com.firemerald.fecore.data.FileUtil;
import com.firemerald.fecore.data.ResourceLoader;

public class LoopingSounds
{
	public static final Map<ResourceLocation, LoopingSoundInfo[]> SOUNDS = new HashMap<>();
	public static final List<ISoundLoop> PLAYING = new ArrayList<>();
	public static ImmutableList<String> allSoundNames = ImmutableList.of();

	public static void update()
	{
		for (ISoundLoop player : PLAYING.toArray(new ISoundLoop[0]))
		{
			if (player.isStopped()) PLAYING.remove(player);
			else player.updateCategoryVolume();
		}
	}

	public static void stopAll()
	{
		for (ISoundLoop player : PLAYING.toArray(new ISoundLoop[0])) player.stopSound();
		PLAYING.clear();
	}

	public static void pauseAll()
	{
		for (ISoundLoop player : PLAYING) player.pauseSound();
	}

	public static void resumeAll()
	{
		for (ISoundLoop player : PLAYING) player.resumeSound();
	}

	private static LoopingSoundInfo getRandomSound(ResourceLocation name)
	{
		LoopingSoundInfo[] infos = SOUNDS.get(name);
		if (infos == null || infos.length == 0) return null;
		else if (infos.length == 1) return infos[0];
		else return infos[(int) (Math.random() * infos.length)];
	}

	public static ISoundLoop playSound(ResourceLocation name, SoundSource category, boolean disablePan)
	{
		ISoundLoop loop = grabSound(name, category, disablePan);
		if (loop != null) loop.playSound();
		return loop;
	}

	private static final Set<ResourceLocation> INVALIDS = new HashSet<>();

	public static ISoundLoop grabSound(ResourceLocation name, SoundSource category, boolean disablePan)
	{
		if (INVALIDS.contains(name)) return null;
		LoopingSoundInfo info = getRandomSound(name);
		if (info == null)
		{
			INVALIDS.add(name);
			CustomBGMMod.LOGGER.error("could not grab empty or nonexistent sound: " + name);
			return null;
		}
		else
		{
			ISoundLoop player = info.getSound(category, disablePan);
			if (player == null) CustomBGMMod.LOGGER.error("could not grab missing loop resource: " + name + ": " + info.loc.toString());
			return player;
		}
	}

	public static void loadInfos()
	{
		INVALIDS.clear();
		SOUNDS.clear();
		ResourceLoader.getResources("loops.xml").forEach((domain, ress) -> ress.forEach(res -> {
			try
			{
				addInfos(res, domain);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			FileUtil.closeSafe(res);
		}));
		List<String> allSounds = SOUNDS.keySet().stream().map(Object::toString).collect(Collectors.toList());
		Collections.sort(allSounds);
		allSoundNames = ImmutableList.copyOf(allSounds);
	}

	public static void addInfos(InputStream in, String domain) throws IOException
	{
		try
		{
			AbstractElement root = FileUtil.readStream(in);
			ArrayList<LoopingSoundInfo> infos = new ArrayList<>();
			for (AbstractElement child : root.getChildren())
			{
				String name = child.getName();
				for (AbstractElement child2 : child.getChildren())
				{
					String loc = child2.getValue();
					int loopStart = 0, loopEnd = Integer.MAX_VALUE;
					if (child2.hasAttribute("start"))
					{
						try
						{
							loopStart = child2.getInt("start");
							if (loopStart < 0)
							{
								CustomBGMMod.LOGGER.error("invalid \"start\" attribute: must be positive, got " + loopStart);
								loopStart = 0;
							}
						}
						catch (Throwable t)
						{
							CustomBGMMod.LOGGER.error("invalid \"start\" attribute", t);
						}
					}
					if (child2.hasAttribute("end"))
					{
						try
						{
							loopEnd = child2.getInt("end");
							if (loopEnd < 0)
							{
								CustomBGMMod.LOGGER.error("invalid \"end\" attribute: must be positive, got " + loopEnd);
								loopEnd = Integer.MAX_VALUE;
							}
						}
						catch (Throwable t)
						{
							CustomBGMMod.LOGGER.error("invalid \"end\" attribute", t);
						}
					}
					try
					{
						String extension = FileUtil.getExtension(loc);
						FileFormat format;
						if (extension.length() == 0)
						{
							format = FileFormat.OGG;
							loc = loc + ".ogg";
						}
						else format = FileFormat.getFormat(extension);
						ResourceLocation res = new ResourceLocation(domain, "loops/" + loc);
						if (format.canAddSound(res, child2)) infos.add(new LoopingSoundInfo(res, loopStart, loopEnd, format));
						else CustomBGMMod.LOGGER.warn("Sound format " + format + " is unable to load resource " + res + ", it will not be registered");
					}
					catch (Exception e3)
					{
						CustomBGMMod.LOGGER.error("invalid sound file: " + loc, e3);
					}
				}
				SOUNDS.put(new ResourceLocation(domain, name), infos.toArray(new LoopingSoundInfo[infos.size()]));
				infos.clear();
			}
		}
		catch (IOException e1)
		{
			throw new IOException("Unable to read XML document, or not valid XML", e1);
		}
	}

	public static class LoopingSoundInfo
	{
		public final int loopStart, loopEnd;
		public final ResourceLocation loc;
		public final FileFormat format;

		public LoopingSoundInfo(ResourceLocation loc, int loopStart, int loopEnd, FileFormat format)
		{
			this.loc = loc;
			this.loopStart = loopStart;
			this.loopEnd = loopEnd;
			this.format = format;
		}

		public ISoundLoop getSound(SoundSource category, boolean disablePan)
		{
			return format.getPlayer(loc, loopStart, loopEnd, category, disablePan);
		}
	}

	public static enum FileFormat
	{
		OGG("ogg") {
			@Override
			public ISoundLoop getPlayer(ResourceLocation loc, int loopStart, int loopEnd, SoundSource category, boolean disablePan)
			{
				try
				{
					return new OggSound(loc, category, loopStart, loopEnd, disablePan);
				}
				catch (Throwable t)
				{
					CustomBGMMod.LOGGER.warn("Unable to play OGG sound" + loc, t);
					return null;
				}
			}
			
			@Override
			public boolean canAddSound(ResourceLocation sound, AbstractElement fromElement)
			{
				if (!CustomBGMMod.cacheAll() && !fromElement.getBoolean("cacheFile", false)) return true;
				else try
				{
					OggSound.saveResource(sound);
					return true;
				}
				catch (IOException e)
				{
					CustomBGMMod.LOGGER.error("Unable to cache " + sound + ", this loop is unavailable", e);
					return false;
				}
			}
		};

		public final String[] extensions;

		FileFormat(String... extensions)
		{
			this.extensions = extensions;
		}
		
		public abstract boolean canAddSound(ResourceLocation sound, AbstractElement fromElement);

		public abstract ISoundLoop getPlayer(ResourceLocation loc, int loopStart, int loopEnd, SoundSource category, boolean disablePan);

		public static FileFormat getFormat(String extension) throws Exception
		{
			for (FileFormat format : values()) for (String ext : format.extensions) if (ext.equalsIgnoreCase(extension)) return format;
			throw new Exception("Unsupported sound file extension: " + extension);
		}
	}
}