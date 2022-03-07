package firemerald.api.config;

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Property;

public class ConfigValueStringArray extends ConfigValueBase
{
	public String[] val;
	public final String[] defaultValue;

	public ConfigValueStringArray(Category category, String name, String[] defaultValue, String comment)
	{
		super(category, name, comment + " [default: " + makeString(defaultValue) + "]");
		this.val = this.defaultValue = defaultValue;
	}

	public Property getProperty()
	{
		return config.get(category, name, defaultValue, comment);
	}

	@Override
	public void loadConfig()
	{
		val = getProperty().getStringList();
	}

	@Override
	public void saveConfig()
	{
		getProperty().set(val);
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		NBTTagList list = tag.getTagList(name, 8);
		if (list == null)
		{
			val = new String[defaultValue.length];
			System.arraycopy(defaultValue, 0, val, 0, defaultValue.length);
		}
		else
		{
			val = new String[list.tagCount()];
			for (int i = 0; i < val.length; i++) val[i] = list.getStringTagAt(i);
		}
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		NBTTagList list = new NBTTagList();
		for (String element : val)
			list.appendTag(new NBTTagString(element));
		tag.setTag(name, list);
	}

	public void set(Collection<String> value)
	{
		val = value.toArray(new String[value.size()]);
	}

	public static String makeString(String[] value)
	{
		if (value == null || value.length == 0) return "<>";
		else
		{
			StringBuilder builder = new StringBuilder("<\n");
			for (String str : value) builder.append("    ").append(str).append('\n');
			builder.append(" >");
			return builder.toString();
		}
	}
}