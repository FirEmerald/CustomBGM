package firemerald.custombgm.client;

import firemerald.api.config.Category;
import firemerald.api.config.Config;
import firemerald.api.config.ConfigValueInt;
import firemerald.api.config.ConfigValueResourceLocationArray;
import net.minecraft.util.ResourceLocation;

public class ConfigClientOptions extends Config
{
	public static final ConfigClientOptions INSTANCE = new ConfigClientOptions();

	public final ConfigValueInt preloadedBuffers;
	public final ConfigValueResourceLocationArray titleMusic;

	public ConfigClientOptions()
	{
		super("custombgm.cfg");
		Category audio = new Category(this, "audio", "Audio options");
		titleMusic = new ConfigValueResourceLocationArray(audio, "title_music", new ResourceLocation[] {
				}, "A list of loops to override the default menu music.");
		preloadedBuffers = new ConfigValueInt(audio, "preloaded_buffers", 10, 2, 20, "maximum number of sound buffers a looped player can queue before waiting - higher numbers reduce/eliminate stuttering for a small RAM cost.");
	}
}