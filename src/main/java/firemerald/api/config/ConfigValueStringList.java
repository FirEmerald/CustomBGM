package firemerald.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Property;

public class ConfigValueStringList extends ConfigValueBase
{
	public List<String> val;
	public final String[] defaultValue;

	public ConfigValueStringList(Category category, String name, List<String> defaultValue, String comment)
	{
		this(category, name, (String[]) defaultValue.toArray(), defaultValue, comment);
	}

	public ConfigValueStringList(Category category, String name, String[] defaultValue, String comment)
	{
		this(category, name, defaultValue, Arrays.asList(defaultValue), comment);
	}

	public ConfigValueStringList(Category category, String name, String[] defaultValue, List<String> defaultValueList, String comment)
	{
		super(category, name, comment + " [default: " + ConfigValueStringArray.makeString(defaultValue) + "]");
		this.val = new ArrayList<>(defaultValueList);
		this.defaultValue = defaultValue;
	}

	public Property getProperty()
	{
		return config.get(category, name, defaultValue, comment);
	}

	@Override
	public void loadConfig()
	{
		val = Arrays.asList(getProperty().getStringList());
	}

	@Override
	public void saveConfig()
	{
		getProperty().set((String[]) val.toArray());
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		NBTTagList list = tag.getTagList(name, 8);
		if (list == null)
		{
			val = new ArrayList<>(Arrays.asList(defaultValue));
		}
		else
		{
			val = new ArrayList<>(list.tagCount());
			for (int i = 0; i < list.tagCount(); i++) val.add(list.getStringTagAt(i));
		}
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		NBTTagList list = new NBTTagList();
		val.forEach(str -> list.appendTag(new NBTTagString(str)));
		tag.setTag(name, list);
	}

	public void set(Collection<String> value)
	{
		val = new ArrayList<>(value);
	}
}