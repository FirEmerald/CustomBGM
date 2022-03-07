package firemerald.api.config;

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;

public class ConfigValueResourceLocationArray extends ConfigValueBase
{
	public ResourceLocation[] val;
	public final ResourceLocation[] defaultValue;

	public ConfigValueResourceLocationArray(Category category, String name, ResourceLocation[] defaultValue, String comment)
	{
		super(category, name, comment + " [default: " + makeString(defaultValue) + "]");
		this.val = this.defaultValue = defaultValue;
	}

	public Property getProperty()
	{
		return config.get(category, name, toStringArray(defaultValue), comment);
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

	public static String[] toStringArray(ResourceLocation[] val)
	{
		String[] array = new String[val.length];
		for (int i = 0; i < val.length; i++) array[i] = val[i].toString();
		return array;
	}

	public static ResourceLocation[] fromStringArray(String[] array)
	{
		ResourceLocation[] val = new ResourceLocation[array.length];
		for (int i = 0; i < array.length; i++) val[i] = new ResourceLocation(array[i]);
		return val;
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		NBTTagList list = tag.getTagList(name, 8);
		if (list == null)
		{
			val = new ResourceLocation[defaultValue.length];
			System.arraycopy(defaultValue, 0, val, 0, defaultValue.length);
		}
		else
		{
			val = new ResourceLocation[list.tagCount()];
			for (int i = 0; i < val.length; i++) val[i] = new ResourceLocation(list.getStringTagAt(i));
		}
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		NBTTagList list = new NBTTagList();
		for (ResourceLocation element : val)
			list.appendTag(new NBTTagString(element.toString()));
		tag.setTag(name, list);
	}

	public void set(Collection<ResourceLocation> value)
	{
		val = value.toArray(new ResourceLocation[value.size()]);
	}

	public static String makeString(ResourceLocation[] value)
	{
		if (value == null || value.length == 0) return "<>";
		else
		{
			StringBuilder builder = new StringBuilder("<\n");
			for (ResourceLocation str : value) builder.append("    ").append(str.toString()).append('\n');
			builder.append(" >");
			return builder.toString();
		}
	}
}