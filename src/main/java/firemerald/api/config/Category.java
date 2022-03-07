package firemerald.api.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public class Category implements IConfigElement
{
	public final String name, comment;
	public final Configuration config;
	public final List<IConfigElement> values = new ArrayList<>();

	public Category(Config config, String name, String comment)
	{
		this.config = config.config;
		this.name = name;
		this.comment = comment;
		config.categories.add(this);
	}

	@Override
	public void loadConfig()
	{
		config.addCustomCategoryComment(name, comment);
		values.forEach(element -> element.loadConfig());
	}

	@Override
	public void saveConfig()
	{
		values.forEach(element -> element.saveConfig());
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		NBTTagCompound t;
		if (tag.hasKey(name, 10)) t = tag.getCompoundTag(name);
		else tag.setTag(name, t = new NBTTagCompound());
		values.forEach(element -> element.loadNBT(t));
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		NBTTagCompound t = new NBTTagCompound();
		tag.setTag(name, t);
		values.forEach(element -> element.saveNBT(t));
	}
}