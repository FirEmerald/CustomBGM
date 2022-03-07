package firemerald.custombgm.init;

import firemerald.custombgm.plugin.Plugin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public enum LSSounds
{
	EMPTY("empty");

	public final SoundEvent sound;

	private LSSounds(String name)
	{
		ResourceLocation loc = new ResourceLocation(Plugin.MOD_ID, name);
		sound = new SoundEvent(loc).setRegistryName(loc);
	}

	public static void register(IForgeRegistry<SoundEvent> registry)
	{
		for (LSSounds sound : values()) registry.register(sound.sound);
	}
}