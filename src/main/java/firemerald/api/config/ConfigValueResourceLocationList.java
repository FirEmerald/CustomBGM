package firemerald.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;

public class ConfigValueResourceLocationList extends ConfigValueBase
{
	public List<ResourceLocation> val;
	public final ResourceLocation[] defaultValue;

	public ConfigValueResourceLocationList(Category category, String name, List<ResourceLocation> defaultValue, String comment)
	{
		this(category, name, (ResourceLocation[]) defaultValue.toArray(), defaultValue, comment);
	}

	public ConfigValueResourceLocationList(Category category, String name, ResourceLocation[] defaultValue, String comment)
	{
		this(category, name, defaultValue, Arrays.asList(defaultValue), comment);
	}

	public ConfigValueResourceLocationList(Category category, String name, ResourceLocation[] defaultValue, List<ResourceLocation> defaultValueList, String comment)
	{
		super(category, name, comment + " [default: " + ConfigValueResourceLocationArray.makeString(defaultValue) + "]");
		this.val = new ArrayList<>(defaultValueList);
		this.defaultValue = defaultValue;
	}

	public Property getProperty()
	{
		return config.get(category, name, ConfigValueResourceLocationArray.toStringArray(defaultValue), comment);
	}

	@Override
	public void loadConfig()
	{
		val = fromStringArray(getProperty().getStringList());
	}

	@Override
	public void saveConfig()
	{
		getProperty().set(toStringArray(val));
	}

	public static String[] toStringArray(List<ResourceLocation> val)
	{
		String[] array = new String[val.size()];
		for (int i = 0; i < array.length; i++) array[i] = val.get(i).toString();
		return array;
	}

	public static List<ResourceLocation> fromStringArray(String[] array)
	{
		return Arrays.stream(array).map(ResourceLocation::new).collect(Collectors.toList());
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
			for (int i = 0; i < val.size(); i++) val.add(new ResourceLocation(list.getStringTagAt(i)));
		}
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		NBTTagList list = new NBTTagList();
		val.forEach(rl -> list.appendTag(new NBTTagString(rl.toString())));
		tag.setTag(name, list);
	}

	public void set(Collection<ResourceLocation> value)
	{
		val = new ArrayList<>(value);
	}
}