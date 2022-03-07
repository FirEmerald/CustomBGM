package firemerald.api.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class Config
{
	public final Configuration config;
	public final List<Category> categories = new ArrayList<>();

	public Config(ResourceLocation file)
	{
		this(file.getResourceDomain() + "/" + file.getResourcePath());
	}

	public Config(String file)
	{
		config = new Configuration(new File("config/" + file));
	}

	public void loadConfig()
	{
		config.load();
		categories.forEach(element -> element.loadConfig());
		onLoaded();
		saveConfig();
	}

	public void saveConfig()
	{
		categories.forEach(element -> element.saveConfig());
		config.save();
	}

	public void loadNBT(NBTTagCompound tag)
	{
		categories.forEach(element -> element.loadNBT(tag));
		onLoaded();
	}

	public NBTTagCompound saveNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		categories.forEach(element -> element.saveNBT(tag));
		return tag;
	}

	public void onLoaded() {}
}