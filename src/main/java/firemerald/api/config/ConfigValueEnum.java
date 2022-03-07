package firemerald.api.config;

import java.util.StringJoiner;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigValueEnum<E extends Enum<E>> extends ConfigValueBase
{
	public E val;
	public final E defaultValue;
	public final E[] possibleValues;

	public ConfigValueEnum(Category category, String name, E defaultValue, E[] possibleValues, String comment)
	{
		super(category, name, comment + " [default: " + defaultValue.name() + ", possible values: " + getEnumList(possibleValues) + "]");
		this.val = this.defaultValue = defaultValue;
		this.possibleValues = possibleValues;
	}

	public Property getProperty(Configuration config)
	{
		return config.get(category, name, defaultValue.name(), comment);
	}

	public E getEnum()
	{
		Property p = getProperty(config);
		String name = p.getString();
		for (E value : possibleValues) if (value.name().equalsIgnoreCase(name)) return value;
		try
		{
			int index = Integer.parseInt(name);
			if (index >= 0 && index < possibleValues.length) return possibleValues[index];
		}
		catch (NumberFormatException e) {}
		p.set(defaultValue.name());
		return defaultValue;
	}

	public void setEnum(E value)
	{
		getProperty(config).set(value.name());
	}

	@Override
	public void loadConfig()
	{
		val = getEnum();
	}

	@Override
	public void saveConfig()
	{
		setEnum(val);
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		NBTBase val = tag.getTag(name);
		if (val == null || !(val instanceof NBTPrimitive || val instanceof NBTTagString)) this.val = this.defaultValue;
		else this.val = getEnum(tag.getString(name), possibleValues, defaultValue);
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		tag.setString(name, val.name());
	}
	
	public static String getEnumList(Enum<?>[] enums)
	{
		StringJoiner joiner = new StringJoiner(", ");
		for (Enum<?> e : enums) joiner.add(e.name());
		return joiner.toString();
	}
	
	public static <T extends Enum<?>> T getEnum(String name, T[] values, T _def)
	{
		for (T val : values) if (val.name().equalsIgnoreCase(name)) return val;
		try
		{
			int index = Integer.parseInt(name);
			if (index >= 0 && index < values.length) return values[index];
		}
		catch (NumberFormatException e) {}
		return _def;
	}
}